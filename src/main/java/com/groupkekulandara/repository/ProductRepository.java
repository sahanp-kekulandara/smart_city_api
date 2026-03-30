package com.groupkekulandara.repository;

import com.groupkekulandara.config.DbConfig;
import com.groupkekulandara.models.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

public class ProductRepository {

    public List<Product> allProducts() {
        EntityManager em = DbConfig.getEntityManagerFactory().createEntityManager();

        try {
            // 1. Create the JPQL Query
            // Professional JPQL to get Category and Vendor in one trip to the database
            TypedQuery<Product> query = em.createQuery(
                    "SELECT p FROM Product p JOIN FETCH p.category JOIN FETCH p.vendor",
                    Product.class
            );
            // 2. Execute and get the list
            return query.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>(); // Return empty list instead of null to prevent Android crashes
        } finally {
            // 3. Always close the EntityManager!
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public List<Product> getNewArrivals() {
        EntityManager em = DbConfig.getEntityManagerFactory().createEntityManager();
        // Sort by ID descending so the newest entries come first
        return em.createQuery("SELECT p FROM Product p ORDER BY p.id DESC", Product.class)
                .setMaxResults(10) // This ensures only 10 are sent to Android
                .getResultList();
    }

    public List<Product> searchByName(String name) {
        EntityManager em = DbConfig.getEntityManagerFactory().createEntityManager();
        try {
            // Added JOIN FETCH so Category and Vendor data is included in the search results
            return em.createQuery(
                            "SELECT p FROM Product p " +
                                    "JOIN FETCH p.category " +
                                    "JOIN FETCH p.vendor " +
                                    "WHERE LOWER(p.name) LIKE LOWER(:name)", Product.class)
                    .setParameter("name", "%" + name + "%")
                    .getResultList();
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public Product getById(int id) {
        EntityManager em = DbConfig.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Product p WHERE p.id = :id", Product.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }


    public Product save(Product product) {
        EntityManager em = DbConfig.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            // FIXED: Check for both null and 0 to be safe
            if (product.getId() == null || product.getId() == 0) {
                // This tells Hibernate: "This is a new row, use AUTO_INCREMENT"
                em.persist(product);
            } else {
                // This tells Hibernate: "Find this ID in the DB and update the columns"
                product = em.merge(product);
            }

            transaction.commit();
            return product;
        } catch (Exception e) {
            if (transaction.isActive()) transaction.rollback();
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public List<Product> findByVendorId(long vendorId) {
        EntityManager em = DbConfig.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Product p WHERE p.vendor.id = :vId", Product.class)
                    .setParameter("vId", vendorId)
                    .getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public Product updateStatus(long productId, String statusName) {
        EntityManager em = DbConfig.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        Product product = null;

        try {
            transaction.begin();

            // 1. Find the product
            product = em.find(Product.class, productId);

            if (product != null) {
                // 2. Find the Status entity by name (e.g., "ACTIVE")
                ProductStatus newStatus = em.createQuery(
                                "SELECT s FROM ProductStatus s WHERE s.status = :status", ProductStatus.class)
                        .setParameter("status", statusName)
                        .getSingleResult();

                // 3. Update the product reference
                product.setStatus(newStatus);
                em.merge(product);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) transaction.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
        return product;
    }
}

