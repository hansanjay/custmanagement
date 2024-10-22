package com.tsd.cust.registration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tsd.cust.registration.service.CustomerRegService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/v1/tsd/cust")
@CrossOrigin
@Tag(name = "Fetch Customer/Agent API", description = "Operations related to fetch list of Customer/Agent")
public class CustomerFetchController {

	@Autowired
	CustomerRegService customerRegService;

	@GetMapping(path = "/fetch/{distid}/{userType}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Fetch all Customer", description = "Retrieve a list of all Customer")
	public ResponseEntity<?> getCustomers(@PathVariable("distid") String distid,
			@PathVariable("userType") String userType){
		if ("2".equals(userType)) {
			return customerRegService.fetchAllCustomers(distid);
		} else {
			return customerRegService.fetchAllAgents(distid);
		}
	}
	
	@GetMapping(path = "/fetch/json/{value}/{userType}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Fetch all Customer", description = "Retrieve a list of all Customer")
	public ResponseEntity<?> getCustomersJSONObject(@PathVariable("value") String value,@PathVariable("userType") String userType){
		if ("2".equals(userType)) {
			return customerRegService.getCustomersJSONObject(value);
		} else {
			return customerRegService.getAgentJSONObject(value);
		}
	}
	
	@GetMapping(path = "/fetchAgentByCustbyId/{agentid}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Fetch Customer based on Agent ID", description = "Retrieve Customer based on Agent ID")
	public ResponseEntity<?> getAgentsfetchById(@PathVariable("agentid") String agentid){ 
		return customerRegService.getAgentsfetchById(agentid);
	}
	
	@GetMapping(path = "/fetchCustByAgentId/{custid}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Fetch Agent based on Customer ID", description = "Retrieve Agent based on Customer ID")
	public ResponseEntity<?> getCustfetchById(@PathVariable("custid") String custid){
		return customerRegService.getCustomersfetchById(custid);
	}
}