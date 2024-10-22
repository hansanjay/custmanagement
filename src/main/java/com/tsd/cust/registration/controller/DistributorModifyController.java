package com.tsd.cust.registration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tsd.sdk.request.DistributorReq;

import com.tsd.cust.registration.service.DistributorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/v1/tsd/dist")
@CrossOrigin
@Tag(name = "Modify Distributor API", description = "Operations related to modify distributor details based on mobile")
public class DistributorModifyController {

	@Autowired
	DistributorService distributorService;

	@PutMapping(path = "/update/{mobilenumber}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Modify Distributor", description = "Related to modify distributor based on mobile")
	public ResponseEntity<?> modifyDistributors(@PathVariable("mobilenumber") String mobilenumber,
			@RequestBody DistributorReq distributorReq){
		return distributorService.modifyDistributor(mobilenumber,distributorReq);
	}
}