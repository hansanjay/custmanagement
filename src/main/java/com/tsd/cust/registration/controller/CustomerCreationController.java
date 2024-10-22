package com.tsd.cust.registration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tsd.sdk.request.CustomerReq;

import com.tsd.cust.registration.service.CustomerRegService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/v1/tsd/cust")
@CrossOrigin
@Tag(name = "Registration Customer API", description = "Operations related to registration of Customer/Agent")
public class CustomerCreationController {
	
	@Autowired
	CustomerRegService customerRegService;
	
	@GetMapping(path = "/health")
	public ResponseEntity<?> health(){
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PostMapping(path = "/register",
			produces=MediaType.APPLICATION_JSON_VALUE,
			consumes=MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Customer/Agent registration", description = "Operations related to registration of customer/Agent")
    public ResponseEntity<?> createUser(@RequestBody CustomerReq customerReq) {
        return customerRegService.saveCustomer(customerReq);
    }

}