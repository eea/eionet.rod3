package eionet.rod.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import eionet.rod.util.BreadCrumbs;

@Controller
@RequestMapping("/RSSHelp")
public class RSSHelpController {
	
	@RequestMapping({"", "/"})
    public String rssHelpHome(Model model) {
		
		BreadCrumbs.set(model, "RSS XML/RPC data extraction help text");
        model.addAttribute("title","RSS XML/RPC data extraction help text");
        model.addAttribute("activeTab", "help");
		
		return "rssHelp";
	}

}
