package com.groupkekulandara.config;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class DbConfig {
    // "myPU" must match the name in persistence.xml
    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("myPU");

    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }
}