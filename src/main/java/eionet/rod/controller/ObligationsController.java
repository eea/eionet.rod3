package eionet.rod.controller;

import eionet.rod.model.BreadCrumb;
import eionet.rod.model.ClientDTO;
import eionet.rod.model.Issue;
import eionet.rod.model.ObligationCountry;
import eionet.rod.dao.ClientService;
import eionet.rod.dao.IssueDao;
import eionet.rod.model.Obligations;
import eionet.rod.model.SiblingObligation;
import eionet.rod.model.Spatial;
import eionet.rod.service.ObligationService;
import eionet.rod.service.SpatialService;
import eionet.rod.util.BreadCrumbs;
import eionet.rod.util.RODUtil;
import eionet.rod.util.exception.ResourceNotFoundException;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.validation.BindingResult;
//import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;



/**
 * Spatial managing controller.
 */
@Controller
@RequestMapping("/obligations")
public class ObligationsController {

    private static BreadCrumb obligationCrumb = new BreadCrumb("/obligations", "Obligations");
 
    /**
     * Page of error
     * @return
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFoundException() {
        return "404";
    }
    
         
    /**
     * Service for spatial management.
     */
    @Autowired
    ObligationService obligationsService;
    
    @Autowired
    IssueDao issueDao;
    
    @Autowired
    SpatialService spatialService;
    
    @Autowired
    ClientService clientService;

    /**
     * View for all obligations.
     *
     * @param model - contains attributes for the view
     * @param message
     * @return view name
     */
    @RequestMapping({"", "/", "/view"})
    public String viewObligations(Model model, @RequestParam(required = false) String message) {
        BreadCrumbs.set(model,"Reporting obligations");
        
        Obligations obligation = new Obligations();
        
        
        model.addAttribute("allObligations", obligationsService.findObligationList("0","0","0","","0"));

        model.addAttribute("title","Reporting obligations");
        
      //Environmental issues
    	List<Issue> issues = issueDao.findAllIssuesList();
    	model.addAttribute("allIssues", issues);
    	
    	//Countries/territories
    	List<Spatial> countries = spatialService.findAll();
    	model.addAttribute("allCountries", countries);
        
    	//Countries/territories
    	List<ClientDTO> clients = clientService.getAllClients();
    	model.addAttribute("allClients", clients);
    	
        model.addAttribute("activeTab", "obligations");
        
        model.addAttribute("obligation",obligation);
        
        if(message != null) model.addAttribute("message", message);
        return "obligations";
    }

