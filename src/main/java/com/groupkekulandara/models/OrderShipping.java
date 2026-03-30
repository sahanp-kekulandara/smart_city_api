package com.groupkekulandara.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@Table(name = "order_shipping")
@NoArgsConstructor
@AllArgsConstructor
public class OrderShipping implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double latitude;
    private double longitude;

    @Column(name = "address_name")
    private String addressName;

    @Column(name = "formatted_address")
    private String formattedAddress;
}
