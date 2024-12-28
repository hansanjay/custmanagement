package com.tsd.cust.registration.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tsd.cust.registration.entity.DeliveryAgent;



public interface DeliveryAgentRepo extends JpaRepository<DeliveryAgent, Long> {
	DeliveryAgent findByMobile(String mobile);
	DeliveryAgent findByEmail(String email);

	@Query("Select d from DeliveryAgent d where d.id=:id")
	DeliveryAgent findByAgentId(@Param("id") Long id);
	
	@Query("Select d from DeliveryAgent d where d.mobile=:mobile AND d.created_by=:distid")
	DeliveryAgent findAgentByDistid(String mobile,Long distid);
}