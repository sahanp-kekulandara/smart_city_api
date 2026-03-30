package com.groupkekulandara.repository;

import com.groupkekulandara.config.DbConfig;
import com.groupkekulandara.models.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {

    public List<Category> allCategories() {
        EntityManager em = DbConfig.getEntityManagerFactory().createEntityManager();

        try {
            // 1. Create the JPQL Query
            TypedQuery<Category> query = em.createQuery("SELECT c FROM Category c", Category.class);

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

}
