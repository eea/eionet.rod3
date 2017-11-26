package eionet.rod.controller;

import eionet.rod.dao.ClientService;
import eionet.rod.model.ClientDTO;
import eionet.rod.util.BreadCrumbs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 * Client managing controller.
 */
@Controller
@RequestMapping("/clients")
public class ClientsController {

    /**
     * Service for client management.
     */
    @Autowired
    ClientService clientService;

    /**
     * View for all clients.
     *
     * @param model - contains attributes for the view
     * @param message
     * @return view name
     */
    @RequestMapping({"", "/", "/view"})
    public String viewClients(Model model, @RequestParam(required = false) String message) {
        BreadCrumbs.set(model, "Clients");
        model.addAttribute("allClients", clientService.getAllClients());
        ClientDTO client = new ClientDTO();
        model.addAttribute("client", client);
        if(message != null) model.addAttribute("message", message);
        return "clients";
    }

    /**
     * Client factsheet.
     */
    @RequestMapping(value = "/{clientId}")
    public String clientFactsheet(
            @PathVariable("clientId") Integer clientId, final Model model) throws Exception {
        model.addAttribute("clientId", clientId);
        ClientDTO client = clientService.getById(clientId);
        model.addAttribute("client", client);
        return "clientFactsheet";
    }

    /**
     * Adds new client to database.
     * @param client client name
     * @param redirectAttributes
     * @return view name or redirection
     */
    @RequestMapping("/add")
    public String addClient(ClientDTO client, RedirectAttributes redirectAttributes) {
        String clientName = client.getName();
        if (clientName.trim().equals("")) {
            redirectAttributes.addFlashAttribute("message", "Client name cannot be empty");
            return "redirect:view";
        }

        clientService.insert(client);
        redirectAttributes.addFlashAttribute("message", "Client " + client.getName() + " added");
        return "redirect:view";
    }

    /**
     * Form for editing existing client.
     * @param clientName
     * @param model - contains attributes for the view
     * @param message
     * @return view name
     */
    @RequestMapping("/edit")
    public String editClientForm(@RequestParam Integer clientId, Model model,
            @RequestParam(required = false) String message) {
        model.addAttribute("clientId", clientId);
        BreadCrumbs.set(model, "Modify client");
        ClientDTO client = clientService.getById(clientId);
        model.addAttribute("client", client);
        if (message != null) model.addAttribute("message", message);
        return "clientEditForm";
    }

    /**
     * Save client record to database.
     *
     * @param client
     * @param bindingResult
     * @param model - contains attributes for the view
     * @return view name
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String editClient(ClientDTO client, BindingResult bindingResult, ModelMap model) {
        clientService.update(client);
        model.addAttribute("message", "Client " + client.getClientId() + " updated");
        return "redirect:view";
    }

    /**
     * Deletes client.
     *
     * @param clientName
     * @param model - contains attributes for the view
     * @return view name
     */
    @RequestMapping("/delete")
    public String deleteClient(@RequestParam Integer clientId, Model model) {
        if (!clientService.clientExists(clientId)){
            model.addAttribute("message", "Client " + clientId + " was not deleted, because it does not exist ");
        } else {
            clientService.deleteById(clientId);
            model.addAttribute("message", "Client " + clientId + " deleted ");
        }
        return "redirect:view";
    }
}
