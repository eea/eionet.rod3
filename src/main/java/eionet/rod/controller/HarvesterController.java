package eionet.rod.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import eionet.rod.Constants;
import eionet.rod.IAuthenticationFacade;

import eionet.rod.extractor.Extractor;

import eionet.rod.util.RODUtil;


@Controller
@RequestMapping("/harvester")
public class HarvesterController {
	
	@Autowired
    IAuthenticationFacade authenticationFacade;

	@Autowired
	Extractor extractor;
	
	@RequestMapping({"", "/", "/view"})
	 public String harvest(Model model, @RequestParam(required = false) String message) {
		 Authentication authentication = authenticationFacade.getAuthentication();
			String userName = authentication.getName();
			
	        if (RODUtil.isNullOrEmpty(userName)) {
	        	String messageLogin = "You are not logged in!" + " - " + Constants.SEVERITY_WARNING;
	        	model.addAttribute("message", messageLogin);
	        	return "harvester";
	        }
	        
	        model.addAttribute("mode" , "");
	        
	        if(message != null) model.addAttribute("message", message);
	        return "harvester";
	 }
	
	 @RequestMapping(method = RequestMethod.POST)
	 public String harvest(String mode, RedirectAttributes redirectAttributes, Model model, @RequestParam(required = false) String message) {
		
	    Authentication authentication = authenticationFacade.getAuthentication();
		String userName = authentication.getName();
        if (RODUtil.isNullOrEmpty(userName)) {
        	String messageLogin = "You are not logged in!" + " - " + Constants.SEVERITY_WARNING;
       	 	model.addAttribute("message", messageLogin);
       	 	return "harvester";
        }

        try {
        	//Comment only for test in my PC
//            AccessControlListIF acl = AccessController.getAcl(Constants.ACL_HARVEST_NAME);
//            boolean perm = acl.checkPermission(userName, Constants.ACL_INSERT_PERMISSION);
//            if (!perm) {
//            	String messagePerm = "Insufficient permissions" + " - " + Constants.SEVERITY_WARNING;
//    			model.addAttribute("message", messagePerm);    	
//            	return "harvester";
//            }

            int m = Integer.parseInt(mode);
            //Extractor ext = new Extractor();

            
            extractor.harvest(m, userName);
            
            model.addAttribute("mode" , mode);
            
            String messageResult = "Harvested!"; // See log for details";
            model.addAttribute("message", messageResult);
        } catch (Exception e) {
        	
        	String messageExcep = e.toString() + " - " + Constants.SEVERITY_WARNING;
            model.addAttribute("message", messageExcep);
        }

        if(message != null) model.addAttribute("message", message);
		return "harvester";
	 }
	 
	 
	 
}
