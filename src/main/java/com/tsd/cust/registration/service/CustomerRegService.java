package com.tsd.cust.registration.service;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.SecretKey;

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
import org.tsd.sdk.exception.ApplicationException;
import org.tsd.sdk.request.AddressReq;
import org.tsd.sdk.request.AuthRequest;
import org.tsd.sdk.request.CustomerReq;
import org.tsd.sdk.request.OTPDetails;
import org.tsd.sdk.request.OTPRequest;
import org.tsd.sdk.request.UserReq;
import org.tsd.sdk.response.JsonSuccessResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tsd.cust.registration.entity.Customer;
import com.tsd.cust.registration.entity.DeliveryAgent;
import com.tsd.cust.registration.repo.CustomerPagingRepo;
import com.tsd.cust.registration.repo.CustomerRepo;
import com.tsd.cust.registration.repo.DeliveryAgentPagingRepo;
import com.tsd.cust.registration.repo.DeliveryAgentRepo;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.SneakyThrows;

@Service
public class CustomerRegService {
	
	private static final Logger logger = LoggerFactory.getLogger(CustomerRegService.class);

	@Autowired
	private CustomerRepo customerRepo;
	
	@Autowired
	private CustomerPagingRepo customerPagingRepo;
	
	@Autowired
	private DeliveryAgentRepo deliveryAgentRepo;
	
	@Autowired
	private DeliveryAgentPagingRepo deliveryAgentPagingRepo;
	
