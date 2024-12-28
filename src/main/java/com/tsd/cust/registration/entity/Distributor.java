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

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "distributor")
@Entity
public class Distributor {
	
	@Id
    private Long id;
    
    private boolean active;
    private boolean enabled;
    private String first_name;
    private String last_name;
    private String email;
    private String mobile;
    private String pannum;
    private String gstin;
    private String companyName;
    
    private Timestamp created_on;
    private Timestamp last_updated_on;
    private String created_by;
    private String last_updated_by;
    
}