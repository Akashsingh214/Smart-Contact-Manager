package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;


@Controller
public class HomeController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@GetMapping("/")
	public String getHome(Model model) {
		
		model.addAttribute("title", "Home SmartContact Manager");
		return "home";
	}
	
	@GetMapping("/about")
	public String getAbout() {
		
		return "about";
	}
	
	@GetMapping("/signup")
	public String getSignup( Model model) {
		
		model.addAttribute("title", "Register- Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}
	
	/* Handler for registering user */
	
	@PostMapping("/do-register")
	public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult result1, @RequestParam(value="agreement", defaultValue="false") boolean agreement, Model model, HttpSession session) {
		
		try {
			
			if(!agreement) {
				System.out.println("You have not agreed terms and conditions!!");
				throw new Exception("You have not agreed terms and conditions!!");
			}
			
			
			if(result1.hasErrors()) {
				
				System.out.println("ERROR "+ result1.toString());
				
				model.addAttribute("user", user);
				return "signup";
			}
			
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			
			System.out.println("Agreement "+agreement);
			System.out.println("User "+ user);
			
			
			User result = this.userRepository.save(user);
			
			model.addAttribute("user", new User());
			model.addAttribute("session", session);
			
			session.setAttribute("message", new Message("Successfully registered!!", "alert-success"));
			
			return "signup";
			
			
		}catch(Exception e) {
			e.printStackTrace();
			
			model.addAttribute("user", user);
			model.addAttribute("session", session);
			
			session.setAttribute("message", new Message("Something Went Wrong!! "+ e.getMessage(), "alert-danger"));
			return "signup";
		}
		
		
	}
	
	
	//Handler for Custom-login page which we are making
	
	@GetMapping("/signin")
	public String customLogin(Model model) {
		
		model.addAttribute("title", "Login Page");
		return "login";
	}
	
}
