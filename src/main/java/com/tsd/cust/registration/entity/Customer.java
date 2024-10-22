package com.tsd.cust.registration.entity;

import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "customer")
public class Customer{
	
	@Id
    private Long id;
    
    private boolean active;
    private String first_name;
    private String last_name;
    private String mobile;
    private String email;
    private Long distid;
    
    private Timestamp created_on;
    private Timestamp last_updated_on;
    private String created_by;
    private String last_updated_by;
    
}