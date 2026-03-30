package com.groupkekulandara.services;

import com.groupkekulandara.config.DbConfig;
import com.groupkekulandara.models.VendorProfile;
import com.groupkekulandara.repository.VendorRepository;
import jakarta.persistence.EntityManager;

import java.util.List;

public class VendorService {

    private VendorRepository repository = new VendorRepository();

    public VendorProfile getProfile(long userId) {
        return repository.findVendorInId(userId);
    }

    public boolean updateProfile(long vendorId, String name, String description) {
        EntityManager em = DbConfig.getEntityManagerFactory().createEntityManager();
        VendorProfile vendor = em.find(VendorProfile.class,vendorId);
        if (vendor != null) {
            vendor.setBusinessName(name);
            vendor.setDescription(description);
            repository.update(vendor);
            return true;
        }
        return false;
    }

    public List<VendorProfile> getAllVendors() {
        return repository.findAll();
    }
}
