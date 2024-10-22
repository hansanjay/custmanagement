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

import com.tsd.cust.registration.service.CustomerRegService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/v1/tsd/cust")
@CrossOrigin
@Tag(name = "Delete Customer/Agent API", description = "Operations related to delete Customer/Agent based on mobile")
public class CustomerDeleteController {
	
	@Autowired
	CustomerRegService customerRegService;
	
	@DeleteMapping(path = "/delete/{mobile}",
			produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Delete Customer/Agent", description = "Related to delete Customer/Agent based on mobile")
	public ResponseEntity<?> fetchAllCustomers(@PathVariable("mobile") String mobile){
		JsonSuccessResponse<?> response = (JsonSuccessResponse<?>) customerRegService.deleteCustomer(mobile);
		return ResponseEntity.status(response.successCode).body(response);
	}
}