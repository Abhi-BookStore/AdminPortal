package com.adminportal.service;

import java.util.List;

import com.adminportal.domain.InStockNotification;

public interface InStockNotificationService {

	void save(InStockNotification inStockNotification);

	InStockNotification findByEmail(String notifyMeEmail);

	List<InStockNotification> findAllSubscribers(Long bookId);

	void notifyMeSubscriptionNotified(InStockNotification inStockNotification);

}
