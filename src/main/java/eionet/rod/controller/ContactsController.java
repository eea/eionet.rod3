package eionet.rod.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/contacts")
public class ContactsController {
	
	 @RequestMapping(method = RequestMethod.GET)
	 public String contacts(String roleId, RedirectAttributes redirectAttributes, Model model) {
		 
		 
		 
		 model.addAttribute("activeTab", "spatial");
		 return "contacts";
	 }

}
