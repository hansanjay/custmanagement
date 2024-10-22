package com.tsd.cust.registration.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tsd.cust.registration.entity.Distributor;


@Repository
public interface DistributorRepo extends JpaRepository<Distributor, Long> {
	void deleteByMobile(String mobile);
	Distributor findByEmail(String email);
	Distributor findByMobile(String mobile);
}