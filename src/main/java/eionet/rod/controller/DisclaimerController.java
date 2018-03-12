package eionet.rod.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import eionet.rod.util.BreadCrumbs;

@Controller
@RequestMapping("/disclaimer")
public class DisclaimerController {
	
	@RequestMapping({"", "/"})
    public String disclamerHome(Model model) {
		BreadCrumbs.set(model, "Disclaimer");
        model.addAttribute("title","Disclaimer");
        model.addAttribute("activeTab", "help");
		
        return "disclaimer";
		
	}

}
