package com.adminportal.service.impl;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adminportal.domain.User;
import com.adminportal.domain.security.UserRole;
import com.adminportal.repository.RoleRepository;
import com.adminportal.repository.UserRepository;
import com.adminportal.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;


	@Override
	public User createUser(User user, Set<UserRole> userRoles) throws Exception {
		User localUser = userRepository.findByUsername(user.getUsername());
		if (localUser != null) {
			// throw new Exception("User already exists. Nothiong will be done here.");
			LOG.info("User {} already exists. Nothiong will be done here." + user.getUsername());
		} else {
			for (UserRole uRole : userRoles) {
				roleRepository.save(uRole.getRole());
			}
			user.getUserRoles().addAll(userRoles);

			localUser = userRepository.save(user);
		}
		return localUser;
	}

	@Override
	public User save(User user) {
		if (null == user) {
			LOG.info("No user to save.");
			return null;
		}
		userRepository.save(user);

		return user;
	}

}
