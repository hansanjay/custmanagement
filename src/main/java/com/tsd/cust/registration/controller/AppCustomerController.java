package com.tsd.cust.registration.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.tsd.sdk.exception.ApplicationException;
import org.tsd.sdk.request.AuthRequest;
import org.tsd.sdk.request.OTPRequest;

import com.tsd.cust.registration.service.CustomerRegService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/c")
@CrossOrigin
@Tag(name = "Fetch Customer/Agent API", description = "Operations related to fetch list of Customer/Agent")
public class AppCustomerController {

	@Autowired
	CustomerRegService customerRegService;
	
	@GetMapping(path = "/customer/profile", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Fetch all Customer", description = "Retrieve a list of all Customer")
	public ResponseEntity<?> getCustomerDetail(@RequestAttribute("_X_AUTH_ID") String userId) {
		return customerRegService.getUserDetail("2", userId);
	}
	
	@GetMapping(path = "/agent/profile", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Fetch all Customer", description = "Retrieve a list of all Customer")
	public ResponseEntity<?> getAgentDetail(@RequestAttribute("_X_AUTH_ID") String userId) {
		return customerRegService.getUserDetail("3", userId);
	}

	@PostMapping(path = "/otp", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Fetch all OTP", description = "Responsible to generate the otp")
	public ResponseEntity<?> getOTP(@RequestBody OTPRequest otpRequest) {
		customerRegService.generateOTP(otpRequest);
		return ResponseEntity.noContent().build();
	}
	
	@PostMapping(path = "/auth", produces = "application/json")
    public Map<String, Object> generateToken(@RequestParam("rid") String rid, 
                                             @RequestBody AuthRequest authRequest) {
        authRequest.setRid(rid);
        String token = null;
		try {
			token = customerRegService.generateAuthToken(authRequest);
		} catch (ApplicationException e) {
			e.printStackTrace();
		}

        Map<String, Object> response = new HashMap<>();
        response.put("Token", token);
        response.put("Expiry", LocalDateTime.now().plusHours(2).toString());

        return response;
    }
	
}