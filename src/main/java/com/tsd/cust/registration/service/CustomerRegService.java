package com.tsd.cust.registration.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.tsd.sdk.request.CustomerReq;
import org.tsd.sdk.request.UserReq;
import org.tsd.sdk.response.JsonSuccessResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsd.cust.registration.entity.Customer;
import com.tsd.cust.registration.entity.DeliveryAgent;
import com.tsd.cust.registration.repo.CustomerRepo;
import com.tsd.cust.registration.repo.DeliveryAgentRepo;

import lombok.SneakyThrows;

@Service
public class CustomerRegService {
	
	private static final Logger logger = LoggerFactory.getLogger(CustomerRegService.class);

	@Autowired
	private CustomerRepo customerRepo;
	
	@Autowired
	private DeliveryAgentRepo deliveryAgentRepo;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Transactional
	@SneakyThrows
	public ResponseEntity<?> saveCustomer(CustomerReq customerReq) {
		ResponseEntity<?> userResponse = saveUserData(customerReq);
		ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(userResponse.getBody().toString());
        int successCode = jsonNode.get("success_code").asInt();
		if (successCode == 302) {
			return userResponse;
		}
		JsonNode dataNode = jsonNode.path("data");
		Long userId = dataNode.path("id").asLong();
		System.out.println(jsonNode.get("data.id"));
		if (successCode == 302) {
			return userResponse;
		} else {
			if ("2".equals(customerReq.getUserType())) {
				Customer customer = Customer.builder()
						.id(userId)
						.active(false)
						.first_name(customerReq.getFirst_name())
						.last_name(customerReq.getLast_name())
						.mobile(customerReq.getMobile())
						.email(customerReq.getEmail())
						.distid(customerReq.getDistid())
						.created_by(customerReq.getDistid().toString())
						.created_on(Timestamp.valueOf(LocalDateTime.now()))
						.last_updated_by(customerReq.getDistid().toString())
						.last_updated_on(Timestamp.valueOf(LocalDateTime.now()))
						.build();
				customerRepo.save(customer);
				
				ResponseEntity<?> addresponse = saveCustomerAddress(customerReq.getMobile(), customerReq.getAddressReq());
				logger.info("Address saved successfully "+addresponse);
				
				return ResponseEntity.ok(JsonSuccessResponse.ok("Success", HttpStatus.OK.value(), customer));
			} else {
				DeliveryAgent deliveryAgent = DeliveryAgent.builder()
						.id(userId)
						.active(false)
						.first_name(customerReq.getFirst_name())
						.last_name(customerReq.getLast_name())
						.mobile(customerReq.getMobile())
						.email(customerReq.getEmail())
						.aadhar_card(customerReq.getAadhar_card())
						.distid(customerReq.getDistid())
						.verification_expiry(customerReq.getVerification_expiry())
						.created_by(customerReq.getDistid().toString())
						.created_on(Timestamp.valueOf(LocalDateTime.now()))
						.last_updated_by(customerReq.getDistid().toString())
						.last_updated_on(Timestamp.valueOf(LocalDateTime.now()))
						.build();
				deliveryAgentRepo.save(deliveryAgent);
				
				ResponseEntity<?> addresponse = saveCustomerAddress(customerReq.getMobile(), customerReq.getAddressReq());
				logger.info("Address saved successfully "+addresponse);
				
				return ResponseEntity.ok(JsonSuccessResponse.ok("Success", HttpStatus.OK.value(), deliveryAgent));
			}
		}
	}
	
	@Transactional
	@SneakyThrows
	private String saveAgentPinCodeMapping(String mobile, String agentAllotedPincodes) {
		String url = "http://localhost:8182/api/v1/tsd/pincode/agentPinCodeMapping" + mobile + "/" + agentAllotedPincodes;
		return restTemplate.getForObject(url, String.class);
	}
	
