package com.adminportal.utility;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.adminportal.domain.Book;


@Component
public class MailConstructor {

	@Autowired
	private Environment env;
	
	@Autowired
	private TemplateEngine templateEngine;

	@Async
	public CompletableFuture<MimeMessagePreparator> constructNotifyMeEmailToSubscriber(Book book, String emailId,
			Locale english) {
		
		Context context = new Context();
		context.setVariable("book", book);
		context.setVariable("emailId", emailId);
		String text = templateEngine.process("notifyMeEmailTemplate", context);
		
		MimeMessagePreparator messagePreparator = new MimeMessagePreparator() {
			
			@Override
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper email = new MimeMessageHelper(mimeMessage, true);
				email.setTo(emailId);
				email.setFrom(new InternetAddress("abhibookstore123@gmail.com"));
				email.setSubject("Back In Stock - "+ book.getTitle());
				email.setText(text, true);
			}
		};
		
		return CompletableFuture.completedFuture(messagePreparator);
	}
	

}
