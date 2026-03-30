package com.groupkekulandara.repository;

import com.groupkekulandara.config.DbConfig;
import com.groupkekulandara.models.OrderStatus;
import com.groupkekulandara.models.UserStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class OrderStatusRepository {

    public OrderStatus findByName(String statusName) {
        EntityManager em = DbConfig.getEntityManagerFactory().createEntityManager();
        try {
            // Using JPQL to find the status object by its string name
            TypedQuery<OrderStatus> query = em.createQuery(
                    "SELECT s FROM OrderStatus s WHERE s.status = :name", OrderStatus.class);
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
