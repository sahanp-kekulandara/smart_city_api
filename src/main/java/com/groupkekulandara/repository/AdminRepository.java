package com.groupkekulandara.repository;

import com.groupkekulandara.config.DbConfig;
import com.groupkekulandara.models.*;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.Arrays;
import java.util.List;

public class AdminRepository {

    private final EntityManager em = DbConfig.getEntityManagerFactory().createEntityManager();

    public long countVendors() {
        return (long) em.createQuery("SELECT COUNT(v) FROM VendorProfile v").getSingleResult();
    }

    public long countUsers() {
        return (long) em.createQuery("SELECT COUNT(u) FROM User u").getSingleResult();
    }

    public long countActiveOrders() {
        return (long) em.createQuery("SELECT COUNT(o) FROM Order o").getSingleResult();
    }

    // 1. Calculate Total Revenue
    public double getTotalRevenue() {
        Double revenue = (Double) em.createQuery(
                        "SELECT SUM(o.totalAmount) FROM Order o WHERE o.orderStatus.status = 'COMPLETED'")
                .getSingleResult();
        return (revenue != null) ? revenue : 0.0;
    }

    public List<Long> getWeeklyOrderData() {

        List<Long> counts = em.createQuery("SELECT COUNT(o) FROM Order o GROUP BY o.createdAt ORDER BY o.createdAt DESC", Long.class)
                .setMaxResults(7)
                .getResultList();

        if (counts.isEmpty()) return Arrays.asList(0L, 0L, 0L, 0L, 0L, 0L, 0L);
        return counts;
    }

    public List<VendorProfile> findAll() {
        return em.createQuery("SELECT v FROM VendorProfile v", VendorProfile.class).getResultList();
    }

    public boolean updateVerification(int id) {
        try {
            em.getTransaction().begin(); // 1. Start the transaction

            VendorProfile v = em.find(VendorProfile.class, id);
            if (v != null) {
                v.setIsVerified((short) 1);
                em.merge(v);

                em.getTransaction().commit(); // 2. SAVE the changes to DB
                return true;
            }

            em.getTransaction().rollback(); // Rollback if vendor not found
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback(); // 3. Safety: Rollback on error
            }
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveVendor(VendorProfile vendor) {
        try {
            em.getTransaction().begin();
            em.persist(vendor);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            return false;
        }
    }

    public List<User> findAllUsers() {
        EntityManager em = DbConfig.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u", User.class).getResultList();
        } finally {
            em.close();
        }
    }

    public boolean updateUserStatus(int userId, int status) {
        EntityManager em = DbConfig.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            // Use a direct HQL update for efficiency
            int updatedCount = em.createQuery(
                            "UPDATE User u SET u.userStatus.id = :status WHERE u.id = :id")
                    .setParameter("status", status)
                    .setParameter("id", userId)
                    .executeUpdate();

            em.getTransaction().commit();
            return updatedCount > 0;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    // Fetch all products with their categories
    public List<Product> findAllProducts() {
        EntityManager em = DbConfig.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Product p JOIN FETCH p.category", Product.class).getResultList();
        } finally {
            em.close();
        }
    }

    // Fetch all categories for the manager modal
    public List<Category> findAllCategories() {
        EntityManager em = DbConfig.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT c FROM Category c", Category.class).getResultList();
        } finally {
            em.close();
        }
    }

    // Save a new category
    public boolean saveCategory(Category category) {
        EntityManager em = DbConfig.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(category);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    public List<OrderItem> findAllOrdersWithDetails() {
        EntityManager em = DbConfig.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT oi FROM OrderItem oi " +
                    "JOIN FETCH oi.order o " +
                    "JOIN FETCH oi.product p ", OrderItem.class).getResultList();
        } finally {
            em.close();
        }
    }
}