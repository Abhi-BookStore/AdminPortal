package com.adminportal.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.adminportal.domain.security.Role;

@Repository
public interface RoleRepository extends CrudRepository<Role	, Long> {
	
	Role findByName(String name);

}
