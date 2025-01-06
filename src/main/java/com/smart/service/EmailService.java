package com.smart.service;

import java.io.File;
import java.util.Properties;

import org.springframework.stereotype.Component;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.Message.RecipientType;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

@Component
public class EmailService {

public boolean sendEmail(String to, String from, String subject, String text) {
		
		boolean flag= false;
		
		//Mail sending logic defined below......
		
		
		//Step 1: Setting mail sending smtp properties
		
		
		Properties properties= new Properties();
		
		properties.put("mail.smtp.auth", true);
		
		properties.put("mail.smtp.port", "587");
		
		properties.put("mail.smtp.host", "smtp.gmail.com");
		
		properties.put("mail.smtp.starttls.enable", true);
		
		
		
		String userName= "akash214singh";
		
		//password generated using add app-passwords
		//hhtps://myaccount.google.com/apppasswords  .....and make "EmailApi" and click generate
		
		String password= "vncflrvskmpyyjgz";
		
		//Step 2: Create session using above properties
		
	    Session session = Session.getInstance(properties, new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				
				return  new PasswordAuthentication(userName, password);
			}	
	    });
	    
	    
	    try {
	    	
	    	Message message= new MimeMessage(session);
	    	
	    	//Adding message details
	    	
	    	message.setRecipient(RecipientType.TO, new InternetAddress(to));
	    	
	    	message.setFrom(new InternetAddress(from));
	    	
	    	message.setSubject(subject);
	    	
	    	//adding text/message to message using below way....
	    	
	    	//message.setText(text);
	    	
	    	//if we want to send the Html tags to our message body in Email, we can use message.setContent()method
	    	
	    	message.setContent(text, "text/html");
	    	
	    	//sending email using above message
	    	
	    	Transport.send(message);
	    	
	    	flag=true;
	    	
	    	
	    	
	    }catch(Exception e) {
	    	
	    	e.printStackTrace();
	    }
	    
		
		return flag;
	}
	
	
	//Method for sending email with Attachments
	
	
	public boolean sendEmailWithAttachment(String to, String from, String subject, String text, File file) {
		
		boolean flag= false;
		
		//logic for sending email with attachments
		
		
		//Step 1: Defining properties for smtp(gmail.com)
		
		Properties properties= new Properties();
		
		properties.put("mail.smtp.auth", true);
		
		properties.put("mail.smtp.port", "587");
		
		properties.put("mail.smtp.host", "smtp.gmail.com");
		
		properties.put("mail.smtp.starttls.enable", true);
		
		
		String userName="akash214singh";
		
		String password="hmohmchaezosuanc";
		
		
		//Step 2: Creating session using above properties
		
		
		Session session = Session.getInstance(properties, new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				// TODO Auto-generated method stub
				return new PasswordAuthentication(userName, password);
			}
		});
		
		
		//Step 3: Adding message details and sending Email
		
		try {
			
			Message message= new MimeMessage(session);
			
			
			message.setRecipient( RecipientType.TO, new InternetAddress(to));
			
			message.setFrom(new InternetAddress(from));
			
			message.setSubject(subject);
			
			//now below we can add mutltipart files and text to be included in message to be sent
			
			MimeBodyPart part1= new MimeBodyPart();
			
			part1.setText(text);
			
			MimeBodyPart part2= new MimeBodyPart();
			
			part2.attachFile(file);
			
			//Attaching both text and file to be sent over email below
			
			MimeMultipart mimeMultiPart = new MimeMultipart();
			
			mimeMultiPart.addBodyPart(part1);
			mimeMultiPart.addBodyPart(part2);
			
			message.setContent(mimeMultiPart);
			
			Transport.send(message);
			
			flag= true;
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return flag;
	
	}
	
}
