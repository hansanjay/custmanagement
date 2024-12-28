package com.tsd.cust.registration.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.tsd.cust.registration.entity.DeliveryAgent;


@Repository
public interface DeliveryAgentPagingRepo extends PagingAndSortingRepository<DeliveryAgent, Long> {
	
	@Query("Select d from DeliveryAgent d where d.created_by=:distid")
	Page<DeliveryAgent> findByDistid(Long distid,Pageable pageable);
	
}