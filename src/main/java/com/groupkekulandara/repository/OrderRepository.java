package com.groupkekulandara.repository;

import com.groupkekulandara.config.DbConfig;
import com.groupkekulandara.models.Order;
import com.groupkekulandara.models.OrderStatus;
import com.groupkekulandara.services.OrderStatusService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

public class OrderRepository {

    public List<Order> findOrdersByUserId(long userId) {
        EntityManager em = DbConfig.getEntityManagerFactory().createEntityManager();
        try {
            // Using JOIN FETCH to get Shipping and Status immediately
            TypedQuery<Order> query = em.createQuery(
                    "SELECT o FROM Order o " +
                            "JOIN FETCH o.orderStatus " +
                            "JOIN FETCH o.orderShipping " +
                            "WHERE o.user.id = :userId ORDER BY o.createdAt DESC", Order.class);

            query.setParameter("userId", userId);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    public boolean updateStatus(Long orderId, String newStatus) {
        EntityManager em = DbConfig.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            // 1. Find the existing Order
            Order order = em.find(Order.class, orderId);

            if (order != null) {
                // 2. Update the status inside the OrderStatus object
                // (Assuming Order has a @OneToOne or @ManyToOne relationship with OrderStatus)

                OrderStatus status = new OrderStatusRepository().findByName(newStatus);
                order.setOrderStatus(status);

                em.merge(order);
                em.getTransaction().commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            return false;
        } finally {
            em.close();
        }
    }

    public List<Order> findByVendorId(Long vendorId) {
        EntityManager em = DbConfig.getEntityManagerFactory().createEntityManager();
        try {
            // We join OrderItem -> Order
            //            OrderItem -> Product -> Product -> Vendor -> =userId
            return em.createQuery("SELECT DISTINCT o FROM OrderItem oi " +
                    "JOIN oi.order o " +
                    "JOIN oi.product p " +
                    "JOIN p.vendor v WHERE v.user.id= :id",Order.class)
                    .setParameter("id",vendorId).getResultList();
        } finally {
            em.close();
        }
    }
}