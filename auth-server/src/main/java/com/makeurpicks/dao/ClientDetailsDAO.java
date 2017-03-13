package com.makeurpicks.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.makeurpicks.domain.OAuthClientDetails;

@Repository
public interface ClientDetailsDAO extends CrudRepository<OAuthClientDetails, String> {
	public OAuthClientDetails findByClientId(String client_id);
}
