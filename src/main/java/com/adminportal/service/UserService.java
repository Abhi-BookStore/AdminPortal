package com.adminportal.service;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.adminportal.domain.User;
import com.adminportal.domain.security.UserRole;

public interface UserService {
	
	User createUser(User user, Set<UserRole> userRoles) throws Exception;

	User save(User user);

}