    /**
     * 
     * @param obligations
     * @param model
     * @return view obligations
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
	public String searchObligation(Obligations obligations, Model model) {
    	
    	 model.addAttribute("allObligations", obligationsService.findObligationList(obligations.getClientId(),obligations.getIssueId(),obligations.getSpatialId(),obligations.getTerminate(),"0"));

         model.addAttribute("title","Reporting obligations");
         
       //Environmental issues
     	List<Issue> issues = issueDao.findAllIssuesList();
     	model.addAttribute("allIssues", issues);
     	
     	//Countries/territories
     	List<Spatial> countries = spatialService.findAll();
     	model.addAttribute("allCountries", countries);
         
     	//Countries/territories
     	List<ClientDTO> clients = clientService.getAllClients();
     	model.addAttribute("allClients", clients);
     	
         model.addAttribute("activeTab", "obligations");
         
         model.addAttribute("obligation",obligations);
         
         return "obligations";
    }
   
    /**
     * obligations details by ID (overview)
     */
    @RequestMapping(value = "/{obligationId}")
    public String obligation_overview(@PathVariable("obligationId") Integer obligationId, final Model model) throws Exception {
        model.addAttribute("obligationId", obligationId);
        Obligations obligation = obligationsService.findOblId(obligationId);
        BreadCrumbs.set(model, obligationCrumb, new BreadCrumb(obligation.getOblTitle()));
        model.addAttribute("obligation", obligation);
        model.addAttribute("title",RODUtil.replaceTags(obligation.getOblTitle())); 
        if (obligation.getReportFreqMonths() == null)
        	obligation.setReportFreqMonths("");
        
        obligation.setOblTitle(RODUtil.replaceTags(obligation.getOblTitle()));
        
        obligation.setDescription(RODUtil.replaceTags(obligation.getDescription()));
        
        obligation.setReportingFormat(RODUtil.replaceTags(obligation.getReportingFormat()));
        
        obligation.setCoordinator(RODUtil.replaceTags(obligation.getCoordinator()));
        obligation.setCoordinatorRole(RODUtil.replaceTags(obligation.getCoordinatorRole()));
        obligation.setCoordRoleId(RODUtil.replaceTags(obligation.getCoordRoleId()));
        obligation.setCoordRoleName(RODUtil.replaceTags(obligation.getCoordRoleName()));
        obligation.setCoordinatorUrl(RODUtil.replaceTags(obligation.getCoordinatorUrl(),true,true));
        
        obligation.setNationalContactUrl(RODUtil.replaceTags(obligation.getNationalContactUrl(),true,true));
        obligation.setNationalContact(RODUtil.replaceTags(obligation.getNationalContact()));
        obligation.setRespRoleId(RODUtil.replaceTags(obligation.getRespRoleId()));
        obligation.setRespRoleName(RODUtil.replaceTags(obligation.getRespRoleName()));
        obligation.setResponsibleRole(RODUtil.replaceTags(obligation.getResponsibleRole()));
        
        obligation.setSourceAlias(RODUtil.replaceTags(obligation.getSourceAlias()));
        obligation.setSourceTitle(RODUtil.replaceTags(obligation.getSourceTitle()));
       
      
        //List of clients with status = C
        String status = "C";
        List<ClientDTO> clients = clientService.findOblClients(obligationId,status);
       
        if (clients != null)
        	model.addAttribute("clients", clients);
       
        model.addAttribute("activeTab", "obligations");
        
        return "obligation_overview";
    }
    
    
    /**
     * obligations details by ID (overview)
     */
    @RequestMapping(value = "/{obligationId}/legislation")
    public String obligation_legislation(@PathVariable("obligationId") Integer obligationId, final Model model) throws Exception {
    	model.addAttribute("obligationId", obligationId);
    	
    	Obligations obligation = obligationsService.findOblId(obligationId);
        BreadCrumbs.set(model, obligationCrumb, new BreadCrumb(obligation.getOblTitle()));
        model.addAttribute("obligation", obligation);
        model.addAttribute("title",RODUtil.replaceTags(obligation.getOblTitle())); 
        
        obligation.setSourceAlias(RODUtil.replaceTags(obligation.getSourceAlias()));
        obligation.setSourceTitle(RODUtil.replaceTags(obligation.getSourceTitle()));
        obligation.setComment(RODUtil.replaceTags(obligation.getComment()));
        
        //Sibling reporting obligations
        List<SiblingObligation> siblingObligations = obligationsService.findSiblingObligations(obligationId);
        model.addAttribute("siblingObligations", siblingObligations);
        
        //Environmental issues
    	List<Issue> issues = issueDao.findObligationIssuesList(obligationId);
    	model.addAttribute("issues", issues);
    	
    	//Participating countries/territories
    	List<ObligationCountry> ObligationCountries = spatialService.findObligationCountriesList(obligationId);
    	model.addAttribute("ObligationCountries", ObligationCountries);
    	
    	model.addAttribute("activeTab", "obligations");
        
    	return "obligation_legislation";
    }
    
