package eionet.rod.controller;

import eionet.rod.util.BreadCrumbs;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/RSSHelp")
public class RSSHelpController {

    @RequestMapping({"", "/"})
    public String rssHelpHome(Model model) {

        BreadCrumbs.set(model, "RDF extraction help text");
        model.addAttribute("title", "RDF data extraction help text");
        model.addAttribute("activeTab", "help");

        return "rssHelp";
    }

}
