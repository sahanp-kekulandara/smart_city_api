package com.groupkekulandara.services;

import com.groupkekulandara.config.DbConfig;
import com.groupkekulandara.models.*;
import com.groupkekulandara.repository.AdminRepository;
import com.groupkekulandara.repository.VendorRepository;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminService {
    private final AdminRepository adminRepository = new AdminRepository();

    public Map<String, Object> getDashboardOverview() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalVendors", adminRepository.countVendors());
        stats.put("totalUsers", adminRepository.countUsers());
        stats.put("activeOrders", adminRepository.countActiveOrders());

        // Revenue calculation
        double revenue = adminRepository.getTotalRevenue();
        stats.put("revenue", "Rs. " + String.format("%.2f", revenue)); // Formats to 2 decimal places

        // Real chart data
        stats.put("weeklyOrders", adminRepository.getWeeklyOrderData());

        return stats;
    }

    public List<Map<String, Object>> getAllVendorsList() {
        // We convert the Entity objects to a Map for easy JSON consumption
        List<VendorProfile> vendors = adminRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (VendorProfile v : vendors) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", v.getId());
            map.put("name", v.getBusinessName());
            map.put("email", v.getUser().getEmail());
            map.put("phone", v.getPhoneNumber());
            map.put("is_verified", v.getIsVerified()); // This is your boolean
            result.add(map);
        }
        return result;
    }

    public boolean verifyVendorStatus(int id) {
        return adminRepository.updateVerification(id);
    }

    public List<User> getAllUsersForDropdown() {
            List<User> users = adminRepository.findAllUsers();
//            List<Map<String, Object>> userList = new ArrayList<>();

//            for (User u : users) {
//                Map<String, Object> map = new HashMap<>();
//                map.put("id", u.getId());
//                map.put("name",u.getFirstName() + " " + u.getLastName());
//                map.put("email", u.getEmail());
//                userList.add(map);
//            }
            return users;
    }

    public String validateAndSaveVendor(Map<String, Object> data) {
        // Create the EntityManager locally
        EntityManager em = DbConfig.getEntityManagerFactory().createEntityManager();

        try {
            // 1. Backend Validation
            if (data.get("name") == null || data.get("name").toString().trim().length() < 3) {
                return "Invalid Business Name";
            }

            int userId = Integer.parseInt(data.get("userId").toString());

            // 2. Check if User exists
            User user = em.find(User.class, userId);
            if (user == null) return "Selected User does not exist";

            // 3. Duplicate Check (Check the field name in your Entity! v.userId or v.user.id)
            Long existingCount = (Long) em.createQuery(
                            "SELECT COUNT(v) FROM VendorProfile v WHERE v.user.id = :uid")
                    .setParameter("uid", userId)
                    .getSingleResult();

            if (existingCount > 0) {
                return "ALREADY_EXISTS";
            }

            // 4. Mapping Data
            VendorProfile vp = new VendorProfile();
            vp.setUser(user);
            vp.setBusinessName(data.get("name").toString());
            vp.setPhoneNumber(data.get("phone").toString());
            vp.setDescription(data.get("description").toString());
            vp.setLatitude(Double.parseDouble(data.get("latitude").toString()));
            vp.setLongitude(Double.parseDouble(data.get("longitude").toString()));
            vp.setIsVerified((short) 0);

            // 5. Save using the Repository
            if (adminRepository.saveVendor(vp)) {
                return "SUCCESS";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Internal Error: " + e.getMessage();
        } finally {
            // CRITICAL: Always close the EntityManager to prevent DB lockups
            if (em.isOpen()) {
                em.close();
            }
        }
        return "Database Error";
    }

    public String changeUserStatus(int userId, int status) {
        // 1. Logic Validation
        if (status != 2 && status != 3) {
            return "INVALID_STATUS";
        }

        // 2. Call Repo
        boolean success = adminRepository.updateUserStatus(userId, status);
        return success ? "SUCCESS" : "USER_NOT_FOUND";
    }

    public List<Product> getAllProducts() {
        return adminRepository.findAllProducts();
    }

    public List<Category> getAllCategories() {
        return adminRepository.findAllCategories();
    }

    public boolean addCategory(String name) {
        if (name == null || name.trim().isEmpty()) return false;
        Category cat = new Category();
        cat.setName(name);
        return adminRepository.saveCategory(cat);
    }

    public List<OrderItem> getAllOrders() {
        return adminRepository.findAllOrdersWithDetails();
    }

}