    /**
     * 
     * @param obligationId
     * @param model
     * @return vie eobligation (edit)
     * @throws Exception
     */
    @RequestMapping("/{obligationId}/edit")
    public String editClientForm(@PathVariable("obligationId") Integer obligationId, final Model model) throws Exception {
        model.addAttribute("obligationId", obligationId);
        model.addAttribute("activeTab", "obligations");
        
        BreadCrumbs.set(model, obligationCrumb, new BreadCrumb("Edit obligation"));
        Obligations obligations = obligationsService.findOblId(obligationId);
        
       
        //init var ListBox
        List<String> selectedClients = new ArrayList<String>();
        obligations.setSelectedClients(selectedClients);
        List<String> selectedVoluntaryCountries= new ArrayList<String>();
        obligations.setSelectedVoluntaryCountries(selectedVoluntaryCountries);
        List<String> selectedFormalCountries= new ArrayList<String>();
        obligations.setSelectedFormalCountries(selectedFormalCountries);
        List<String> selectedIssues= new ArrayList<String>();
        obligations.setSelectedIssues(selectedIssues);
        
        model.addAttribute("obligation", obligations);
        
        //List of clients with status = C
        List<ClientDTO> clients = clientService.getAllClients();
        model.addAttribute("allClients", clients);
                                
        List<Spatial> countries = spatialService.findAll();
        model.addAttribute("allcountries", countries);
        
        List<Issue> issues = issueDao.findAllIssuesList();
        model.addAttribute("issues", issues);
        
        //List box selected
        //list of clients selected with status = C
        
      
        List<ClientDTO> allObligationClientsEdit = clientService.findOblClients(obligationId, "C");
        model.addAttribute("obligationClients", allObligationClientsEdit);
   
        //list of formal countries by obligationId with voluntary = N
        List<Spatial> allObligationCountriesEdit = obligationsService.findAllCountriesByObligation(obligationId, "N");
        model.addAttribute("obligationCountries",allObligationCountriesEdit);
        
      //list of formal countries by obligationId with voluntary = N
        List<Spatial> allObligationVoluntaryCountriesEdit = obligationsService.findAllCountriesByObligation(obligationId, "Y");
        model.addAttribute("obligationVoluntaryCountries", allObligationVoluntaryCountriesEdit);
        
        //list of issues seleted by obligationID
        List<Issue> allIssuesObligationEdit = obligationsService.findAllIssuesbyObligation(obligationId);
        model.addAttribute("obligationIssues", allIssuesObligationEdit);
        
        model.addAttribute("id","edit");
        
        return "eobligation";
    }
    
    
    /**
     * obligations details by ID (overview)
     */
    @RequestMapping(value = "/add")
    public String obligation_add(final Model model ) {
    	BreadCrumbs.set(model, obligationCrumb, new BreadCrumb("Add obligation"));
    	model.addAttribute("activeTab", "obligations");
    	model.addAttribute("title","Edit Reporting Obligation for");
    	
    	Obligations obligation = new Obligations();
    	  	
        model.addAttribute("obligation", obligation);
       //List of clients with status = C
        List<ClientDTO> clients = clientService.getAllClients();
        model.addAttribute("allClients", clients);
                                
        List<Spatial> countries = spatialService.findAll();
        model.addAttribute("allcountries", countries);
        
        List<Issue> issues = issueDao.findAllIssuesList();
        model.addAttribute("issues", issues);
        
        model.addAttribute("id","add");
                
       return "eobligation";
    }
    /**
     * 
     * @param obligations
     * @param bindingResult
     * @param model
     * @param redirectAttributes
     * @return eobligation (edit)
     */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String addObligation(@Valid Obligations obligations, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
		BreadCrumbs.set(model, obligationCrumb, new BreadCrumb("Edit reporting obligation for"));
    	model.addAttribute("activeTab", "obligations");
    	model.addAttribute("title","Edit Reporting Obligation for");
           
        //List of clients with status = C
        List<ClientDTO> allclients = clientService.getAllClients();
        model.addAttribute("allClients", allclients);
        //list of countries
        List<Spatial> countries = spatialService.findAll();
        model.addAttribute("allcountries", countries);
        //Lists Issues
        List<Issue> issues = issueDao.findAllIssuesList();
        model.addAttribute("issues", issues);
        
        
        //drop down list selected
        //list of the clients selected
        List<ClientDTO> allObligationClients = formalObligationClientsSelected(obligations.getSelectedClients());
        model.addAttribute("obligationClients", allObligationClients);
        
        //list of the formal countries selected
        List<Spatial> allObligationCountries = formalObligationCountriesSelected(obligations.getSelectedFormalCountries());
        model.addAttribute("obligationCountries", allObligationCountries);
             
        //list of the voluntary countries selected        
        List<Spatial> allObligationVoluntaryCountries = voluntaryObligationCountriesSelected(obligations.getSelectedVoluntaryCountries());
        model.addAttribute("obligationVoluntaryCountries", allObligationVoluntaryCountries);
        
      //list of the issues selected
        List<Issue> allSelectedIssues = obligationIssuesSelected(obligations.getSelectedIssues());
        model.addAttribute("obligationIssues", allSelectedIssues);
                
        if (!bindingResult.hasErrors()) {
		    
	        model.addAttribute("id","add");
//			
//			if (obligations.getOblTitle() == "") {
//				bindingResult.addError(new FieldError("oblTitle","oblTitle", "Field is mandatory!"));//TODO i18n
//				redirectAttributes.addFlashAttribute("oblTitle", "Field is mandatory!");
//			}
//
			return "eobligation";
		}else {
			//insertamos y reenviamos a redirect:edit:id
        
        	Integer obligationID = obligationsService.insertObligation(obligations, allObligationClients, allObligationCountries, allObligationVoluntaryCountries, allSelectedIssues);
			obligations.setObligationId(obligationID);
			model.addAttribute("obligationId", obligationID);
			
			model.addAttribute("obligation", obligations);

			model.addAttribute("id","edit");
			return "eobligation";
		}
		
	}
    
