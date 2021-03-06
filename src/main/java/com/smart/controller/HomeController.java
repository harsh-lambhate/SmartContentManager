package com.smart.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.smart.entities.User;
import com.smart.entities.dao.UserRepository;
import com.smart.helper.Message;

@Controller
public class HomeController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
 @RequestMapping({"/home","/"})
  public String home(Model m)
  {  
	 m.addAttribute("title", "Home  - Smart Contact Manager");
	  return "home";
  }
 
 @RequestMapping("/about")
 public String about(Model m)
 {  
	 m.addAttribute("title", "About  - Smart Contact Manager");
	  return "about";
 }
 
 @RequestMapping("/signup")
 public String signUp(Model m)
 {  
	 m.addAttribute("title", "Register  - Smart Contact Manager");
	 m.addAttribute("user", new User());
	  return "signup";
 }
 
 @RequestMapping(value = "/do_register",method = RequestMethod.POST)
 public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult result1,@RequestParam(value = "agreement",defaultValue = "false")boolean agreement,Model model,HttpSession session)
 {  
	 try 
	 {

		 if (!agreement) 
		{
		   	System.out.println("You have not agreed the terms and conditions");
		   	throw new Exception("You have not agreed the terms and conditions");
		}
		if(result1.hasErrors())
		{
			System.out.println("ERROR "+result1.toString());
			model.addAttribute("user", user);
			return "signup";
		}
		 user.setRole("ROLE_USER");
		 user.setEnabled(true);
		 user.setImageurl("default.png");
		 user.setPassword(passwordEncoder.encode(user.getPassword()));
		 
		 System.out.println("agreement"+agreement);
		 
		 System.out.println("user"+user);
		 
		 User result = userRepository.save(user);
		 model.addAttribute("user", new User());
		 session.setAttribute("message", new Message("Successfully Registered !!", "alert-info"));
	} 
	 catch (Exception e) 
    {
	   e.printStackTrace();	
	   model.addAttribute("user", user);
	   session.setAttribute("message", new Message("something went wrong !! "+e.getMessage(), "alert-danger"));
	}
	 return "signup";
 }

 //handle for custom login
 @RequestMapping("/signin")
 public String customLogin(Model m)
 {  
	 m.addAttribute("title", "login  - Smart Contact Manager");
	  return "login";
 }
 
}