	@Autowired
	private RestTemplate restTemplate;
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@Transactional
	@SneakyThrows
	public ResponseEntity<?> saveCustomer(CustomerReq customerReq) {
		ResponseEntity<?> userResponse = saveUserData(customerReq);
        JsonNode jsonNode = objectMapper.readTree(userResponse.getBody().toString());
        int successCode = jsonNode.get("success_code").asInt();
		if (successCode == 302) {
			return userResponse;
		}
		JsonNode dataNode = jsonNode.path("data");
		Long userId = dataNode.path("id").asLong();
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
	public ResponseEntity<?> fetchAllCustomers(String distid, String page, String size) {
		Pageable pageable = PageRequest.of(Integer.parseInt(page), Integer.parseInt(size));
		return ResponseEntity.ok(JsonSuccessResponse.ok("Success", 200, customerPagingRepo.findByDistid(Long.parseLong(distid),pageable)));
	}
	
	@Transactional(isolation = Isolation.READ_COMMITTED)
	@SneakyThrows
	public ResponseEntity<?> fetchAllAgents(String distid, String page, String size) {
		Pageable pageable = PageRequest.of(Integer.parseInt(page), Integer.parseInt(size));
		return ResponseEntity.ok(JsonSuccessResponse.ok("Success", 200, deliveryAgentPagingRepo.findByDistid(Long.parseLong(distid),pageable)));
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
	public ResponseEntity<?> getCustomersJSONObject(String mobile,String distid) {
		Customer customer = customerRepo.findCustByDistid(mobile,Long.parseLong(distid));
//		ObjectMapper objectMapper = new ObjectMapper();
//		String jsonString = objectMapper.writeValueAsString(customer);
		return ResponseEntity.ok(JsonSuccessResponse.ok("Success", 200, customer));
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	@SneakyThrows
	public ResponseEntity<?> getAgentJSONObject(String mobile,String distid) {
		DeliveryAgent deliveryAgent = deliveryAgentRepo.findAgentByDistid(mobile,Long.parseLong(distid));
//		ObjectMapper objectMapper = new ObjectMapper();
//		String jsonString = objectMapper.writeValueAsString(deliveryAgent);
		return ResponseEntity.ok(JsonSuccessResponse.ok("Success", 200, deliveryAgent));
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
	
	@Transactional(isolation = Isolation.READ_COMMITTED)
	@SneakyThrows
	public ResponseEntity<?> fetchCustDetails(String filter,String value) {
		switch (filter) {
		case "mobile":
			Customer customer = customerRepo.findByMobile(value);
			CustomerReq customerReq = copyEntityToDTO(customer);
			AddressReq addressReq = getUserAddressDetails(customer.getMobile());
			customerReq.setAddressReq(addressReq);
			return ResponseEntity.ok(JsonSuccessResponse.ok("Success", 200, customerReq));
		case "email":
			customer = customerRepo.findByEmail(value);
			customerReq = copyEntityToDTO(customer);
			addressReq = getUserAddressDetails(customerReq.getMobile());
			customerReq.setAddressReq(addressReq);
			return ResponseEntity.ok(JsonSuccessResponse.ok("Success", 200, customerReq));
		default:
			return ResponseEntity.ok(JsonSuccessResponse.ok("No match found", 404, null));
		}
	}
	
	@Transactional(isolation = Isolation.READ_COMMITTED)
	@SneakyThrows
	public ResponseEntity<?> fetchAgentDetails(String filter,String value) {
		switch (filter) {
		case "mobile":
			DeliveryAgent deliveryAgent = deliveryAgentRepo.findByMobile(value);
			CustomerReq customerReq = copyEntityToDTO(deliveryAgent);
			AddressReq addressReq = getUserAddressDetails(deliveryAgent.getMobile());
			customerReq.setAddressReq(addressReq);
			return ResponseEntity.ok(JsonSuccessResponse.ok("Success", 200, customerReq));
		case "email":
			deliveryAgent = deliveryAgentRepo.findByEmail(value);
			customerReq = copyEntityToDTO(deliveryAgent);
			addressReq = getUserAddressDetails(deliveryAgent.getMobile());
			customerReq.setAddressReq(addressReq);
			return ResponseEntity.ok(JsonSuccessResponse.ok("Success", 200, customerReq));
		default:
			return ResponseEntity.ok(JsonSuccessResponse.ok("No match found", 404, null));
		}
	}
	
	@SneakyThrows
	public ResponseEntity<?> getUserDetail(String userType, String userId) {
		if("2".equals(userType)) {
			Customer customer = customerRepo.findByCustomerId(Long.parseLong(userId));
			CustomerReq customerReq = copyEntityToDTO(customer);
			AddressReq addressReq = getUserAddressDetails(customer.getMobile());
			customerReq.setAddressReq(addressReq);
			return ResponseEntity.ok(JsonSuccessResponse.ok("Success", 200, customerReq));
		}else {
			DeliveryAgent deliveryAgent = deliveryAgentRepo.findByAgentId(Long.parseLong(userId));
			CustomerReq customerReq = copyEntityToDTO(deliveryAgent);
			AddressReq addressReq = getUserAddressDetails(deliveryAgent.getMobile());
			customerReq.setAddressReq(addressReq);
			return ResponseEntity.ok(JsonSuccessResponse.ok("Success", 200, customerReq));
		}
	}
	
	private CustomerReq copyEntityToDTO(Customer customer) {
		return CustomerReq.builder()
				.first_name(customer.getFirst_name())
				.last_name(customer.getLast_name())
				.active(customer.isActive())
				.email(customer.getEmail())
				.mobile(customer.getMobile())
				.distid(customer.getDistid())
				.build();
	}
	
	private CustomerReq copyEntityToDTO(DeliveryAgent agent) {
		return CustomerReq.builder()
				.first_name(agent.getFirst_name())
				.last_name(agent.getLast_name())
				.active(agent.isActive())
				.email(agent.getEmail())
				.mobile(agent.getMobile())
				.distid(agent.getDistid())
				.aadhar_card(agent.getAadhar_card())
				.verification_expiry(agent.getVerification_expiry())
				.build();
	}
	
	private AddressReq getUserAddressDetails(String mobile) throws JsonProcessingException, JsonMappingException {
		ResponseEntity<?> response = fetchAddressByMobileNumber(mobile);
		JsonNode jsonNode = objectMapper.readTree(response.getBody().toString());
		JsonNode dataNode = jsonNode.path("data");
		AddressReq addressReq = objectMapper.treeToValue(dataNode.get(0), AddressReq.class);
		return addressReq;
	}
	
	private ResponseEntity<?> fetchAddressByMobileNumber(String mobile) {
        try {
        	String url = "http://localhost:8184/api/v1/tsd/add/fetch/"+mobile;
        	ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, mobile);
        	return response;
        } catch (Exception e) {
            System.err.println("Error calling Address API: " + e.getMessage());
            return (ResponseEntity<?>) ResponseEntity.notFound();
        }
    }
	
	private static ConcurrentHashMap<String,OTPDetails> otpStorage = new ConcurrentHashMap<>();

	@SneakyThrows
	public ResponseEntity<?> generateOTP(OTPRequest otpRequest) {
		Customer customer = customerRepo.findByMobile(otpRequest.getPrincipal());
		if(null == customer) {
			ResponseEntity.ok(JsonSuccessResponse.fail("No match found", HttpStatus.NOT_FOUND.value(), null));
		}
	    String otp = String.format("%04d", new Random().nextInt(10000));
	    otpStorage.put(otpRequest.getPrincipal(), new OTPDetails("1234",otpRequest));
	    System.out.println("OTP sent successfully to "+otpRequest.getPrincipal());
	    return ResponseEntity.ok(JsonSuccessResponse.ok("Your OTP to login into TSD application is "+otp+". This OTP is valid for next 5 minutes.", HttpStatus.OK.value(), otp));
	}
	
	public String generateAuthToken(AuthRequest request) throws ApplicationException {
        // Retrieve OTP details
        OTPDetails otpDetails = otpStorage.get(request.getPrincipal());

        // Validate the OTP details and request
        if (otpDetails != null && request.getOtp().equals(otpDetails.getOtp())) {
            otpStorage.remove(request.getPrincipal());

            // Fetch the authenticated user details
            Customer user = customerRepo.findByMobile(request.getPrincipal());

            // Prepare JWT claims
            Map<String, Object> claims = new HashMap<>();
            claims.put("sub", request.getPrincipal());
            claims.put("iat", System.currentTimeMillis());
            claims.put("expiry", LocalDateTime.now().plusHours(2).toString());
            claims.put("deviceId", request.getDeviceId());
            claims.put("user", user);

            // Fetch the secret key for signing the JWT
            String secret = "cBZchf6NNTbG55NqexpW4AZ3vn41Nj42He";
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

            // Generate and return the signed JWT
            return Jwts.builder()
                       .setClaims(claims)
                       .signWith(key)
                       .compact();
        } else {
            throw new ApplicationException(0, "BAD REQUEST", HttpStatus.BAD_REQUEST);
        }
    }
	
}