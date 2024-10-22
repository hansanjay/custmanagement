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
@Table(name = "delivery_agent")
public class DeliveryAgent {
	
	@Id
	private Long id;
	
	private boolean active;
	private Long distid;
	private String first_name;
	private String last_name;
	private String mobile;
	private String email;
	private String aadhar_card;
	private String verification_expiry;
	
	private Timestamp created_on;
	private Timestamp last_updated_on;
	private String created_by;
	private String last_updated_by;
	
}