package com.techolution.cf.haash.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.techolution.cf.haash.collection.CustomHashMap;
import com.techolution.cf.haash.service.HashService;

/**
 * 
 * @author Prithvish Mukherjee
 *
 */
@RestController
public class HashServiceController {

	@Autowired
	private HashService service;

	@Value("${apps.uris}")
	private String apps_uris;

	private String myUri() {
		return apps_uris;
	}

	/**
	 * this is the default Service urls Instance Info for a particular
	 * service_instance
	 * 
	 * @return
	 */
	@RequestMapping(value = "/prits-hashmap/{instanceId}", method = RequestMethod.GET)
	public ResponseEntity<?> getMap(@PathVariable(value = "instanceId") String instanceId) {
		class InstanceUris {
			@JsonProperty(value = "service_uris")
			private CustomHashMap<String, List<String>> uris;

			public InstanceUris() {
				CustomHashMap<String, List<String>> values = new CustomHashMap<String, List<String>>();
				values.put("https://" + myUri() + "/prits-hashmap/" + instanceId + "/",
						Arrays.asList(new String[] { "GET" }));
				values.put("https://" + myUri() + "/prits-hashmap/" + instanceId + "/{key}",
						Arrays.asList(new String[] { "GET", "DELETE" }));
				values.put("https://" + myUri() + "/prits-hashmap/" + instanceId + "/{key}/{value}",
						Arrays.asList(new String[] { "PUT" }));
				this.uris = values;
			}

			@SuppressWarnings("unused")
			public CustomHashMap<String, List<String>> getUris() {
				return uris;
			}

			@SuppressWarnings({ "unused" })
			public void setUris(CustomHashMap<String, List<String>> uris) {
				this.uris = uris;
			}
		}
		String jsonObj = new GsonBuilder().create().toJson(new InstanceUris());
		return new ResponseEntity<String>(jsonObj, HttpStatus.OK);
	}

	/**
	 * This method adds a key:value pair to a Map of a particular
	 * service_instance
	 * 
	 * @param instanceId
	 * @param key
	 * @param value
	 * @return
	 */
	@RequestMapping(value = "/prits-hashmap/{instanceId}/{key}/{value}", method = RequestMethod.PUT)
	public ResponseEntity<String> put(@PathVariable("instanceId") String instanceId, @PathVariable("key") String key,
			@PathVariable("value") String value) {
		service.put(instanceId, key, value);
		return new ResponseEntity<String>(getJsonObject(HttpStatus.CREATED, "For service_instance " + instanceId
				+ " key=> " + key + ", value=> " + value + " was successfully created"), HttpStatus.CREATED);
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
	 * This method is used to retrieve the value for a defined key in a
	 * service_instance
	 * 
	 * @param instanceId
	 * @param key
	 * @return
	 */
	@RequestMapping(value = "/prits-hashmap/{instanceId}/{key}", method = RequestMethod.GET)
	public ResponseEntity<?> get(@PathVariable("instanceId") String instanceId, @PathVariable("key") String key) {
		Object result = service.get(instanceId, key);
		if (null != result) {
			return new ResponseEntity<Object>(
					getJsonObject(HttpStatus.OK,
							"service_instance=> " + instanceId + "; for key=> " + key + " value=> " + (String) result),
					HttpStatus.OK);
		} else {
			return new ResponseEntity<String>(
					getJsonObject(HttpStatus.NOT_FOUND,
							"key =>" + key + " OR service_instance => " + instanceId + " , DOES NOT EXIST"),
					HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * this method is used to delete the Key:value pair in the Map
	 * 
	 * @param instanceId
	 * @param key
	 * @return
	 */
	@RequestMapping(value = "/prits-hashmap/{instanceId}/{key}", method = RequestMethod.DELETE)
	public ResponseEntity<String> delete(@PathVariable("instanceId") String instanceId,
			@PathVariable("key") String key) {
		Object result = service.get(instanceId, key);
		if (result != null) {
			service.delete(instanceId, key);
			return new ResponseEntity<String>(getJsonObject(HttpStatus.OK,
					"service_instance=> " + instanceId + "; for key=> " + key + " was DELETED"), HttpStatus.OK);
		} else {
			return new ResponseEntity<String>(
					getJsonObject(HttpStatus.NOT_FOUND,
							"key =>" + key + " OR service_instance => " + instanceId + " , DOES NOT EXIST"),
					HttpStatus.NOT_FOUND);
		}
	}

}
