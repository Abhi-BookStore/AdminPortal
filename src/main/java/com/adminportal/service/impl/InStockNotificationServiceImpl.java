package com.adminportal.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adminportal.domain.InStockNotification;
import com.adminportal.repository.InStockNotificationRepository;
import com.adminportal.service.InStockNotificationService;


@Service
public class InStockNotificationServiceImpl implements InStockNotificationService {

	@Autowired
	private InStockNotificationRepository inStockNotificationRepository;
	
	@Override
	public void save(InStockNotification inStockNotification) {
		inStockNotificationRepository.save(inStockNotification);
	}

	@Override
	public InStockNotification findByEmail(String notifyMeEmail) {
		return inStockNotificationRepository.findByEmail(notifyMeEmail);
	}

	@Override
	public List<InStockNotification> findAllSubscribers(Long bookId) {
		
		List<InStockNotification> subscriberList = inStockNotificationRepository.findByBookIdAndIsNotifyMe(bookId);
		
		return subscriberList;
	}

	@Override
	public void notifyMeSubscriptionNotified(InStockNotification inStockNotification) {
		
		InStockNotification toBeNotified = inStockNotificationRepository.findByEmailIdAndBookId(inStockNotification.getEmail(), inStockNotification.getBookId());
		
		toBeNotified.setNotified(true);
		inStockNotificationRepository.save(toBeNotified);
	}
	
	

}
