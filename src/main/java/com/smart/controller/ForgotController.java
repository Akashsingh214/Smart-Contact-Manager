package com.smart.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.service.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotController {
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	//random variable to generate otp for forget password!!
	
	Random random= new Random(1000);

	// forgot password email id form open handler
	
	@RequestMapping(value="/forgot", method=RequestMethod.GET)
	public String forgot_email_form() {
		
		
		return "forgot_email_form";
	}
	
	
	//send otp handler
	
	@PostMapping("/send-otp")
	public String sendOtp(@RequestParam("email") String email, HttpSession session) {
	
		//System.out.println("Email: "+ email);
		
		//generating Otp of 4 digits
		
		int otp = random.nextInt(9999);
		
		//System.out.println("OTP: "+ otp);
		
		String subject="OTP sent from Smart Contact Manager";
		
		String text=""
				+"<div style= 'border: 1px solid #e2e2e2; padding: 20px;'>"
				+"<h1>"
				+" OTP is : "
				+" <b>"
				+otp
				+"</b>"
				+"</h1>"
				+"</div>";
		
		boolean sendEmail = this.emailService.sendEmail(email, "akash214singh@gmail.com", subject, text);
		
		if(sendEmail) {
			
			session.setAttribute("myotp", otp);
			
			session.setAttribute("email", email);
			
			return "verify_otp";
			
		}else {
			
			session.setAttribute("message", "Failed to send Email");
			
			return "forgot_email_form";
		}
		
		
	}
	
	
	//verify-otp handler
	
		@PostMapping("/verify-otp")
		public String verifyOtp(@RequestParam("otp") int otp, HttpSession session, Model model) {
		
			//Setting title for page
			
			model.addAttribute("title", "Password Update Form");
			
			
			int myotp =(int) session.getAttribute("myotp");
			
			String email= (String)session.getAttribute("email");
			
			if(otp==myotp) {
				
				//show update password form
				
				User user = this.userRepository.getUserByUserName(email);
				
				if(user==null) {
					
					//send error message
					
					session.setAttribute("message", "User does not exist with this Email");
					
					return "forgot_email_form";
					
					
				}else {
					
					
				}
				
				return "password_update_form";
				
			}else {
				
				//incorrect otp entered
				
				session.setAttribute("message", "You have entered wrong Otp");
				
				return "verify_otp";
			
			
			}
			
		}
	
		//change- password handler()
		
		@PostMapping("/change-password")
		public String changePassword(@RequestParam("newPassword") String newPassword, HttpSession session) {
			
			session.setAttribute("changePasswordMessage", "Password Changed Successfully!!");
			
			String email =(String) session.getAttribute("email");
			
			User user = this.userRepository.getUserByUserName(email);
			
			user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			
			this.userRepository.save(user);
			
			return "redirect:/signin?change=Password changed Successfully";
		}
		
		//adding GetMapping URL for Git branching...
		
		@GetMapping("/git-url")
		public String getStringResponse() {
			return "GIT is Awesome";
		}
	
}
