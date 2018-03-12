package eionet.rod.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import eionet.rod.util.BreadCrumbs;


@Controller
@RequestMapping("/help")
public class HelpDocController {
	     
	@RequestMapping({"", "/"})
    public String helpHome(Model model) {
		
		BreadCrumbs.set(model, "What is ROD");
        model.addAttribute("title","What is ROD");
        model.addAttribute("activeTab", "help");
		
        return "help";       
    }
}
