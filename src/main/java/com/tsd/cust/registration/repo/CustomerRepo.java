package com.tsd.cust.registration.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tsd.cust.registration.entity.Customer;


@Repository
public interface CustomerRepo extends JpaRepository<Customer, Long> {
	Customer findByMobile(String mobile);
	Customer findByEmail(String email);
	
	@Query("Select c from Customer c where c.id=:id")
	Customer findByCustomerId(@Param("id") Long id);
	
	@Query("Select c from Customer c where c.mobile =:mobile AND c.created_by=:distid")
	Customer findCustByDistid(String mobile,Long distid);
	
}