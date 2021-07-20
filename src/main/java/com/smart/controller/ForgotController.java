package com.smart.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.entities.User;
import com.smart.entities.dao.UserRepository;
import com.smart.service.EmailService;

@Controller
public class ForgotController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private BCryptPasswordEncoder bCrypt;
	
	Random random =new Random(1000);

	//email id form open handler
	@RequestMapping("/forgot")
	public String openEmailForm()
	{
		return "forgot_email_form";
	}
	
	
	//email id form open handler
	@PostMapping("/send_otp")
	public String sendOTP(@RequestParam("email") String email,
			               HttpSession session)
	{
		//generating otp of 4 digit
		int otp = random.nextInt(999999);
		System.out.println("otp"+otp);
		System.out.println("email"+email);
			
		//write code for send otp to email
		
		String subject="OTP from Smart Contact Manager";
		String message=""
				+ "<div style='border:1px solid #e2e2e2; padding:20px;'>"
				+ "<h1>"
				+ "Your OTP is"
				+ "<b>"+otp+ "</n>"
				+ "</h1>"
				+ "</div>";
		String to=email;
		
		boolean flag = this.emailService.sendEmail(subject, message, to);
		if(flag)
		{
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);
			return "verify_otp";
		}
		else {
			
			session.setAttribute("message", "Check your email id !!");
			return "forgot_email_form";
		}
	}
	
	//verify otp 
	@RequestMapping(value="/verify_otp",method = RequestMethod.POST)
	public String verifyotp(@RequestParam("otp") int otp,HttpSession session )
	{
		int myOtp =(int)session.getAttribute("myotp");
		System.out.println("otp"+otp);
		System.out.println("myotp"+myOtp);
		
		String email=(String)session.getAttribute("email");
		System.out.println("email"+email);
		
		if(myOtp==otp)
		{
			//password change form
			User user = this.userRepository.getUserByUserName(email);
			System.out.println("user ki email "+email);
			
			if (user==null) 
			{
			 //send error message
				session.setAttribute("message", "User dost not exist this email !!");
			    return "forgot_email_form";
			}
			else 
			{
				//send change password
				return "password_change_form";	
			}
		}
		else {
			
			session.setAttribute("message", "You have entered wrong otp");
		    return "verify_otp";
		}
	}
	
	//change_passowrd
	@PostMapping("/change_passowrd")
	public String change_passowrd(@RequestParam("newpassword")String newpassword,
			                      HttpSession session )
	{
		String email=(String)session.getAttribute("email");
		User user = this.userRepository.getUserByUserName(email);
		user.setPassword(this.bCrypt.encode(newpassword));
		this.userRepository.save(user);
		
		return "redirect:/signin?change=password changed successfully..";
	}
	
}