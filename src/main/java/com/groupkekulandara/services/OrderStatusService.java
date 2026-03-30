package com.groupkekulandara.services;

import com.groupkekulandara.models.OrderStatus;
import com.groupkekulandara.repository.OrderStatusRepository;

public class OrderStatusService {
    OrderStatusRepository statusRepository = new OrderStatusRepository();

    public OrderStatus getPendingStatus() {
        return statusRepository.findByName("PENDING");
    }

    public OrderStatus getConfirmedStatus() {
        return statusRepository.findByName("CONFIRMED");
    }

    public OrderStatus getShippedStatus() {
        return statusRepository.findByName("SHIPPED");
    }

    public OrderStatus getDeliveredStatus() {
        return statusRepository.findByName("DELIVERED");
    }

    public OrderStatus getCancelledStatus() {
        return statusRepository.findByName("CANCELLED");
    }
}
