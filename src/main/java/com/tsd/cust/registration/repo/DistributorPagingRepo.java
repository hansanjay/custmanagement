package com.tsd.cust.registration.repo;


import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import com.tsd.cust.registration.entity.Distributor;


@Repository
public interface DistributorPagingRepo extends PagingAndSortingRepository<Distributor, Long> {
}