	@Transactional
	@SneakyThrows
	private ResponseEntity<?> saveCustomerAddress(String mobile, AddressReq addressReq) {
		String url = "http://localhost:8184/api/v1/tsd/add/register/"+mobile;
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<AddressReq> addReq = new HttpEntity<>(addressReq, headers);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, addReq, String.class);
		return response;
	}
	
	@Transactional
	@SneakyThrows
	private String checkIfUserAlreadyExits(String value) {
		String url = "http://localhost:8185/api/v1/tsd/user/fetch/" + value;
		return restTemplate.getForObject(url, String.class);
	}
	
	@Transactional
	@SneakyThrows
	private ResponseEntity<?> saveUserData(CustomerReq customerReq) {
		
		UserReq userReq = UserReq.builder()
				.username(customerReq.getUsername())
    			.password(customerReq.getPassword())
    			.email(customerReq.getEmail())
    			.role(customerReq.getUserType())
    			.mobile(customerReq.getMobile())
    			.build();
		
		String url = "http://localhost:8185/api/v1/tsd/user/registration";
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<UserReq> userObject = new HttpEntity<>(userReq, headers);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, userObject, String.class);
		return response;
	}

	public JsonSuccessResponse<?> deleteCustomer(String mobile) {
		return JsonSuccessResponse.ok("Resource Successfully deleted", 204, null);
	}
	
	@Transactional(isolation = Isolation.READ_COMMITTED)
	@SneakyThrows
	public ResponseEntity<?> fetchAllCustomers(String distid) {
		return ResponseEntity.ok(JsonSuccessResponse.ok("Success", 200, customerRepo.findByDistid(Long.parseLong(distid))));
	}
	
	@Transactional(isolation = Isolation.READ_COMMITTED)
	@SneakyThrows
	public ResponseEntity<?> fetchAllAgents(String distid) {
		return ResponseEntity.ok(JsonSuccessResponse.ok("Success", 200, deliveryAgentRepo.findByDistid(Long.parseLong(distid))));
	}
	
	@Transactional
	@SneakyThrows
	public ResponseEntity<?> modifyCustomer(String mobilenumber, CustomerReq customerReq){

		if("2".equals(customerReq.getUserType())) {
			Customer customer = customerRepo.findByMobile(mobilenumber);
			if (null == customer.getId()) {
				return ResponseEntity.ok(JsonSuccessResponse.fail("Fail", HttpStatus.FOUND.value(), "Customer not found with " + mobilenumber));
			}
			customer.setMobile(customerReq.getMobile());
			customer.setEmail(customerReq.getEmail());
			customer.setActive(customerReq.isActive());
			customer.setDistid(customerReq.getDistid());
			customer.setFirst_name(customerReq.getFirst_name());
			customer.setLast_name(customerReq.getLast_name());
			
			customer.setLast_updated_by(customerReq.getDistid().toString());
			customer.setLast_updated_on(Timestamp.valueOf(LocalDateTime.now()));
			
			customerRepo.save(customer);
			return ResponseEntity.ok(JsonSuccessResponse.ok("Success", 200, customer));
		}else {
			DeliveryAgent deliveryAgent = deliveryAgentRepo.findByMobile(mobilenumber);
			if (null == deliveryAgent.getId()) {
				return ResponseEntity.ok(JsonSuccessResponse.fail("Fail", HttpStatus.FOUND.value(), "Agent not found with " + mobilenumber));
			}
			deliveryAgent.setFirst_name(customerReq.getFirst_name());
			deliveryAgent.setLast_name(customerReq.getLast_name());
			deliveryAgent.setMobile(customerReq.getMobile());
			deliveryAgent.setEmail(customerReq.getEmail());
			deliveryAgent.setActive(customerReq.isActive());
			deliveryAgent.setAadhar_card(customerReq.getAadhar_card());
			deliveryAgent.setDistid(customerReq.getDistid());
			deliveryAgent.setVerification_expiry(customerReq.getVerification_expiry());
			
			deliveryAgent.setLast_updated_by(customerReq.getDistid().toString());
			deliveryAgent.setLast_updated_on(Timestamp.valueOf(LocalDateTime.now()));
			
			deliveryAgentRepo.save(deliveryAgent);
			return ResponseEntity.ok(JsonSuccessResponse.ok("Success", 200, deliveryAgent));
		}
	}
	
	@Transactional(isolation = Isolation.READ_COMMITTED)
	@SneakyThrows
	public ResponseEntity<?> getCustomersJSONObject(String value) {
		Customer customer = customerRepo.findByMobile(value);
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonString = objectMapper.writeValueAsString(customer);
		return ResponseEntity.ok(JsonSuccessResponse.ok("Success", 200, jsonString));
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	@SneakyThrows
	public ResponseEntity<?> getAgentJSONObject(String value) {
		DeliveryAgent deliveryAgent = deliveryAgentRepo.findByMobile(value);
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonString = objectMapper.writeValueAsString(deliveryAgent);
		return ResponseEntity.ok(JsonSuccessResponse.ok("Success", 200, jsonString));
	}

	@SneakyThrows
	public ResponseEntity<?> getAgentsfetchById(String id) {
		DeliveryAgent deliveryAgent = deliveryAgentRepo.findByAgentId(Long.parseLong(id));
		if (null == deliveryAgent) {
			return ResponseEntity.ok(JsonSuccessResponse.ok("Fail", HttpStatus.NOT_FOUND.value(),"Agent not found with id " + id));
		}
		return ResponseEntity.ok(JsonSuccessResponse.ok("Success", 200, deliveryAgent));
	}
	
	@SneakyThrows
	public ResponseEntity<?> getCustomersfetchById(String id) {
		Customer customer = customerRepo.findByCustomerId(Long.parseLong(id));
		if (null == customer) {
			return ResponseEntity.ok(JsonSuccessResponse.ok("Fail", HttpStatus.NOT_FOUND.value(),"Customer not found with id " + id));
		}
		return ResponseEntity.ok(JsonSuccessResponse.ok("Success", 200, customer));
	}
}