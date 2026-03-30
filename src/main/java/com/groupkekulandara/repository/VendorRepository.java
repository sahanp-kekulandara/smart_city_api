package com.groupkekulandara.repository;

import com.groupkekulandara.config.DbConfig;
import com.groupkekulandara.models.OrderStatus;
import com.groupkekulandara.models.VendorProfile;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class VendorRepository {

    public VendorProfile findVendorInId(long userId){

        EntityManager em = DbConfig.getEntityManagerFactory().createEntityManager();
        try {
           return em.createQuery("SELECT vp FROM VendorProfile vp WHERE vp.user.id =:userId",VendorProfile.class)
                    .setParameter("userId",userId).getSingleResult();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public void update(VendorProfile vendor) {
        EntityManager em = DbConfig.getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        em.merge(vendor);
        em.getTransaction().commit();
    }

    public List<VendorProfile> findAll() {
            EntityManager em = DbConfig.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT v FROM VendorProfile v", VendorProfile.class).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }


}
