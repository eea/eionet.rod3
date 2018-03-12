package eionet.rod.controller;

import eionet.rod.model.BreadCrumb;
import eionet.rod.model.ClientDTO;

import eionet.rod.model.Delivery;
import eionet.rod.model.Issue;
import eionet.rod.model.ObligationCountry;
import eionet.rod.IAuthenticationFacade;
import eionet.rod.dao.ClientService;
import eionet.rod.dao.IssueDao;
import eionet.rod.dao.UndoService;
import eionet.rod.model.Obligations;
import eionet.rod.model.SiblingObligation;
import eionet.rod.model.Spatial;
import eionet.rod.model.UndoDTO;
import eionet.rod.service.DeliveryService;
import eionet.rod.service.ObligationService;
import eionet.rod.service.SpatialService;
import eionet.rod.util.BreadCrumbs;
import eionet.rod.util.RODUtil;
import eionet.rod.util.exception.ResourceNotFoundException;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.Authentication;
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
 * Obligation managing controller.
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
     * Service for obligation management.
     */
    @Autowired
    ObligationService obligationsService;
    
    @Autowired
    IssueDao issueDao;
    
    @Autowired
    SpatialService spatialService;
    
    @Autowired
    ClientService clientService;
	
	@Autowired
    DeliveryService deliveryService;
	@Autowired
	UndoService undoService;
	
	@Autowired
    IAuthenticationFacade authenticationFacade;

    /**
     * View for all obligations.
     *
     * @param model - contains attributes for the view
     * @param message
     * @return view name
     */
    @RequestMapping({"", "/", "/view"})
    public String viewObligations(Model model, @RequestParam(required = false) String message, @RequestParam(required = false) String anmode) {
        BreadCrumbs.set(model,"Reporting obligations");
        
        Obligations obligation = new Obligations();
        String issueID = "0";
        if (!RODUtil.isNullOrEmpty(anmode)) {
        	if (anmode.equals("NI")) {
        		issueID = anmode;
        		obligation.setIssueId(issueID);
        		model.addAttribute("activeTab", "obligations");
        		model.addAttribute("titleObl","Reporting obligations");
	        }else if (anmode.equals("P")) {
        		model.addAttribute("activeTab", "CoreData");
        		model.addAttribute("titleObl","Reporting obligations : Eionet core data flows"); 
	        } if (anmode.equals("F")) {
        		model.addAttribute("activeTab", "EEAData");
        		model.addAttribute("titleObl","Reporting obligations : Delivery process is managed by EEA");
	        }else {
	        	model.addAttribute("activeTab", "obligations");
	        	model.addAttribute("titleObl","Reporting obligations");
	        }
        }else {
        	model.addAttribute("activeTab", "obligations");
        	model.addAttribute("titleObl","Reporting obligations");
        }
        
        model.addAttribute("allObligations", obligationsService.findObligationList("0",issueID,"0","N","0",anmode, null, null, false));

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
    	
        
        
        model.addAttribute("obligation",obligation);
        
        if(message != null) model.addAttribute("message", message);
        return "obligations";
    }

	/**
	 * 
	 * @param model
	 * @return
	 */
	 @RequestMapping(value = "/delete", method = RequestMethod.POST)
	 public String deleteObligations(Obligations obligations, Model model) {
		 if (obligations.getDelObligations() != null) {
			 String[] listObligations = obligations.getDelObligations().split(",");
			 Authentication authentication = authenticationFacade.getAuthentication();
			 for (int i = 0; i < listObligations.length; i++) {
				 processEditDelete("D", authentication.getName(), Integer.parseInt(listObligations[i]));
			 }
			 obligationsService.deleteObligations(obligations.getDelObligations());
		 }
		 model.addAttribute("message", "Obligations selected deleted.");
		 return "redirect:view";
	 }
    
	 /**
	 * 
	 * @param model
	 * @return
	 */
	 @RequestMapping(value = "/delete/{obligationId}")
	 public String deleteObligations(@PathVariable("obligationId") Integer obligationId, Model model) {
		 if (obligationId != null) {
			 Authentication authentication = authenticationFacade.getAuthentication();
			 processEditDelete("D", authentication.getName(), obligationId);
			 obligationsService.deleteObligations(obligationId + ",");
			 model.addAttribute("message", "Obligation deleted.");
		 }		 
		 return "redirect:/obligations";
	 }
	 
	 
    /**
     * 
     * @param obligations
     * @param model
     * @return view obligations
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public String searchObligation(Obligations obligations, Model model) {
    	
    	 model.addAttribute("allObligations", obligationsService.findObligationList(obligations.getClientId(),obligations.getIssueId(),obligations.getSpatialId(),obligations.getTerminate(),"0",null, null, null, false));

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
        
        Obligations obligationRelation = obligationsService.findObligationRelation(obligationId);
        model.addAttribute("obligationRelation", obligationRelation);
      
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
     * obligation deliveries
     */
    @RequestMapping(value = "/{obligationId}/deliveries")
    public String obligation_deliveries(@PathVariable("obligationId") Integer obligationId, final Model model) throws Exception {
    	model.addAttribute("obligationId", obligationId);
    	
    	Obligations obligation = obligationsService.findOblId(obligationId);
        BreadCrumbs.set(model, obligationCrumb, new BreadCrumb(obligation.getOblTitle()));
        model.addAttribute("obligation", obligation);
        model.addAttribute("title",RODUtil.replaceTags(obligation.getOblTitle())); 
        
        List<Delivery> deliveries =  deliveryService.getAllDelivery(obligationId.toString(), null);
        model.addAttribute("deliveries", deliveries);
        
    	model.addAttribute("activeTab", "obligations");
        
    	return "obligation_deliveries";
    }
 
    /**
     * obligation history	
     */
    @RequestMapping(value = "/{obligationId}/history")
    public String obligation_history(@PathVariable("obligationId") Integer obligationId, final Model model) {
    	model.addAttribute("obligationId", obligationId);
    	
    	Obligations obligation = obligationsService.findOblId(obligationId);
    	BreadCrumbs.set(model, obligationCrumb, new BreadCrumb(obligation.getOblTitle()));
    	model.addAttribute("obligation", obligation);
    	model.addAttribute("title",RODUtil.replaceTags(obligation.getOblTitle())); 
    	
    	List<UndoDTO> versions = undoService.getPreviousActionsReportSpecific(obligationId, "T_OBLIGATION", "PK_RA_ID", "U");
    	if (versions != null) {
    		for (int i = 0; i < versions.size(); i++) {
    			String date = RODUtil.miliseconds2Date(versions.get(i).getUndoTime());
    			versions.get(i).setDate(date);
    		}
    	}
    	model.addAttribute("versions", versions);
    	
    	model.addAttribute("activeTab", "obligations");
    	
    	return "obligation_history";
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
        
        if (!RODUtil.isNullOrEmpty(obligations.getFirstReporting())) {
        	obligations.setFirstReporting(RODUtil.strDate(obligations.getFirstReporting()));
        }else {
        	obligations.setFirstReporting(null);
        }
        if (!RODUtil.isNullOrEmpty(obligations.getValidTo())) {
        	obligations.setValidTo(RODUtil.strDate(obligations.getValidTo()));
        }else {
        	obligations.setValidTo(null);
        }
        if (!RODUtil.isNullOrEmpty(obligations.getNextDeadline2())) {
        	obligations.setNextDeadline2(RODUtil.strDate(obligations.getNextDeadline2()));
        }else {
        	obligations.setNextDeadline2(null);
        }
        if (!RODUtil.isNullOrEmpty(obligations.getNextDeadline())) {
        	obligations.setNextDeadline(RODUtil.strDate(obligations.getNextDeadline()));
        }else {
        	obligations.setNextDeadline(null);
        }
        
        if (!RODUtil.isNullOrEmpty(obligations.getValidSince())) {
        	obligations.setValidSince(RODUtil.strDate(obligations.getValidSince()));
        }else {
        	obligations.setValidSince(null);
        }
       
        //init var ListBox
        List<String> selectedClients = new ArrayList<String>();
        obligations.setSelectedClients(selectedClients);
        List<String> selectedVoluntaryCountries= new ArrayList<String>();
        obligations.setSelectedVoluntaryCountries(selectedVoluntaryCountries);
        List<String> selectedFormalCountries= new ArrayList<String>();
        obligations.setSelectedFormalCountries(selectedFormalCountries);
        List<String> selectedIssues= new ArrayList<String>();
        obligations.setSelectedIssues(selectedIssues);
        
        Obligations obligationRelation = obligationsService.findObligationRelation(obligationId);
        
        obligations.setRelObligationId(obligationRelation.getRelObligationId());
        obligations.setOblRelationId(obligationRelation.getOblRelationId());
        
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
        
        //all obligations except the edited.
        List<Obligations> relObligations = obligationsService.findAll();
        model.addAttribute("relObligations", relObligations);
       
        model.addAttribute("title","Edit Reporting Obligation for");
        
        model.addAttribute("id","edit");
        
        return "eobligation";
    }
    
    
    /**
     * obligations details by ID (overview)
     */
    @RequestMapping(value = "/add/{sourceId}")
    public String obligation_add(final Model model, @PathVariable("sourceId") String sourceId) {
    	BreadCrumbs.set(model, obligationCrumb, new BreadCrumb("Add obligation"));
    	model.addAttribute("activeTab", "obligations");
    	model.addAttribute("title","Edit Reporting Obligation for");
    	
    	Obligations obligation = new Obligations();
    	obligation.setSourceId(sourceId);
        model.addAttribute("obligation", obligation);
       //List of clients with status = C
        List<ClientDTO> clients = clientService.getAllClients();
        model.addAttribute("allClients", clients);
                                
        List<Spatial> countries = spatialService.findAll();
        model.addAttribute("allcountries", countries);
        
        List<Issue> issues = issueDao.findAllIssuesList();
        model.addAttribute("issues", issues);
        
        List<Obligations> relObligations = obligationsService.findAll();
        model.addAttribute("relObligations", relObligations);
        
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
        
        List<Obligations> relObligations = obligationsService.findAll();
        model.addAttribute("relObligations", relObligations);
 
    	model.addAttribute("sourceId", obligations.getSourceId());
    	
		model.addAttribute("activeTab", "obligations");
    	
    	model.addAttribute("title","Edit Reporting Obligation for");

        
        if (bindingResult.hasErrors()) {
        	
    		model.addAttribute("obligation", obligations);
    		model.addAttribute("obligation", obligations);
	        model.addAttribute("id","add");

		}else {
			//insertamos y reenviamos a redirect:edit:id
        
        	Integer obligationID = obligationsService.insertObligation(obligations, allObligationClients, allObligationCountries, allObligationVoluntaryCountries, allSelectedIssues);
			obligations.setObligationId(obligationID);
			model.addAttribute("obligationId", obligationID);
		
			model.addAttribute("obligation", obligations);

			model.addAttribute("id","edit");
			
			model.addAttribute("message", "Obligation " + obligations.getOblTitle() + " added.");
			
		}
             
        return "eobligation";
		
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
        
        List<Obligations> relObligations = obligationsService.findAll();
        model.addAttribute("relObligations", relObligations);
        
		Authentication authentication = authenticationFacade.getAuthentication();
        processEditDelete("U", authentication.getName(), obligations.getObligationId());
        
		obligationsService.updateObligations(obligations, allObligationClients, allObligationCountries, allObligationVoluntaryCountries, allSelectedIssues);
        //change format of Dates to visualize dd/mm/yyyy
        if (!RODUtil.isNullOrEmpty(obligations.getFirstReporting())) {
        	obligations.setFirstReporting(RODUtil.strDate(obligations.getFirstReporting()));
        }else {
        	obligations.setFirstReporting(null);
        }
        if (!RODUtil.isNullOrEmpty(obligations.getValidTo())) {
        	obligations.setValidTo(RODUtil.strDate(obligations.getValidTo()));
        }else {
        	obligations.setValidTo(null);
        }
        if (!RODUtil.isNullOrEmpty(obligations.getNextDeadline2())) {
        	obligations.setNextDeadline2(RODUtil.strDate(obligations.getNextDeadline2()));
        }else {
        	obligations.setNextDeadline2(null);
        }
        if (!RODUtil.isNullOrEmpty(obligations.getNextDeadline())) {
        	obligations.setNextDeadline(RODUtil.strDate(obligations.getNextDeadline()));
        }else {
        	obligations.setNextDeadline(null);
        }
        if (!RODUtil.isNullOrEmpty(obligations.getValidSince())) {
        	obligations.setValidSince(RODUtil.strDate(obligations.getValidSince()));
        }else {
        	obligations.setValidSince(null);
        }
        
        model.addAttribute("obligation", obligations);
        
        model.addAttribute("id","edit");
        model.addAttribute("message", "Obligation " + obligations.getOblTitle() + " updated.");
        
		return "eobligation";
    }
	
		
	/**
	 * 
	 * @param selectedFormalCountries
	 * @return List<Spatial> allObligationCountries
	 */
	private  List<Spatial> formalObligationCountriesSelected (List<String> selectedFormalCountries){
		Spatial obligationCountry;
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
		ClientDTO obligationClient;
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
		Spatial obligationCountry;
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
		Issue selectedIssue;
		List<Issue> allSelectedIssues = new ArrayList<Issue>();
	       if (selectedIssues != null) {
		       for (int i = 0 ; i < selectedIssues.size(); i++) {
		    	   selectedIssue = issueDao.findById(Integer.parseInt(selectedIssues.get(i)));
		    	   allSelectedIssues.add(selectedIssue);
		       }
	       }
	    return allSelectedIssues;
	}
	private void processEditDelete(String state, String userName, Integer obligationId) {
		
		long ts = System.currentTimeMillis();
		
		if (state != null && state.equals("U")) {
			undoService.insertIntoUndo(obligationId, "U", "T_OBLIGATION", "PK_RA_ID", ts, "", "y");
		}
		
		String url = "obligations/" + obligationId;
		undoService.insertIntoUndo(ts, "T_OBLIGATION", "REDIRECT_URL", "L", "y", "n", url, 0, "n");
		undoService.insertIntoUndo(ts, "T_OBLIGATION", "A_USER", "K", "y", "n", userName, 0, "n");
		undoService.insertIntoUndo(ts, "T_OBLIGATION", "TYPE", "T", "y", "n", "A", 0, "n");
		
		if (state != null && state.equals("D")) {
			String aclPath = "/obligations/" + obligationId;
			undoService.insertIntoUndo(ts, "T_OBLIGATION", "ACL", "ACL", "y", "n", aclPath, 0, "n");
		}
		
		delActivity(state, "y", obligationId, ts);
		
	}
	
	private void delActivity(String op, String show, Integer obligationId, long ts) {
		
		undoService.insertTransactionInfo(obligationId, "A", "T_RAISSUE_LNK", "FK_RA_ID", ts, "");
		undoService.insertTransactionInfo(obligationId, "A", "T_RASPATIAL_LNK", "FK_RA_ID", ts, "");
		//undoService.insertTransactionInfo(sourceId, "A", "T_INFO_LNK", "FK_RA_ID", ts, "");
		undoService.insertTransactionInfo(obligationId, "A", "T_CLIENT_OBLIGATION_LNK", "FK_RA_ID", ts, "");
		undoService.insertTransactionInfo(obligationId, "A", "T_OBLIGATION", "PK_RA_ID", ts, "");
		undoService.insertTransactionInfo(obligationId, "A", "T_HISTORIC_DEADLINES", "FK_RA_ID", ts, "");
		undoService.insertTransactionInfo(obligationId, "A", "T_OBLIGATION_RELATION", "FK_RA_ID", ts, "");
		
		undoService.insertIntoUndo(obligationId, op, "T_RAISSUE_LNK", "FK_RA_ID", ts, "", show);
		undoService.insertIntoUndo(obligationId, op, "T_RASPATIAL_LNK", "FK_RA_ID", ts, "", show);
		//undoService.insertIntoUndo(sourceId, op, "T_INFO_LNK", "FK_RA_ID", ts, "", show);
		undoService.insertIntoUndo(obligationId, op, "T_HISTORIC_DEADLINES", "FK_RA_ID", ts, "", show);
		undoService.insertIntoUndo(obligationId, op, "T_OBLIGATION_RELATION", "FK_RA_ID", ts, "", show);
		
		if (op != null && op.equals("D")) {
			//FALTAN LOS ACLS
			undoService.insertIntoUndo(obligationId, "D", "T_OBLIGATION", "PK_RA_ID", ts, "", show);
		}
		
		undoService.insertIntoUndo(obligationId, op, "T_CLIENT_OBLIGATION_LNK", "FK_RA_ID", ts, "", show);
		
	}
		
}
