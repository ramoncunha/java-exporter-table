package com.export.table.csv.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Table
@Entity
public class UserEntity {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String address;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private boolean active;
}
