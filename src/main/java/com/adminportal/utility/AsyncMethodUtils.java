package com.adminportal.utility;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.adminportal.domain.Book;
import com.adminportal.domain.InStockNotification;
import com.adminportal.service.InStockNotificationService;

@Component
public class AsyncMethodUtils {
	
	@Autowired
	private InStockNotificationService inStockNotificationService;
	
	@Autowired
	private MailSenderUtilityService mailSenderUtilityService;
	
	@Async
	public void notifyMeImplementation(Long bookId, Book book) {
		List<InStockNotification> inStockNotificationList = inStockNotificationService.findAllSubscribers(bookId);
		System.out.println("###################################### inStockNotificationList.size() "+ inStockNotificationList.size());
		
		if(inStockNotificationList.size() > 0) {
			for(InStockNotification inStockNotification : inStockNotificationList) {
				// Step 1.  Send the email to the subscriber
				
				System.out.println("###################################### inStockNotification.getEmail() "+ inStockNotification.getEmail());
				mailSenderUtilityService.sendNotifyMeEmailToSubscriber(book, inStockNotification.getEmail());
				
				// Step 2. Mark notified true and check if without saving it, would it reflect in DB ????
				inStockNotificationService.notifyMeSubscriptionNotified(inStockNotification);
				System.out.println("######################################  NOTIFIED");
				// If: it is not reflecting in DB then save it or get some other workaround.
			}
		}
	}


}
