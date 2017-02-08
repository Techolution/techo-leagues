package com.techolution.cf.haash.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.techolution.cf.haash.domain.Credentials;
import com.techolution.cf.haash.domain.Service;
import com.techolution.cf.haash.domain.ServiceBinding;
import com.techolution.cf.haash.domain.ServiceInstance;
import com.techolution.cf.haash.repository.ServiceBindingRepository;
import com.techolution.cf.haash.repository.ServiceInstanceRepository;
import com.techolution.cf.haash.repository.ServiceRepository;
import com.techolution.cf.haash.service.HashService;

/**
 * 
 * @author Prithvish Mukherjee
 *
 */
@RestController
public class ServiceBrokerController {

	Log log = LogFactory.getLog(ServiceBrokerController.class);

	@Autowired
	private ServiceRepository serviceRepository;

	@Autowired
	private ServiceInstanceRepository serviceInstanceRepository;

	@Autowired
	private ServiceBindingRepository serviceBindingRepository;

	@Autowired
	private HashService hashService;

	@RequestMapping("/v2/catalog")
	public Map<String, Iterable<Service>> catalog() {
		Map<String, Iterable<Service>> wrapper = new HashMap<>();
		wrapper.put("services", serviceRepository.findAll());
		return wrapper;
	}

	/**
	 * 
	 * @param id
	 * @param serviceInstance
	 * @return
	 */
	@RequestMapping(value = "/v2/service_instances/{id}", method = RequestMethod.PUT)
	public ResponseEntity<String> create(@PathVariable("id") String id, @RequestBody ServiceInstance serviceInstance) {
		serviceInstance.setId(id);
		boolean exists = serviceInstanceRepository.exists(id);
		if (exists) {
			ServiceInstance existing = serviceInstanceRepository.findOne(id);
			if (existing.equals(serviceInstance)) {
				return new ResponseEntity<String>(new GsonBuilder().create().toJson(existing), HttpStatus.OK);
			}
			return new ResponseEntity<String>(getJsonObject(HttpStatus.CONFLICT, "Some App error"),
					HttpStatus.CONFLICT);

		}
		serviceInstanceRepository.save(serviceInstance);
		ServiceInstance instance = serviceInstanceRepository.findOne(serviceInstance.getId());
		hashService.create(id);
		return new ResponseEntity<String>(new GsonBuilder().create().toJson(instance), HttpStatus.CREATED);

	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/v2/service_instances/{id}", method = RequestMethod.GET)
	public ResponseEntity<String> getServiceInstanceInfo(@PathVariable String id) {
		boolean exists = serviceInstanceRepository.exists(id);
		if (exists) {
			ServiceInstance instance = serviceInstanceRepository.findOne(id);
			return new ResponseEntity<String>(new GsonBuilder().create().toJson(instance), HttpStatus.OK);
		}
		return new ResponseEntity<String>(getJsonObject(HttpStatus.NOT_FOUND, "no Such Service Instance exists"),
				HttpStatus.NOT_FOUND);

	}

	/**
	 * This method gets a default JsonStructure for the generic Response
	 * 
	 * @param status
	 * @param message
	 * @return
	 */
	private String getJsonObject(HttpStatus status, String message) {
		JsonObject obj = new JsonObject();
		obj.addProperty("message", message);
		obj.addProperty("status", "SUCCESS");
		obj.addProperty("code", status.value());
		return obj.toString();
	}

	/**
	 * 
	 * @param instanceId
	 * @param id
	 * @param serviceBinding
	 * @return
	 */
	@RequestMapping(value = "/v2/service_instances/{instanceId}/service_bindings/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> createBinding(@PathVariable("instanceId") String instanceId, @PathVariable("id") String id,
			@RequestBody ServiceBinding serviceBinding) {
		if (!serviceInstanceRepository.exists(instanceId)) {
			return new ResponseEntity<Object>(
					getJsonObject(HttpStatus.BAD_REQUEST, "The service instance " + instanceId + " doesn't exist"),
					HttpStatus.BAD_REQUEST);
		}
		serviceBinding.setId(id);
		serviceBinding.setInstanceId(instanceId);
		boolean exists = serviceBindingRepository.exists(id);
		if (exists) {
			ServiceBinding existing = serviceBindingRepository.findOne(id);
			if (existing.equals(serviceBinding)) {
				return new ResponseEntity<Object>(wrapCredentials(existing.getCredentials()), HttpStatus.OK);
			}
			return new ResponseEntity<String>(getJsonObject(HttpStatus.CONFLICT, "Some App error"),
					HttpStatus.CONFLICT);
		}
		Credentials credentials = myCredentials(instanceId);
		serviceBinding.setCredentials(credentials);
		serviceBindingRepository.save(serviceBinding);
		return new ResponseEntity<Object>(wrapCredentials(credentials), HttpStatus.CREATED);

	}

	/**
	 * @param instanceId
	 * @return
	 */
	private Credentials myCredentials(String instanceId) {
		Credentials credentials = new Credentials();
		credentials.setId(UUID.randomUUID().toString());
		credentials.setUri("https://" + myUri() + "/prits-hashmap/" + instanceId);
		credentials.setUsername("mojojojo");
		credentials.setPassword("secretmojo");
		credentials.setDescriptionUri("Please use the URI without the port numbers , Thank You, Prithvish");
		return credentials;
	}

	/**
	 * 
	 * @param instanceId
	 * @param id
	 * @param serviceId
	 * @param planId
	 * @return
	 */
	@RequestMapping(value = "/v2/service_instances/{instanceId}/service_bindings/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteBinding(@PathVariable("instanceId") String instanceId, @PathVariable("id") String id,
			@RequestParam("service_id") String serviceId, @RequestParam("plan_id") String planId) {
		if (!serviceInstanceRepository.exists(instanceId)) {
			return new ResponseEntity<String>(
					getJsonObject(HttpStatus.BAD_REQUEST, "Service instance " + instanceId + " doesn't not exist"),
					HttpStatus.BAD_REQUEST);
		}
		boolean exists = serviceBindingRepository.exists(id);
		if (exists) {
			serviceBindingRepository.delete(id);
			return new ResponseEntity<String>(
					getJsonObject(HttpStatus.OK,
							"Service binding " + id + " for service instance " + instanceId + " was deleted"),
					HttpStatus.OK);
		}
		return new ResponseEntity<String>(getJsonObject(HttpStatus.GONE,
				"Service binding " + id + " for service instance " + instanceId + " doesn't not exist or deleted"),
				HttpStatus.GONE);

	}

	/**
	 * 
	 * @param id
	 * @param serviceId
	 * @param planId
	 * @return
	 */
	@RequestMapping(value = "/v2/service_instances/{id}", method = RequestMethod.DELETE)
	@Transactional(rollbackFor = { Throwable.class })
	public ResponseEntity<String> deleteServiceInstance(@PathVariable("id") String id,
			@RequestParam("service_id") String serviceId, @RequestParam("plan_id") String planId) {
		boolean exists = serviceRepository.exists(id);
		if (exists) {
			hashService.delete(id);
			serviceRepository.delete(id);
			return new ResponseEntity<String>(getJsonObject(HttpStatus.OK, "Service instance " + id + " was deleted"),
					HttpStatus.OK);
		}
		return new ResponseEntity<>(
				getJsonObject(HttpStatus.GONE, "Service instance " + id + " doesn't exist or deleted"),
				HttpStatus.GONE);
	}

	@Value("${apps.uris}")
	private String apps_uris;

	private String myUri() {
		return apps_uris;
	}

	/**
	 * 
	 * @param credentials
	 * @return
	 */
	private Map<String, Object> wrapCredentials(Credentials credentials) {
		Map<String, Object> wrapper = new HashMap<String, Object>();
		wrapper.put("credentials", credentials);
		return wrapper;
	}
}
