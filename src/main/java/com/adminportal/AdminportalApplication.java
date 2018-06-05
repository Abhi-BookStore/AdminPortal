package com.adminportal;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.adminportal.domain.User;
import com.adminportal.domain.security.Role;
import com.adminportal.domain.security.UserRole;
import com.adminportal.service.UserService;
import com.adminportal.utility.SecurityUtility;

@SpringBootApplication
public class AdminportalApplication implements CommandLineRunner {
	
	@Autowired
	private UserService userService;

	public static void main(String[] args) {
		SpringApplication.run(AdminportalApplication.class, args);
	}
	
	@Override
	public void run(String... args) throws Exception {
		 User user = new User();
		
		 user.setFirstName("Abhinav");
		 user.setLastName("Singh");
		 user.setUsername("admin");
		 user.setEmail("singh09.abhinav+admin@gmail.com");
		 user.setEnabled(true);
		 user.setPassword(SecurityUtility.passwordEncoder().encode("admin"));
		 user.setPhone("8087102325");
		 Set<UserRole> userRoles = new HashSet<>();
		 Role role1 = new Role();
		 role1.setRoleId(0);
		 role1.setName("ROLE_ADMIN");
		 userRoles.add(new UserRole(user, role1));
		
		 userService.createUser(user, userRoles);

	}

}
