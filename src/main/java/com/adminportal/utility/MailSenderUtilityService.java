package com.adminportal.utility;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.adminportal.domain.Book;

@Service
public class MailSenderUtilityService {

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private MailConstructor mailContructor;

	// @Async
	// public void sendOrderSubmittedEmail(Order order, User user, Locale english) {
	//
	// CompletableFuture<MimeMessagePreparator> email =
	// mailContructor.constructSimpleOrderPlacedEmail(order, user, Locale.ENGLISH);
	// try {
	// mailSender.send(email.get());
	// } catch (MailException | InterruptedException | ExecutionException e) {
	// e.printStackTrace();
	// }
	// }

	@Async
	public void sendNotifyMeEmailToSubscriber(Book book, String emailId) {

		try {
			CompletableFuture<MimeMessagePreparator> email = mailContructor.constructNotifyMeEmailToSubscriber(book,
					emailId, Locale.ENGLISH);
			mailSender.send(email.get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

	}

}
