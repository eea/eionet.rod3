package eionet.rod.controller;

import eionet.rod.util.BreadCrumbs;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Controller for simple pages.
 */

@Controller
public class SimplePageController {

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
