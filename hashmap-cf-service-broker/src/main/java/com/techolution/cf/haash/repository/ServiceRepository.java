package com.techolution.cf.haash.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.techolution.cf.haash.domain.Service;

@Repository
public interface ServiceRepository extends CrudRepository<Service, String> {

}