    /**
     * 
     * @param obligations
     * @param bindingResult
     * @param model
     * @param redirectAttributes
     * @return edit eoblication template
     */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String editObligation(@Valid Obligations obligations, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
		BreadCrumbs.set(model, obligationCrumb, new BreadCrumb("Edit obligation"));
    	model.addAttribute("activeTab", "obligations");
    	model.addAttribute("title","Edit Reporting Obligation for");
    	
		model.addAttribute("obligationId", obligations.getObligationId());
    	
            
        //List of clients with status = C
        List<ClientDTO> allclients = clientService.getAllClients();
        model.addAttribute("allClients", allclients);
        //list of countries
        List<Spatial> countries = spatialService.findAll();
        model.addAttribute("allcountries", countries);
        //Lists Issues
        List<Issue> issues = issueDao.findAllIssuesList();
        model.addAttribute("issues", issues);
        
        //drop down list selected
        //list of the clients selected
        List<ClientDTO> allObligationClients = formalObligationClientsSelected(obligations.getSelectedClients());
        model.addAttribute("obligationClients", allObligationClients);
        
        //list of the formal countries selected
        List<Spatial> allObligationCountries = formalObligationCountriesSelected(obligations.getSelectedFormalCountries());
        model.addAttribute("obligationCountries", allObligationCountries);
        
        //list of the voluntary countries selected        
        List<Spatial> allObligationVoluntaryCountries = voluntaryObligationCountriesSelected(obligations.getSelectedVoluntaryCountries());
        model.addAttribute("obligationVoluntaryCountries", allObligationVoluntaryCountries);
              
        //list of the issues selected
        List<Issue> allSelectedIssues = obligationIssuesSelected(obligations.getSelectedIssues());
        model.addAttribute("obligationIssues", allSelectedIssues);
        
        obligationsService.updateObligations(obligations, allObligationClients, allObligationCountries, allObligationVoluntaryCountries, allSelectedIssues);
        //change format of Dates to visualize dd/mm/yyyy
        if (obligations.getFirstReporting() != null && obligations.getFirstReporting() != "") {
        	obligations.setFirstReporting(RODUtil.strDate(obligations.getFirstReporting()));
        }else {
        	obligations.setFirstReporting(null);
        }
        if (obligations.getValidTo() != null && obligations.getValidTo() != "") {
        	obligations.setValidTo(RODUtil.strDate(obligations.getValidTo()));
        }else {
        	obligations.setValidTo(null);
        }
        if (obligations.getNextDeadline2() != null && obligations.getNextDeadline2() != "") {
        	obligations.setNextDeadline2(RODUtil.strDate(obligations.getNextDeadline2()));
        }else {
        	obligations.setNextDeadline2(null);
        }
        if (obligations.getNextReporting() != null && obligations.getNextReporting() != "") {
        	obligations.setNextReporting(RODUtil.strDate(obligations.getNextReporting()));
        }else {
        	obligations.setNextReporting(null);
        }
        if (obligations.getValidSince() != null && obligations.getValidSince() != "") {
        	obligations.setValidSince(RODUtil.strDate(obligations.getValidSince()));
        }else {
        	obligations.setValidSince(null);
        }
        
        model.addAttribute("obligation", obligations);
        
        model.addAttribute("id","edit");
        model.addAttribute("message", "data saved correctly");
        
		return "eobligation";
    }
		
