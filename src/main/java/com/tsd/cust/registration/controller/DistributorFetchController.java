package com.tsd.cust.registration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsd.cust.registration.service.DistributorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/v1/tsd/dist")
@CrossOrigin
@Tag(name = "Fetch Distributor API", description = "Operations related to fetch list of distributors")
public class DistributorFetchController {
	
	@Autowired
	DistributorService distributorService;
	
	@GetMapping(path = "/fetch/{filter}/{value}",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Fetch all distributors", description = "Retrieve a list of all distributors")
	public ResponseEntity<?> getdistributors(@PathVariable("filter") String filter,@PathVariable("value") String value){
		return distributorService.fetchAllDistributor(filter,value);
	}
	
	@GetMapping(path = "/fetchAll/{page}/{size}",produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Fetch all distributors", description = "Retrieve a list of all distributors")
	public ResponseEntity<?> fetchAllDistributors(@PathVariable("page") String page, @PathVariable("size") String size){
		return distributorService.fectchAllDistributors(page,size);
	}
}