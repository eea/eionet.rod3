package eionet.rod.controller;

import eionet.rod.service.ObligationService;
import eionet.rod.service.SpatialService;
import eionet.rod.model.Spatial;
import eionet.rod.dao.ClientService;
import eionet.rod.dao.IssueDao;
import eionet.rod.model.BreadCrumb;
import eionet.rod.model.ClientDTO;
import eionet.rod.model.Issue;
import eionet.rod.model.Obligations;
import eionet.rod.util.BreadCrumbs;
import eionet.rod.util.exception.ResourceNotFoundException;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Spatial managing controller.
 */
@Controller
@RequestMapping("/spatial")
public class SpatialController {

    private static BreadCrumb spatialCrumb = new BreadCrumb("/spatial", "Countries/territories");

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFoundException() {
        return "404";
    }
    
         
    /**
     * Service for spatial management.
     */
    @Autowired
    SpatialService spatialService;

    @Autowired
    IssueDao issueDao;
    
    @Autowired
    ClientService clientService;
    
    @Autowired
    ObligationService obligationsService;
    
    /**
     * View for all countries.
     *
     * @param model - contains attributes for the view
     * @param message
     * @return view name
     */
    @RequestMapping({"", "/", "/view"})
    public String viewSpatials(Model model, @RequestParam(required = false) String message, String id) {
        BreadCrumbs.set(model, "Countries/territories");
        model.addAttribute("allMember", spatialService.findAllMember("Y"));
        Integer allMemberCount = spatialService.findAllMember("Y").size();
        model.addAttribute("allNoMember", spatialService.findAllMember("N"));
        Integer allNoMemberCount = spatialService.findAllMember("N").size();
        
        model.addAttribute("activeTab", "spatial");
        
        //calculate how many countries are in each column.
        
        Integer allMemberColumn1 = 0;
        Integer allMemberColumn2 = 0;	
        Integer allMemberColumn3 = 0;
        
        if (allMemberCount > 8) {
	        Integer allMemberRest = allMemberCount/3;
	        Integer Rest = allMemberCount % 3;
	        allMemberColumn1 = allMemberRest;
	        allMemberColumn2 = allMemberRest;
	        allMemberColumn3 = allMemberRest;
	        if(Rest>1) {
	        	allMemberColumn1 = allMemberColumn1 + 1;
	        	allMemberColumn2 = allMemberColumn2 + 1;
	        }else if(Rest==1){
	        	allMemberColumn1 = allMemberColumn1 + 1;
	        }
        }else {
        	allMemberColumn1 = allMemberCount;
        }
        
        Integer allNoMemberColumn1 = 0;
        Integer allNoMemberColumn2 = 0;
        Integer allNoMemberColumn3 = 0;
        
        if (allNoMemberCount > 8) {
	        Integer allNoMemberRest = allNoMemberCount/3;
	        Integer Rest2 = allNoMemberCount % 3;
	        allNoMemberColumn1 = allNoMemberRest;
	        allNoMemberColumn2 = allNoMemberRest;
	        allNoMemberColumn3 = allNoMemberRest;
	        if(Rest2>1) {
	        	allNoMemberColumn1 = allNoMemberColumn1 + 1;
	        	allNoMemberColumn2 = allNoMemberColumn2 + 1;
	        }else if(Rest2==1){
	        	allNoMemberColumn1 = allNoMemberColumn1 + 1;
	        }
        }else if (allNoMemberCount == 2 ){
        	allNoMemberColumn1 = 2;
        }else if (allNoMemberCount == 1) {
        	allNoMemberColumn1 = 1;
        }
        
        //end calculate
        
        model.addAttribute("allMemberColumn1", allMemberColumn1);
        model.addAttribute("allMemberColumn2", allMemberColumn2);
        model.addAttribute("allMemberColumn3", allMemberColumn3);
        
        model.addAttribute("allNoMemberColumn1", allNoMemberColumn1);
        model.addAttribute("allNoMemberColumn2", allNoMemberColumn2);
        model.addAttribute("allNoMemberColumn3", allNoMemberColumn3);
               
        
        Spatial spatial = new Spatial();
        model.addAttribute("spatial", spatial);
        model.addAttribute("title","Countries and territories"); 
        if(message != null) model.addAttribute("message", message);
        return "spatial";
    }

    @RequestMapping(value = "/{spatialId}/deadlines", method = RequestMethod.GET)
    public String spatial_deadlines(@PathVariable("spatialId") Integer spatialId, final Model model) throws Exception {
           
        model.addAttribute("allObligations",obligationsService.findObligationList("0","0",spatialId.toString(),"N","0"));
        
        
        Spatial countryName = spatialService.findOne(spatialId);
        if (countryName != null) {
        	model.addAttribute("countryName", countryName.getName());
        	model.addAttribute("countryId", spatialId);
        	model.addAttribute("title","Reporting deadlines: " +  countryName.getName()); 
            BreadCrumbs.set(model, spatialCrumb, new BreadCrumb("Reporting deadlines: " +  countryName.getName()));
        }else {
        	model.addAttribute("title","Reporting deadlines: " ); 
            BreadCrumbs.set(model, "Reporting deadlines: " );
            model.addAttribute("countryName", "");
        }
        
        Obligations obligation = new Obligations();
        model.addAttribute("obligation",obligation);
        
        
    	//Environmental issues
    	List<Issue> issues = issueDao.findAllIssuesList();
    	model.addAttribute("allIssues", issues);
    	
    	
        //Countries/territories
    	List<ClientDTO> clients = clientService.getAllClients();
    	model.addAttribute("allClients", clients);
    	
 
    	model.addAttribute("activeTab", "spatial");
        
        return "deadlines";
    	
    }
    
    @RequestMapping(value = "/{spatialId}/deadlines/search", method = RequestMethod.POST)
    public String spatial_search_deadlines(@PathVariable("spatialId") Integer spatialId, Obligations obligation, final Model model) throws Exception {
           
        model.addAttribute("allObligations",obligationsService.findObligationList(obligation.getClientId(),obligation.getIssueId(),spatialId.toString(),"N",obligation.getDeadlineId()));
        
        Spatial countryName = spatialService.findOne(spatialId);
        model.addAttribute("countryName", countryName.getName());
        model.addAttribute("countryId", spatialId);

        model.addAttribute("obligation",obligation);
        
    	//Environmental issues
    	List<Issue> issues = issueDao.findAllIssuesList();
    	model.addAttribute("allIssues", issues);
    	
        //Countries/territories
    	List<ClientDTO> clients = clientService.getAllClients();
    	model.addAttribute("allClients", clients);
   	
        model.addAttribute("title","Reporting deadlines: " +  countryName.getName()); 
        BreadCrumbs.set(model, spatialCrumb, new BreadCrumb("Reporting deadlines: " +  countryName.getName()));
        
        model.addAttribute("activeTab", "spatial");
        
        return "deadlines";
    	
    }
    
}
