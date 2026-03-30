package com.groupkekulandara.services;

import com.groupkekulandara.config.DbConfig;
import com.groupkekulandara.dto.CartItemDTO;
import com.groupkekulandara.dto.OrderDTO;
import com.groupkekulandara.dto.OrderRequest;
import com.groupkekulandara.models.*;
import com.groupkekulandara.repository.OrderItemRepository;
import com.groupkekulandara.repository.OrderRepository;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OrderService {

    private final OrderRepository orderRepository = new OrderRepository();
    private final OrderItemRepository orderItemRepository = new OrderItemRepository();

    public List<OrderItem> getItemsByOrderId(Long orderId) {
        return orderItemRepository.findItemsByOrderId(orderId);
    }

    public boolean saveCompleteOrder(OrderRequest dto) { // Changed void to boolean
        EntityManager em = DbConfig.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            // 1. Shipping
            OrderShipping shipping = new OrderShipping();
            shipping.setLatitude(dto.getLatitude());
            shipping.setLongitude(dto.getLongitude());
            shipping.setAddressName("Home");
            shipping.setFormattedAddress(dto.getAddress());
            em.persist(shipping);

            // 2. Main Order
            Order order = new Order();
            User user = em.find(User.class, dto.getUserId());

            // Ensure you have this status in your DB (e.g., ID 1)
            OrderStatus status = em.find(OrderStatus.class, 1);

            order.setUser(user);
            order.setTotalAmount(dto.getTotalAmount());
            order.setOrderStatus(status);
            order.setOrderShipping(shipping);
            order.setCreatedAt(LocalDateTime.now());

            em.persist(order);

            // 3. Order Items - FIXED LOGIC HERE
            for (CartItemDTO itemDto : dto.getItems()) {
                OrderItem item = new OrderItem();
                item.setOrder(order);

                Long actualProductId = itemDto.getProductId().getId();
                Product product = em.find(Product.class, actualProductId);
                item.setProduct(product);
                item.setQuantity(itemDto.getQuantity());
                item.setPriceAtPurchase(itemDto.getProductId().getPrice());

                em.persist(item);
            }

            em.getTransaction().commit();
            return true; // Successfully saved
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            return false; // Failed to save
        } finally {
            em.close();
        }
    }

    public List<OrderDTO> getHistory(long userId) {
        List<Order> orders = orderRepository.findOrdersByUserId(userId);

        return orders.stream().map(o -> {
            OrderDTO dto = new OrderDTO();
            dto.setId(o.getId());
            dto.setTotalAmount(o.getTotalAmount());
            dto.setCreatedAt(o.getCreatedAt());

            // Map Status
            if (o.getOrderStatus() != null) {
                dto.setOrderStatus(o.getOrderStatus());
            }

            // Map Shipping
            if (o.getOrderShipping() != null) {
                dto.setOrderShipping(o.getOrderShipping());
            }

            return dto;
        }).collect(Collectors.toList());
    }

    public boolean updateOrderStatus(Long orderId, String newStatus) {
        // We delegate the database work to the repository
        return orderRepository.updateStatus(orderId, newStatus);
    }

    public List<Order> getOrdersByVendorId(Long vendorId) {
        return orderRepository.findByVendorId(vendorId);
    }
}