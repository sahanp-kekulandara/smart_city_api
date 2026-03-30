package com.groupkekulandara.services;

import com.groupkekulandara.config.DbConfig;
import com.groupkekulandara.dto.UserResponseDTO;
import com.groupkekulandara.models.User;
import com.groupkekulandara.models.UserRole;
import com.groupkekulandara.models.UserStatus;
import com.groupkekulandara.models.UserVerification;
import com.groupkekulandara.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import com.groupkekulandara.util.EmailSender;
import com.groupkekulandara.util.VerificationCode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;

public class UserService {

    UserRepository userRepository = new UserRepository();

    public UserResponseDTO userSignIn(User user) {
        User result = userRepository.userSignIn(user);

        if (result != null) {
            return new UserResponseDTO(true, "Sign in successful", result.getId(), result);
        }
        return null;
    }

    public UserResponseDTO processFullRegistration(User user) {
        EntityManager em = DbConfig.getEntityManagerFactory().createEntityManager();
        EntityTransaction et = em.getTransaction();

        try {
            et.begin();

            // 1. Check Email
            if (userRepository.findByEmail(user.getEmail()) != null) {
                return new UserResponseDTO(false, "This email address already exists.");
            }

            // 2. Set Default Status (PENDING)
//            UserStatus pendingStatus = em.find(UserStatus.class, 1L); // Assuming ID 1 is PENDING
            UserStatus pendingStatus = new UserStatusService().getPendingStatus();
            user.setUserStatus(pendingStatus);

            UserRole userRole = new UserRoleService().getUser();
            user.setUserRole(userRole);

            // 3. Save User
            User managedUser = em.merge(user);

            // 4. Create Verification Code
            UserVerification uv = new UserVerification();
            uv.setUser(managedUser);
            uv.setVerificationCode(VerificationCode.generateVerificationCode()); // Your random logic
            uv.setVerificationCodeExpire(VerificationCode.getExpiryTimestamp()); // Your +15 mins logic
            em.persist(uv);

            // 5. Commit EVERYTHING at once
            et.commit();

            // 6. Trigger Email here in a background thread
            new Thread(() -> {
                EmailSender.sendVerificationEmail(managedUser.getEmail(), uv.getVerificationCode());
            }).start();

            return new UserResponseDTO(true, "Registration successful", managedUser.getId());

        } catch (Exception e) {
            if (et.isActive()) et.rollback(); // If anything fails, nothing is saved!
            e.printStackTrace();
            return new UserResponseDTO(false, "Server error occurred.");
        } finally {
            em.close();
        }
    }

    public UserResponseDTO verifyUser(String verificationCode, long userId) {
        EntityManager em = DbConfig.getEntityManagerFactory().createEntityManager();
        EntityTransaction et = em.getTransaction();

        try {
            // 1. Find the record (Logic stays the same)
            UserVerification uv = userRepository.findByCodeAndUser(em, verificationCode, userId);


//  ADD THIS CHECK:
            if (uv == null) {
                return new UserResponseDTO(false, "Invalid verification code or User ID.");
            }

// 2. Now it is safe to check Expiry
            Timestamp now = new Timestamp(System.currentTimeMillis());
            if (uv.getVerificationCodeExpire().before(now)) {
                return new UserResponseDTO(false, "Verification code has expired.");
            }

            // 2. Check Expiry (Fixed spelling of 'isBefore')
            if (uv.getVerificationCodeExpire().before(new Timestamp(System.currentTimeMillis()))) {
                return new UserResponseDTO(false, "Verification code has expired.");
            }

            et.begin();

            // 3. Update Status
            User user = uv.getUser();
            UserStatus activeStatus = em.find(UserStatus.class, 2L); // 2 = Active
            user.setUserStatus(activeStatus);
            user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            em.merge(user);

            // 4. Clean up
            em.remove(uv);

            et.commit();
            return new UserResponseDTO(true, "Account verified successfully!");

        } catch (NoResultException e) {
            return new UserResponseDTO(false, "Invalid verification code.");
        } catch (Exception e) {
            if (et.isActive()) et.rollback();
            return new UserResponseDTO(false, "Server error: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    public UserResponseDTO registerFinal(long userId, String fName, String lName, String mobile) {
        EntityManager em = DbConfig.getEntityManagerFactory().createEntityManager();
        EntityTransaction et = em.getTransaction();

        try {
            // 1. Check if mobile exists for a DIFFERENT user
            User existingUserWithMobile = userRepository.findByMobile(em, mobile);
            if (existingUserWithMobile != null && existingUserWithMobile.getId() != userId) {
                return new UserResponseDTO(false, "This mobile number is already in use.");
            }

            et.begin();

            // 2. Find the current user by ID
            User user = em.find(User.class, userId);
            if (user == null) {
                return new UserResponseDTO(false, "User not found.");
            }

            // 3. Update fields
            user.setFirstName(fName);
            user.setLastName(lName);
            user.setPhoneNumber(mobile);

            em.merge(user);
            et.commit();

            return new UserResponseDTO(true, "Profile updated successfully!", user.getId(), user);

        } catch (Exception e) {
            if (et.isActive()) et.rollback();
            return new UserResponseDTO(false, "Error: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    // This tells Windows: "Go to this specific folder on the hard drive"
    private static final String PROJECT_ROOT = "C:\\Users\\ADMIN\\IdeaProjects\\AndroidProjectAPI";

    private static final String UPLOAD_DIR = PROJECT_ROOT + File.separator +
            "src" + File.separator +
            "main" + File.separator +
            "webapp" + File.separator +
            "uploads" + File.separator +
            "profiles" + File.separator;

    public User handleProfileUpdate(long id, String fn, String ln, String ph, InputStream is, String fileName) {
        String nameToSaveInDb = null;

        if (is != null && fileName != null) {
            try {
                File dir = new File(UPLOAD_DIR);
                if (!dir.exists()) dir.mkdirs();

                // Create a unique filename
                String finalFileName = id + "_" + System.currentTimeMillis() + "_" + fileName;
                File file = new File(dir, finalFileName);

                Files.copy(is, file.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // ONLY save the filename in the DB, not the whole C:/ path!
                nameToSaveInDb = finalFileName;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return userRepository.updateUserDetails(id, fn, ln, ph, nameToSaveInDb);
    }

    public User authenticate(String email, String password) {
        // You could add BCrypt password checking here if you had time
        return userRepository.login(email, password);
    }
}