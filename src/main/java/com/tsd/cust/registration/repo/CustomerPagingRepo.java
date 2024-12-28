package com.tsd.cust.registration.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.tsd.cust.registration.entity.Customer;


@Repository
public interface CustomerPagingRepo extends PagingAndSortingRepository<Customer, Long> {
	@Query("Select c from Customer c where c.created_by=:distid")
	Page<Customer> findByDistid(Long distid,Pageable pageable);
	
}