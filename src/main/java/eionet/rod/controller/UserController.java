package eionet.rod.controller;

import eionet.rod.model.Authorisation;
import eionet.rod.model.UserRole;
import eionet.rod.service.UserManagementService;
import eionet.rod.util.BreadCrumbs;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 * User managing controller.
 */
@Controller
@RequestMapping("/users")
public class UserController {

    private static final Log logger = LogFactory.getLog(UserController.class);


    /**
     * Service for user management.
     */
    @Autowired
    UserManagementService userManagementService;

    @ModelAttribute("allRoles")
    public List<String> allRoles() {
        ArrayList<String> grantedAuthorities = new ArrayList<>();
        for (UserRole authority : UserRole.values()) {
            grantedAuthorities.add(authority.toString());
        }
        return grantedAuthorities;
    }

    /**
     * View for all users.
     *
     * @param model   - contains attributes for the view
     * @param message
     * @return view name
     */
    @RequestMapping({"", "/", "/view"})
    public String viewUsers(Model model, @RequestParam(required = false) String message) {
        BreadCrumbs.set(model, "Users");
        model.addAttribute("allUsers", userManagementService.getAllUsers());
        model.addAttribute("activeTab", "users");
        Authorisation user = new Authorisation();
        model.addAttribute("user", user);
        if (message != null) model.addAttribute("message", message);
        return "users";
    }

    /**
     * Adds new user to database.
     *
     * @param user               user name
     * @param redirectAttributes
     * @return view name or redirection
     */
    @RequestMapping("/add")
    public String addUser(Authorisation user, RedirectAttributes redirectAttributes) {
        String userName = user.getUserId();
        if ("".equals(userName.trim())) {
            redirectAttributes.addFlashAttribute("message", "User's username cannot be empty");
            return "redirect:view";
        }

        ArrayList<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>();
        if (user.getAuthorisations() != null) {
            for (String authority : user.getAuthorisations()) {
                grantedAuthorities.add(new SimpleGrantedAuthority(authority));
            }
        }
        User userDetails = new User(user.getUserId(), "", grantedAuthorities);
        if (userManagementService.userExists(userName)) {
            redirectAttributes.addFlashAttribute("message", "User " + userName + " already exists");
            return "redirect:view";
        }
        String message = "User " + user.getUserId() + " added with " + rolesAsString(user.getAuthorisations());
        logger.info(message);

        userManagementService.createUser(userDetails);
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:view";
    }

    private String rolesAsString(List<String> authorisations) {
        if (authorisations == null) {
            return "no roles";
        } else {
            //return String.join(", ", authorisations); // Only works on Java 8
            return "roles " + StringUtils.collectionToDelimitedString(authorisations, ", ");
        }
    }

    /**
     * Form for editing existing user.
     *
     * @param userName
     * @param model    - contains attributes for the view
     * @param message
     * @return view name
     */
    @RequestMapping("/edit")
    public String editUserForm(@RequestParam String userName, Model model,
                               @RequestParam(required = false) String message) {
        model.addAttribute("activeTab", "users");
        model.addAttribute("userName", userName);
        BreadCrumbs.set(model, "Modify user");
        UserDetails userDetails = userManagementService.loadUserByUsername(userName);

        ArrayList<String> userRoles = new ArrayList<>();
        for (GrantedAuthority authority : userDetails.getAuthorities()) {
            userRoles.add(authority.getAuthority());
        }
        Authorisation user = new Authorisation(userName, userRoles);
        model.addAttribute("user", user);
        if (message != null) model.addAttribute("message", message);
        return "userEditForm";
    }

    /**
     * Save user record to database.
     *
     * @param user
     * @param bindingResult
     * @param model         - contains attributes for the view
     * @return view name
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String editUser(Authorisation user, BindingResult bindingResult, ModelMap model) {
        ArrayList<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>();
        if (user.getAuthorisations() != null) {
            for (String authority : user.getAuthorisations()) {
                grantedAuthorities.add(new SimpleGrantedAuthority(authority));
            }
        }
        User userDetails = new User(user.getUserId(), "", grantedAuthorities);
        userManagementService.updateUser(userDetails);
        String message = "User " + user.getUserId() + " updated with "
                + rolesAsString(user.getAuthorisations());
        logger.info(message);
        model.addAttribute("message", message);
        return "redirect:view";
    }

    /**
     * Deletes user.
     *
     * @param userName
     * @param model    - contains attributes for the view
     * @return view name
     */
    @RequestMapping("/delete")
    public String deleteUser(@RequestParam String userName, Model model) {
        if (!userManagementService.userExists(userName)) {
            model.addAttribute("message", "User " + userName + " was not deleted, because it does not exist ");
        } else {
            userManagementService.deleteUser(userName);
            String message = "User " + userName + " deleted";
            model.addAttribute("message", message);
            logger.info(message);
        }
        return "redirect:view";
    }
}
