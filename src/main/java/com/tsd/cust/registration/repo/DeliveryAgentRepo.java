package com.tsd.cust.registration.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tsd.cust.registration.entity.DeliveryAgent;
import java.util.List;



public interface DeliveryAgentRepo extends JpaRepository<DeliveryAgent, Long> {
	DeliveryAgent findByMobile(String mobile);
	DeliveryAgent findByEmail(String email);
	List<DeliveryAgent> findByDistid(Long distid);
	
	@Query("Select d from DeliveryAgent d where d.id=:id")
	DeliveryAgent findByAgentId(@Param("id") Long id);
}