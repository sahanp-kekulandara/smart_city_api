package com.groupkekulandara.repository;

import com.groupkekulandara.config.DbConfig;
import com.groupkekulandara.models.UserRole;
import com.groupkekulandara.models.UserStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class UserRoleRepository {

    public UserRole findByName(String userRole) {
        EntityManager em = DbConfig.getEntityManagerFactory().createEntityManager();
        try {
            // Using JPQL to find the status object by its string name
            TypedQuery<UserRole> query = em.createQuery(
                    "SELECT r FROM UserRole r WHERE r.role = :name", UserRole.class);
            query.setParameter("name", userRole);

            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }
}
