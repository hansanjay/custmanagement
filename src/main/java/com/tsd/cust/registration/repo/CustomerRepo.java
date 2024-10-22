package com.tsd.cust.registration.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tsd.cust.registration.entity.Customer;



public interface CustomerRepo extends JpaRepository<Customer, Long> {
	Customer findByMobile(String mobile);
	Customer findByEmail(String email);
	List<Customer> findByDistid(Long distid);
	
	@Query("Select c from Customer c where c.id=:id")
	Customer findByCustomerId(@Param("id") Long id);
}