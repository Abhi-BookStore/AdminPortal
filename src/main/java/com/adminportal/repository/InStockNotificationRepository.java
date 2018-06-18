package com.adminportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.adminportal.domain.InStockNotification;


@Repository
public interface InStockNotificationRepository extends CrudRepository<InStockNotification, Long> {

	InStockNotification findByEmail(String email);

	@Query("select u from InStockNotification u where u.bookId = ?1 and u.notified='false'")
	List<InStockNotification> findByBookIdAndIsNotifyMe(Long bookId);

	@Query("select u from InStockNotification u where u.email= ?1 and u.bookId = ?2")
	InStockNotification findByEmailIdAndBookId(String emailId, Long bookId);

}
