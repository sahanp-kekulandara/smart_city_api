package com.groupkekulandara.repository;

import com.groupkekulandara.config.DbConfig;
import com.groupkekulandara.models.User;
import com.groupkekulandara.models.UserStatus;
import com.groupkekulandara.models.UserVerification;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import org.hibernate.Transaction;

import java.time.LocalDateTime;

public class UserRepository {

    public User findByEmail(String email) {
        EntityManager em = DbConfig.getEntityManagerFactory().createEntityManager();
        try {
            // Use JPQL because email is not the @Id primary key
            return em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            return null; // Return null if email doesn't exist (Good for Registration check)
        } finally {
            em.close();
        }
    }

    // You can move the findVerification code here to keep UserService "thin"
    public UserVerification findByCodeAndUser(EntityManager em, String code, long uid) {
        try {
            return em.createQuery(
                            "SELECT u FROM UserVerification u WHERE u.verificationCode = :code AND u.user.id = :uid",
                            UserVerification.class)
                    .setParameter("code", code)
                    .setParameter("uid", uid)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public User findByMobile(EntityManager em, String mobile) {
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.phoneNumber = :mobile", User.class)
                    .setParameter("mobile", mobile)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public User updateUserDetails(long userId, String fName, String lName, String phone, String imagePath) {
        EntityManager em = DbConfig.getEntityManagerFactory().createEntityManager();
        User user = null;

        try {
            em.getTransaction().begin();

            // JPA find
            user = em.find(User.class, userId);

            if (user != null) {
                // JPA tracks changes automatically once the object is managed
                user.setFirstName(fName);
                user.setLastName(lName);
                user.setPhoneNumber(phone);

                if (imagePath != null) {
                    user.setProfilePicUrl(imagePath);
                }
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
        return user;
    }

    public User userSignIn(User user) {
        EntityManager em = DbConfig.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.email = :email AND u.password = :password", User.class)
                    .setParameter("email", user.getEmail())
                    .setParameter("password", user.getPassword())
                    .getSingleResult();
        } catch (NoResultException e) {
            return null; // User not found
        } finally {
            em.close();
        }
    }

    public User login(String email, String password) {
        EntityManager em = DbConfig.getEntityManagerFactory().createEntityManager();
        try {
            // Corrected syntax: added spaces, fixed aliases, and added '=' for the ID
            return em.createQuery(
                            "SELECT u FROM User u " +
                                    "JOIN FETCH u.userRole ur " +
                                    "WHERE u.email = :email " +
                                    "AND u.password = :password " +
                                    "AND ur.id = :urId", User.class)
                    .setParameter("email", email)
                    .setParameter("password", password)
                    .setParameter("urId", 1) // 1 = Admin Role ID
                    .getSingleResult();
        } catch (NoResultException e) {
            return null; // No admin user found with those credentials
        } catch (Exception e) {
            e.printStackTrace(); // Log other potential database errors
            return null;
        } finally {
            em.close();
        }
    }
}
