package com.groupkekulandara.repository;

import com.groupkekulandara.config.DbConfig;
import com.groupkekulandara.models.OrderItem;
import jakarta.persistence.EntityManager;

import java.util.List;

public class OrderItemRepository {

    public List<OrderItem> findItemsByOrderId(Long orderId) {
        EntityManager em = DbConfig.getEntityManagerFactory().createEntityManager();
        return em.createQuery("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId", OrderItem.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }
}