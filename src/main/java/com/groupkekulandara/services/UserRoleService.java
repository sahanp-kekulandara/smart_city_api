package com.groupkekulandara.services;

import com.groupkekulandara.models.UserRole;
import com.groupkekulandara.models.UserStatus;
import com.groupkekulandara.repository.UserRoleRepository;

public class UserRoleService {
    private UserRoleRepository userRoleRepository = new UserRoleRepository();

    public UserRole getAdmin() {
        return userRoleRepository.findByName("ADMIN");
    }

    public UserRole getUser() {
        return userRoleRepository.findByName("USER");
    }
    public UserRole getVendor() {
        return userRoleRepository.findByName("VENDOR");
    }
}
