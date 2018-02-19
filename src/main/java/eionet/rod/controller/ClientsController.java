package eionet.rod.controller;

import eionet.rod.dao.ClientService;
import eionet.rod.model.ClientDTO;
import eionet.rod.model.InstrumentDTO;
import eionet.rod.model.Obligations;
import eionet.rod.model.BreadCrumb;
import eionet.rod.util.BreadCrumbs;
import eionet.rod.util.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Client managing controller.
 */
@Controller
@RequestMapping("/clients")
public class ClientsController {

    private static BreadCrumb clientsCrumb = new BreadCrumb("/clients", "Clients");

    /**
     * Service for client management.
     */
    @Autowired
    ClientService clientService;
    
    /**
     * Page of error
     * @return
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFoundException() {
        return "404";
    }
        
    
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
        model.addAttribute("title","Clients");
        model.addAttribute("activeTab", "clients");
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
        BreadCrumbs.set(model, clientsCrumb, new BreadCrumb(client.getName()));
        
        model.addAttribute("activeTab", "clients");
       
        model.addAttribute("title",client.getName());
        
        model.addAttribute("client", client);
        
        List<Obligations> directObligations =  clientService.getDirectObligations(clientId);
        
        model.addAttribute("directObligations", directObligations);
        
        
        List<Obligations> indirectObligations =  clientService.getIndirectObligations(clientId);
        
        model.addAttribute("indirectObligations", indirectObligations);
        
        
        List<InstrumentDTO> directInstruments = clientService.getDirectInstruments(clientId);
        
        model.addAttribute("directInstruments", directInstruments);
        
        List<InstrumentDTO> indirectInstruments = clientService.getIndirectInstruments(clientId);
        
        model.addAttribute("indirectInstruments", indirectInstruments);
        
        return "clientFactsheet";
    }

    /**
     * Provide a form for a new client.
     */
    @RequestMapping("/add")
    public String addClientForm(final Model model) {
        BreadCrumbs.set(model, clientsCrumb, new BreadCrumb("Add client"));
        ClientDTO client = new ClientDTO();
        
        model.addAttribute("title","Add client");
        model.addAttribute("activeTab", "clients");
        
        model.addAttribute("client", client);
        return "clientNewClient";
    }

    /**
     * Adds new client to database.
     * @param client client name
     * @param redirectAttributes
     * @return view name or redirection
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
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
    @RequestMapping("/{clientId}/edit")
    public String editClientForm(@PathVariable("clientId") Integer clientId, final Model model,
            @RequestParam(required = false) String message) {
        model.addAttribute("clientId", clientId);
        BreadCrumbs.set(model, "Modify client");
        ClientDTO client = clientService.getById(clientId);
        model.addAttribute("client", client);
        
        model.addAttribute("title","Modify client");
        model.addAttribute("activeTab", "clients");
        
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
    @RequestMapping(value = "/{clientId}/edit", method = RequestMethod.POST)
    public String editClient(@PathVariable("clientId") Integer clientId,
            ClientDTO client, BindingResult bindingResult, ModelMap model) {
        clientService.update(client);
        model.addAttribute("message", "Client " + client.getClientId() + " updated");
        return "redirect:/clients/" + clientId + "/edit";
    }

    /**
     * Deletes client.
     * TODO: Use POST
     * @param clientName
     * @param model - contains attributes for the view
     * @return view name
     */
    @RequestMapping("/delete/{clientId}")
    public String deleteClient(@PathVariable("clientId") Integer clientId, Model model) {
        if (!clientService.clientExists(clientId)){
            model.addAttribute("message", "Client " + clientId + " was not deleted, because it does not exist");
        } else {
            clientService.deleteById(clientId);
            model.addAttribute("message", "Client " + clientId + " deleted");
        }
        return "redirect:/clients";
    }
    
    /**
	 * 
	 * @param model
	 * @return
	 */
	 @RequestMapping(value = "/delete", method = RequestMethod.POST)
	 public String deleteClients(ClientDTO client, Model model) {
		 if (client.getDelClients() != null) {
			 clientService.deleteByIds(client.getDelClients());
			 model.addAttribute("message", "Clients selected deleted.");
		 }
		 
		 return "redirect:view";
	 }
    
}
