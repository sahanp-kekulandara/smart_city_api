package com.groupkekulandara.services;

import com.groupkekulandara.models.UserStatus;
import com.groupkekulandara.repository.UserStatusRepository;

public class UserStatusService {

    private final UserStatusRepository statusRepository = new UserStatusRepository();

    public UserStatus getPendingStatus() {
        return statusRepository.findByName("PENDING");
    }
    public UserStatus getVerifiedStatus() {
        return statusRepository.findByName("VERIFIED");
    }
    public UserStatus getBlockedStatus() {
        return statusRepository.findByName("BLOCKED");
    }
}