package com.groupkekulandara.repository;


import com.groupkekulandara.models.UserStatus;
import com.groupkekulandara.config.DbConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class UserStatusRepository {

    public UserStatus findByName(String statusName) {
        EntityManager em = DbConfig.getEntityManagerFactory().createEntityManager();
        try {
            // Using JPQL to find the status object by its string name
            TypedQuery<UserStatus> query = em.createQuery(
                    "SELECT s FROM UserStatus s WHERE s.status = :name", UserStatus.class);
            query.setParameter("name", statusName);

            // Returns the object found in the DB (e.g., ID: 1, Name: PENDING)
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }
}