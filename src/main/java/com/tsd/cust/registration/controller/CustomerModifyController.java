package com.tsd.cust.registration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@Tag(name = "Modify Customer API", description = "Operations related to modify customer details based on mobile")
public class CustomerModifyController {
	
	@Autowired
	CustomerRegService customerRegService;
	
	@PatchMapping(path = "/update/{mobilenumber}",
			consumes=MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Modify Distributor", description = "Related to modify customer based on mobile")
	public ResponseEntity<?> modifyCustomers(@PathVariable("mobilenumber") String mobilenumber,@RequestBody CustomerReq customerReq){
		return customerRegService.modifyCustomer(mobilenumber,customerReq);
	}
}