	/**
	 * 
	 * @param selectedFormalCountries
	 * @return List<Spatial> allObligationCountries
	 */
	private  List<Spatial> formalObligationCountriesSelected (List<String> selectedFormalCountries){
		Spatial obligationCountry = new Spatial();
		List<Spatial> allObligationCountries = new ArrayList<Spatial>();
	       if (selectedFormalCountries != null) {
		       for (int i = 0 ; i < selectedFormalCountries.size(); i++) {
		    	   obligationCountry = spatialService.findOne(Integer.parseInt(selectedFormalCountries.get(i)));
		    	   allObligationCountries.add(obligationCountry);
		       }
	       }
	    return allObligationCountries;
	}
    /**
     * 
     * @param selectedclients
     * @return List<ClientDTO> allObligationClients
     */
	private  List<ClientDTO> formalObligationClientsSelected (List<String> selectedclients){
		ClientDTO obligationClient = new ClientDTO();
		List<ClientDTO> allObligationClients = new ArrayList<ClientDTO>();
		   if (selectedclients != null) {
		       for (int i = 0 ; i < selectedclients.size(); i++) {
		           obligationClient = clientService.getById(Integer.parseInt(selectedclients.get(i)));
		           allObligationClients.add(obligationClient);

		    }
		}
//	        if (selectedclients != null) {
//	            for (int i = 0 ; i < selectedclients.size(); i++) {
//		        	String [] selectedclientsSplit = selectedclients.get(i).split(":");
//		        	obligationClient.setClientId(Integer.parseInt(selectedclientsSplit[0]));
//		        	obligationClient.setName(selectedclientsSplit[1]);
//		        	allObligationClients.add(obligationClient);
//		           	obligationClient = new ClientDTO();
//		        }
//	        }
		
	    return allObligationClients;
	}
    /**
     * 
     * @param selectedVoluntaryCountries
     * @return List<Spatial> allObligationVoluntaryCountries
     */
	private List<Spatial> voluntaryObligationCountriesSelected (List<String> selectedVoluntaryCountries){
		Spatial obligationCountry = new Spatial();
		List<Spatial> allObligationVoluntaryCountries = new ArrayList<Spatial>();
	       if (selectedVoluntaryCountries != null) {
		       for (int i = 0 ; i < selectedVoluntaryCountries.size(); i++) {
		    	   obligationCountry = spatialService.findOne(Integer.parseInt(selectedVoluntaryCountries.get(i)));
		    	   allObligationVoluntaryCountries.add(obligationCountry);
		       }
	       }
	    return allObligationVoluntaryCountries;
	}
	/**
	 * 
	 * @param selectedIssues
	 * @return List<Issue> allSelectedIssues
	 */
	private List<Issue> obligationIssuesSelected (List<String> selectedIssues){
		Issue selectedIssue = new Issue();
		List<Issue> allSelectedIssues = new ArrayList<Issue>();
	       if (selectedIssues != null) {
		       for (int i = 0 ; i < selectedIssues.size(); i++) {
		    	   selectedIssue = issueDao.findById(Integer.parseInt(selectedIssues.get(i)));
		    	   allSelectedIssues.add(selectedIssue);
		       }
	       }
	    return allSelectedIssues;
	}
		
}
