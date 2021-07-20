package com.smart.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;


@Service
public class EmailService {
	
	public boolean sendEmail(String subject, String message, String to)
	{
		boolean f=false;
		
		String from ="harsh071998@gmail.com";
		
		// variable for email
		
				String host="smtp.gmail.com";
				
				//get the system property
				
				Properties properties = System.getProperties();
				System.out.println("Properties: "+properties);
				
				//setting important information to properties object
				
				//host set
				properties.put("mail.smtp.host", host);
				properties.put("mail.smtp.port", "465");
				properties.put("mail.smtp.ssl.enable", "true");
				properties.put("mail.smtp.auth", "true");
				
				//step 1: to get the session object..
			     Session session =Session.getInstance(properties, new Authenticator() {
			
					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						
						return new PasswordAuthentication("abc@gmail.com", "**********");
					}
					
					
				});
			     session.setDebug(true);
			   //step 2: compose the message [test, multimedia]
			     MimeMessage m =new MimeMessage(session);
			     
			   //from email
			     try
			     {
			    	 //from email
					m.setFrom(from);
					
					//adding Recipient to Message
					m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
					
					//adding subject to message 
					m.setSubject(subject);
					
					//adding text to message
					//m.setText(message);
					m.setContent(message,"text/html");
					
					//send 
					
					//step 3:send the message using Transport class
					Transport.send(m);
					System.out.println("Send success............");
					f=true;
					
					
				 } 
			     catch (MessagingException e) 
			     {
					
					e.printStackTrace();
				 }
			     
		
		return f;
		
	}
}
