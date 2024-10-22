package com.tsd.cust.registration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tsd.sdk.response.JsonSuccessResponse;

import com.tsd.cust.registration.service.DistributorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/v1/tsd/dist")
@CrossOrigin
@Tag(name = "Delete Distributor API", description = "Operations related to delete distributor based on email")
public class DistributorDeleteController {
	
	@Autowired
	DistributorService distributorService;
	
	@DeleteMapping(path = "/delete/{mobile}",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Delete Distributors", description = "Related to delete distributor based on email")
	public ResponseEntity<?> fetchAllDistributors(@PathVariable("mobile") String mobile){
		JsonSuccessResponse<?> response = (JsonSuccessResponse<?>) distributorService.deleteDistributor(mobile);
		return ResponseEntity.status(response.successCode).body(response);
	}
}