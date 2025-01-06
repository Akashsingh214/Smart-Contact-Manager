package com.smart.controller;


import com.razorpay.Order;
import com.razorpay.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;


@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	//Getting data of User which can be used in all handlers below
	
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		
		String userName = principal.getName();
		
		System.out.println("USERNAME :"+userName);
		
		//Now we can get the user using username(email) which is stored in userName
		//we have to use userRepository.java(dao interface) to fetch details from database and that
		//userRepository.java can be autowired
		
		User user= userRepository.getUserByUserName(userName);
		
		model.addAttribute("user", user);
		model.addAttribute("userName", user.getName());
		model.addAttribute("email", user.getEmail());
		
	}

	@GetMapping("/index")
	public String dashboard( ) {
		
		
		
		return "normal/user_dashboard";
	}
	
	
	// Add Contact form Handler
	
	@GetMapping("/add-contact")
	public String addContact(Model model) {
		
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		
		return "normal/add_contact_form";
	}
	
	
	//Processing Add contact form handler
	
	@PostMapping("/process-contact")
	public String processAddContactForm(@ModelAttribute Contact contact, 
			@RequestParam("profileImage") MultipartFile file ,Principal principal, HttpSession session) {
		
		try {
		String email = principal.getName();
		
		User user = this.userRepository.getUserByUserName(email);
		
		//processing and uploading file(image) in Add contact form
		
		if(file.isEmpty()) {
			//print your message here
			
			System.out.println("File is Empty!!");
			
			contact.setImage("contact.png");
			
		}else {
			
			//upload the file to folder and update the name to contact
			
			contact.setImage(file.getOriginalFilename());
			
			File saveFile = new ClassPathResource("static/img/").getFile();
			
			Path path= Paths.get(saveFile.getAbsolutePath()+File.separator+ file.getOriginalFilename());
			
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING );
			
			System.out.println("Image is Uploaded!!");
			
		}
		
		
		
		contact.setUser(user);
		
	    user.getContacts().add(contact);
	    
	    
	    
	    
	    
	    this.userRepository.save(user);
	    
	    
		
		System.out.println("DATA: "+ contact);
		
		System.out.println("Added to Database!!");
		
		//message success using (message helper object) which we have made
		// new Message(String content, String type);
		
		session.setAttribute("message", new Message("Your Contact has been created!! Add More...","success"));
		
		
		}catch(Exception e) {
			
			System.out.println("Error : "+ e.getMessage());
			
			session.setAttribute("message", new Message("Something went wrong!! Try Again...","danger"));
			
		}
		
		return "normal/add_contact_form";
		
		
	}
	
	
	//show all contacts handler
	//per page display 5 contacts
	//current page= 0 (denoted by page)
	
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") int page ,Model model, Principal principal) {
		
		String userName = principal.getName();
		
		User user = this.userRepository.getUserByUserName(userName);
		
		PageRequest pageRequest = PageRequest.of(page, 5);
		
		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), pageRequest);
		
		
		
		model.addAttribute("title", "Show Contacts page");
		
		
		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());
		
		return "normal/show_contacts";
	}
	
	
	//showing particular contact details
	
	@GetMapping("/contact/{cid}")
	public String showContactDetail(@PathVariable("cid") int cid, Model model, Principal principal) {
		
		
		Optional<Contact> contactOptional = this.contactRepository.findById(cid);
		
		Contact contact = contactOptional.get();
		
		//validation for any user to only see his/ her contact and not of others by manipulating URL's
		
		String userName = principal.getName();
		
		User user = this.userRepository.getUserByUserName(userName);
		
		
		if(user.getId()== contact.getUser().getId()) 
			model.addAttribute("contact", contact);	
		
		
		//end of validation
		
		
		return "normal/contact_detail";
	}
	
	
	//delete contact handler
	
	@GetMapping("/delete/{cid}")
	@Transactional
	public String deleteContact(@PathVariable("cid") int cid, Principal principal, Model model, HttpSession session) {
		
		Optional<Contact> contactOptional = this.contactRepository.findById(cid);
		
		Contact contact = contactOptional.get();
		
		//providing validation to check that a person only deletes his/her contact
		
		String userName = principal.getName();
		
		User user = this.userRepository.getUserByUserName(userName);
		
		if(user.getId()==contact.getUser().getId()) {
			//unlinking user attached to contact to prevent error because of mapping issues(tables are linked)
			
			contact.setUser(null);
			
			//removing the picture of contact
			
			contact.setImage(null);
			
			this.contactRepository.delete(contact);
			
		}
		
		
		
		session.setAttribute("message", new Message("Your Contact has been Deleted!!", "success"));
		
		
		return "redirect:/user/show-contacts/0";
	}
	
	//open "update-form" handler
	@PostMapping("/update-contact/{cid}")
	public String updateContact(@PathVariable("cid") int cid,Model model) {
		
		model.addAttribute("title", "Update Contact Page");
		
		Optional<Contact> contactOptional = this.contactRepository.findById(cid);
		
		Contact contact = contactOptional.get();
		
		model.addAttribute("contact", contact);
		
		return "normal/update_form";
	}
	
	
	//Processing "Update Contact" form handler
	@PostMapping("/process-update")
	@Transactional
	public String processUpdateContact(@ModelAttribute Contact contact, 
			@RequestParam("profileImage") MultipartFile file, Model model, HttpSession session, Principal principal) {
		
		
		try {
			
			Contact oldContactDetail = this.contactRepository.findById(contact.getCid()).get();
		
			if(!file.isEmpty()) {
				
				//file work to be done.....rewrite file
				
				//delete Old photo
				
				File deleteFile= new ClassPathResource("static/img").getFile();
				
				File file1= new File(deleteFile, oldContactDetail.getImage());
				
				file1.delete();
				
				
				
				//update Old photo
				
				File saveFile = new ClassPathResource("static/img/").getFile();
				
				Path path= Paths.get(saveFile.getAbsolutePath()+File.separator+ file.getOriginalFilename());
				
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING );
				
				contact.setImage(file.getOriginalFilename());
				
				
				
			}else {
				
				contact.setImage(oldContactDetail.getImage());
			}
			
			//Getting user to update in the Contact form
			
			String email = principal.getName();
			
			User user = this.userRepository.getUserByUserName(email);
			
			contact.setUser(user);
			
			
			this.contactRepository.save(contact);
			
			session.setAttribute("message", new Message("Contact Updated Successfully!!","success"));
			
			
			
		}catch (Exception e) {
			
			e.printStackTrace();
			session.setAttribute("message", new Message("Oops Something went wrong!! Try Again...","danger"));
		
		}
		
		return "redirect:/user/contact/"+ contact.getCid();
		
	}
	
	
	//Showing "Your profile" handler
	
	@GetMapping("/profile")
	public String showProfile(Model model) {
		
		model.addAttribute("title", "Profile Page");
		
		return "normal/profile";
	}
	
	//open settings Handler
	
	@GetMapping("/settings")
	public String openSettings() {
		
		
		
		return "normal/settings";
	}
	
	//change-password handler
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword, Principal principal, HttpSession session) {
		
		//First get password from database and compare it with old password and then proceed to change
		
		String userName = principal.getName();
		
		User currentUser = this.userRepository.getUserByUserName(userName);
		
		//we can now get old password from above user we have got from database
		
		String password= currentUser.getPassword();
		
		if(this.bCryptPasswordEncoder.matches(oldPassword, password)) {
			
			//change the old password to new password....
			
			currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			
			this.userRepository.save(currentUser);
			
			session.setAttribute("message", new Message("Your Password has been successfully changed...", "success"));
			
		}else {
			
			//error...
			
			session.setAttribute("message", new Message("You have Entered wrong Password, so cannot change to new Password....", "danger"));
			
			return "redirect:settings";
			
		}
		
		
		return "redirect:index";
	}
	
	// creating order for payment
	
	@PostMapping("/create-order")
	@ResponseBody
	public String createOrder(@RequestBody Map<String, Object> data) throws RazorpayException {
		
		
		int amt = Integer.parseInt(data.get("amount").toString());
		
		
		RazorpayClient razorpayClient= new RazorpayClient("rzp_test_2ksHbAkbx28n0p", "K2zX34q5asQG7n44sRz9Cce9");
		
		JSONObject object= new JSONObject();
		
		object.put("amount", amt*100);
		object.put("currency","INR");
		object.put("receipt", "txn_2303738");
		
		//now creating new order for razorpay endpoint
		
		Order order = razorpayClient.Orders.create(object);
		
		System.out.println(order);
		
		//if you want, you can save this order into your database....
		
		
		return order.toString();
	}
	
	
	
	
}
