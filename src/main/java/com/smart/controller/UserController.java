package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.entities.dao.ContactRepository;
import com.smart.entities.dao.UserRepository;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController 
{
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	//method for adding common data
	@ModelAttribute
	public void addcommonData(Model m,Principal principal)
	{
		String userName=principal.getName();
		System.out.println("Username: "+userName);

		//get the user using username
		
		User user = userRepository.getUserByUserName(userName);
		System.out.println("USER "+user);
		m.addAttribute("user", user);
	
	   
	}
   //dash board
	@RequestMapping("/index")
	public String dashbord(Model m,Principal principal)
	{
		m.addAttribute("title","User dashboard");
		return "normal/user_dashbord";
	}
	
	
	//add form handler
	@GetMapping("/add_contact")
	public String openContactForm(Model m)
	{
		m.addAttribute("title","Add Contact");
		m.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}
	
	//processing add contact form
	@PostMapping("/process_contact")
	public String processContact(@ModelAttribute Contact contact,
			                     @RequestParam("profileImage") MultipartFile file,
			                     Principal principal,
			                     HttpSession session)
	{
		try
	   {
	
		String name =principal.getName();
		User user=this.userRepository.getUserByUserName(name);
		
		contact.setUser(user);
		user.getContacts().add(contact);
		
		//processing and uploadig a file
		if(file.isEmpty())
		{
			//if the file is empty then try our message
			System.out.println("File is empty");
			contact.setImage("contact.png");
		}
		else 
		   {
			   //file the file to folder and updating the name to contact
			   contact.setImage(file.getOriginalFilename());
			   File saveFile = new ClassPathResource("static/img").getFile();
			   Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
			   
			   Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
			   System.out.println("Image is uploded");
			   
		   }
		
		//save data
		this.userRepository.save(user);
		
		System.out.println("data: "+contact);
		System.out.println("added to data base");
		
		//message success...
		session.setAttribute("message", new Message("Your contact is added !!","success"));
	    }
	catch (Exception e)
	   {  
		System.out.println("ERROR: "+e.getMessage());
		e.printStackTrace();
		//message error..
		session.setAttribute("message", new Message("Something went wrong !! try again... ","danger"));
	   }
		return "normal/add_contact_form";
	
	}

	                      /* applying pagination */
	                       //show contact handler
		                   //per page =5[n]
	                       //current page =0[page]	
	
	//show contact handler
	@GetMapping("/show_contacts/{page}")
	public String showContacts( @PathVariable("page") Integer page,Model m,Principal principal)
	{   
		m.addAttribute("title", "Show User Contacts");
		
		//contect ki list ko bhejni hai
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		
		//current page
		//contact per page =5
		Pageable pageable = PageRequest.of(page, 8);
		
		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(),pageable);
		
		m.addAttribute("contacts", contacts);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", contacts.getTotalPages());
		
		return"normal/show_contacts";
	}
	
	//delete contact handler
	@GetMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId")Integer cId,Model m,HttpSession session)
	{
		Optional<Contact> contantOptional = this.contactRepository.findById(cId);
		Contact contact =contantOptional.get();
		
		contact.setUser(null);
		//check...Assignment
		this.contactRepository.delete(contact);
		
		System.out.println("Delete successfully..");
		session.setAttribute("message", new Message("contact delete successfully..","success"));
		
		return "redirect:/user/show_contacts";
	}
	
	//open update form handler
	@PostMapping("/update_contact/{cId}")
	public String updateForm(@PathVariable("cId") int cId,Model m) 
	{
		m.addAttribute("title", "Update Contact");
		Contact contact = this.contactRepository.findById(cId).get();
		m.addAttribute("contact", contact);
		return "normal/update_form";
	}
	
	
	//update contact handler 
	@RequestMapping(value = "/process_update",method = RequestMethod.POST)
	public String updateHandler(Model m,Contact contact,
			                    HttpSession session,
			                    Principal principal, 
			                    @RequestParam("profileImage") MultipartFile file)
	{
		try 
		  {
			User user=this.userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);
			
			//processing and uploading a file
			if(file.isEmpty())
			{
				//if the file is empty then try our message
				System.out.println("File is empty");
				contact.setImage("contact.png");
			}
			else 
			   {
				   //file the file to folder and updating the name to contact
				   contact.setImage(file.getOriginalFilename());
				   File saveFile = new ClassPathResource("static/img").getFile();
				   Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				   
				   Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
				   System.out.println("Image is uploded");
				   
			   }
			
			Contact contact1 = this.contactRepository.save(contact);
			session.setAttribute("message", new Message("Your Contact is updated...","success"));
			
		  } catch (Exception e) 
		       {
			        e.printStackTrace();
		        }
		
		return "redirect:/user/show_contacts/0";
	}
	
	
	//showing particular contact details
		@RequestMapping("/{cId}/contact")
		public String showContant(@PathVariable("cId") Integer cId,Model m)
		{
			Optional<Contact> contactOptional = this.contactRepository.findById(cId);
			Contact contact = contactOptional.get();
			m.addAttribute("contact", contact);
			return "normal/contact_details";
		}
		
		
	//your profile handler
	@GetMapping("/profile")
	public String yourProfile(Model m,User user)
	{
		m.addAttribute("title", "Profile Page");
		m.addAttribute("user", user);
		return "normal/profile";
	}
	
	//open setting handler
	@GetMapping("/settings")
	public String openSetting()
	{
		return "normal/settings";
	}
	
	//change_password handler
	
	@PostMapping("/change_password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword,
			                     @RequestParam("newPassword") String newPassword,
			                     Principal principal,
			                     HttpSession session)
	{
		System.out.println("Old password"+oldPassword);
		System.out.println("new password"+newPassword);
		
		String userName = principal.getName();
		User currentUser = this.userRepository.getUserByUserName(userName);
		System.out.println("this is currentUser value "+currentUser.getPassword());
		
		if (this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword()))
		{
		  //change the password
		  currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
		  this.userRepository.save(currentUser);
		  session.setAttribute("message", new Message("Your password is successfully change..","alert-success"));
		}
		else {
			//error
			session.setAttribute("message", new Message("please Enter your correct old password..","alert-danger"));
			return "redirect:/user/settings";
		}
		
		return "redirect:/user/index";
	}
	
}

