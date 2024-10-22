package com.tsd.cust.registration.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.tsd.sdk.request.AddressReq;
import org.tsd.sdk.request.DistributorReq;
import org.tsd.sdk.request.UserReq;
import org.tsd.sdk.response.JsonSuccessResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsd.cust.registration.entity.Distributor;
import com.tsd.cust.registration.repo.DistributorPagingRepo;
import com.tsd.cust.registration.repo.DistributorRepo;

import lombok.SneakyThrows;

@Service
public class DistributorService {
	
	private static final Logger logger = LoggerFactory.getLogger(DistributorService.class);

	@Autowired(required = true)
	private DistributorRepo distributorRepo;
	
	@Autowired
	private DistributorPagingRepo distributorPagingRepo;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Transactional
	@SneakyThrows
	public ResponseEntity<?> saveDistributor(DistributorReq distributorReq) {
		ResponseEntity<?> userResponse = saveUserData(distributorReq);
		ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(userResponse.getBody().toString());
        int successCode = jsonNode.get("success_code").asInt();
		if (successCode == 302) {
			return userResponse;
		}
		JsonNode dataNode = jsonNode.path("data");
		Long userId = dataNode.path("id").asLong();
		System.out.println(jsonNode.get("data.id"));
		
		Distributor distributor = Distributor.builder()
				.id(userId)
				.first_name(distributorReq.getFirst_name())
				.last_name(distributorReq.getLast_name())
				.mobile(distributorReq.getMobile())
				.email(distributorReq.getEmail())
				.active(false)
				.enabled(false)
				.created_by(distributorReq.getCreated_by())
				.created_on(Timestamp.valueOf(LocalDateTime.now()))
				.last_updated_by(null)
				.last_updated_on(null).build();
		
		distributor = distributorRepo.save(distributor);
		ResponseEntity<?> addresponse = saveDistributorAddress(distributorReq.getMobile(), distributorReq.getAddressReq());
		logger.info("Address saved successfully "+addresponse);
		return ResponseEntity.ok(JsonSuccessResponse.ok("Success", HttpStatus.OK.value(), distributor));
		
	}
	
	@Transactional(isolation = Isolation.READ_COMMITTED)
	@SneakyThrows
	public ResponseEntity<?> fetchAllDistributor(String filter, String value) {
		Distributor distributor = null;
		switch (filter) {
		case "mobile":
			distributor = distributorRepo.findByMobile(value);
			return ResponseEntity.ok(JsonSuccessResponse.ok("Success", 200, distributor));
		case "email":
			distributor = distributorRepo.findByEmail(value);
			return ResponseEntity.ok(JsonSuccessResponse.ok("Success", 200, distributor));
		default:
			return ResponseEntity.ok(JsonSuccessResponse.ok("No match found", 404, null));
		}
	}

	public ResponseEntity<?> fectchAllDistributors(String page, String size) {
		Pageable pageable = PageRequest.of(Integer.parseInt(page), Integer.parseInt(size));
		return ResponseEntity.ok(JsonSuccessResponse.ok("Success", 200, distributorPagingRepo.findAll(pageable)));
	}
	
	@Transactional
	@SneakyThrows
	public JsonSuccessResponse<?> deleteDistributor(String mobile) {
		distributorRepo.deleteByMobile(mobile);
		return JsonSuccessResponse.ok("Resource Successfully deleted", 204, null);
	}

	@Transactional
	@SneakyThrows
	public ResponseEntity<?> modifyDistributor(String mobilenumber, DistributorReq distributorReq){

		Distributor distributor = distributorRepo.findByMobile(mobilenumber);
		if (null == distributor.getId()) {
			return ResponseEntity.ok(JsonSuccessResponse.fail("Fail", HttpStatus.NOT_FOUND.value(), "Distributor not found with " + mobilenumber));
		}
		distributor.setFirst_name(distributorReq.getFirst_name());
		distributor.setLast_name(distributorReq.getLast_name());
		distributor.setMobile(distributorReq.getMobile());
		distributor.setEmail(distributorReq.getEmail());
		distributor.setActive(distributorReq.isActive());
		
		distributor.setLast_updated_by(distributorReq.getLast_updated_by());
		distributor.setLast_updated_on(Timestamp.valueOf(LocalDateTime.now()));

		distributorRepo.save(distributor);
		return ResponseEntity.ok(JsonSuccessResponse.ok("Distributor updated successfully", 200, distributor));
	}
	
	@Transactional
	@SneakyThrows
	private ResponseEntity<?> saveDistributorAddress(String mobile, AddressReq addressReq) {
		String url = "http://localhost:8184/api/v1/tsd/add/register/"+mobile;
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<AddressReq> addReq = new HttpEntity<>(addressReq, headers);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, addReq, String.class);
		return response;
	}
	
	@Transactional
	@SneakyThrows
	private ResponseEntity<?> saveUserData(DistributorReq distributorReq) {
		
		UserReq userReq = UserReq.builder()
    			.email(distributorReq.getEmail())
    			.role("1")
    			.mobile(distributorReq.getMobile())
    			.build();
		
		String url = "http://localhost:8185/api/v1/tsd/user/registration";
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<UserReq> userObject = new HttpEntity<>(userReq, headers);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, userObject, String.class);
		return response;
	}
	
}