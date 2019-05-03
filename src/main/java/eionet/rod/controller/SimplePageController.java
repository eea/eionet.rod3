package eionet.rod.controller;

import eionet.rod.dao.ClientService;
import eionet.rod.dao.HelpDao;
import eionet.rod.dao.IssueDao;
import eionet.rod.model.*;
import eionet.rod.service.SpatialService;
import eionet.rod.util.BreadCrumbs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Controller for simple pages.
 */

@Controller
public class SimplePageController {


    @Autowired
    IssueDao issueDao;

    @Autowired
    SpatialService spatialService;

    @Autowired
    ClientService clientService;

    @Autowired
    HelpDao helpdao;

    /**
     * Frontpage.
     *
     * @param model holder for model attributes
     * @return view name
     */
    @RequestMapping(value = "/")
    public String frontpage(Model model) {
        // This is toplevel. No breadcrumbs.
        BreadCrumbs.set(model);

        Obligations obligation = new Obligations();
        List<Spatial> countries = spatialService.findAll();
        List<Issue> issues = issueDao.findAllIssuesList();
        List<ClientDTO> clients = clientService.getAllClients();

        model.addAttribute("obligation", obligation);
        model.addAttribute("countries", countries);
        model.addAttribute("issues", issues);
        model.addAttribute("clients", clients);
        model.addAttribute("activeTab", "/");
        model.addAttribute("title", "EEA - Reporting Obligations Database");
        BreadCrumbs.set(model, "EEA - Reporting Obligations Database");

        Documentation doc = helpdao.getDoc("two_boxes");
        if (doc != null) {
            model.addAttribute("news", doc.getHtml());
        } else {
            model.addAttribute("news", null);
        }


        return "index";
    }

    /**
     * About.
     */
    @RequestMapping(value = "/about")
    public String about(Model model) {
        String title = "About";
        model.addAttribute("title", title);
        BreadCrumbs.set(model, title);
        return "about";
    }

    /**
     * Redirects to welcome page after login.
     *
     * @param model holder for model attributes
     * @return view name
     */
    @RequestMapping(value = "/login")
    public String login(Model model) {
        return frontpage(model);
    }

    /**
     * Shows page which allows to perform SingleSignOut.
     *
     * @return view name
     */
    @RequestMapping(value = "/logout")
    public String logout() {
        return "logout_all_apps";
    }

}
