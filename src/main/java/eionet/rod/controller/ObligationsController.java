package eionet.rod.controller;

import eionet.rod.model.BreadCrumb;
import eionet.rod.model.ClientDTO;

import eionet.rod.model.Delivery;
import eionet.rod.model.Issue;
import eionet.rod.model.ObligationCountry;
import eionet.rod.Attrs;
import eionet.rod.IAuthenticationFacade;
import eionet.rod.UNSEventSender;
import eionet.rod.dao.ClientService;
import eionet.rod.dao.IssueDao;
import eionet.rod.dao.UndoService;
import eionet.rod.model.Obligations;
import eionet.rod.model.SiblingObligation;
import eionet.rod.model.Spatial;
import eionet.rod.model.UndoDTO;
import eionet.rod.service.DeliveryService;
import eionet.rod.service.FileServiceIF;
import eionet.rod.service.ObligationService;
import eionet.rod.service.SpatialService;
import eionet.rod.util.BreadCrumbs;
import eionet.rod.util.RODServices;
import eionet.rod.util.RODUtil;
import eionet.rod.util.exception.ResourceNotFoundException;
import eionet.rod.util.exception.ServiceException;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

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
        boolean deliveries = false;
        String deliveryColumn = "0";
        if (!RODUtil.isNullOrEmpty(anmode)) {
        	model.addAttribute("anmode", anmode);
        	if (anmode.equals("NI")) {
        		issueID = anmode;
        		obligation.setIssueId(issueID);
        		model.addAttribute("activeTab", "obligations");
        		model.addAttribute("titleObl","Reporting obligations");
	        }else if (anmode.equals("P")) {
        		model.addAttribute("activeTab", "CoreData");
        		model.addAttribute("titleObl","Reporting obligations : Eionet core data flows"); 
        		deliveries = true;
        		deliveryColumn = "1";
	        }else if (anmode.equals("F")) {
        		model.addAttribute("activeTab", "EEAData");
        		model.addAttribute("titleObl","Reporting obligations : Delivery process is managed by EEA");
	        }else if (anmode.equals("C")) {
        		model.addAttribute("activeTab", "obligations");
        		model.addAttribute("titleObl","Reporting obligations : EEA Core set of indicators");
	        }else {
	        	model.addAttribute("activeTab", "obligations");
	        	model.addAttribute("titleObl","Reporting obligations");
	        }
        }else {
        	model.addAttribute("activeTab", "obligations");
        	model.addAttribute("titleObl","Reporting obligations");
        }
        
        model.addAttribute("allObligations", obligationsService.findObligationList("0",issueID,"0","N","0",anmode, null, null, deliveries));

        model.addAttribute("title","Reporting obligations");
        
        model.addAttribute("deliveryColumn",deliveryColumn);
        
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
			 long ts = System.currentTimeMillis();
			 for (int i = 0; i < listObligations.length; i++) {
				 processEditDelete("D", authentication.getName(), Integer.parseInt(listObligations[i]), ts);
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
			 long ts = System.currentTimeMillis();
			 processEditDelete("D", authentication.getName(), obligationId, ts);
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
    	BreadCrumbs.set(model,"Reporting obligations");
    	String terminate = obligations.getTerminate();
    	if(RODUtil.isNullOrEmpty(obligations.getTerminate()) ) {
    		terminate = "N";
    	}
    	boolean deliveries = false;
    	String deliveryColumn = "0";
    	if (!RODUtil.isNullOrEmpty(obligations.getAnmode())) {
        	model.addAttribute("anmode", obligations.getAnmode());
        	if (obligations.getAnmode().equals("NI")) {
        		obligations.setIssueId(obligations.getAnmode());
        		model.addAttribute("activeTab", "obligations");
        		model.addAttribute("titleObl","Reporting obligations");
	        }else if (obligations.getAnmode().equals("P")) {
        		model.addAttribute("activeTab", "CoreData");
        		model.addAttribute("titleObl","Reporting obligations : Eionet core data flows"); 
        		deliveryColumn = "1";
        		deliveries = true;
	        }else if (obligations.getAnmode().equals("F")) {
        		model.addAttribute("activeTab", "EEAData");
        		model.addAttribute("titleObl","Reporting obligations : Delivery process is managed by EEA");
	        }else if (obligations.getAnmode().equals("C")) {
        		model.addAttribute("activeTab", "obligations");
        		model.addAttribute("titleObl","Reporting obligations : EEA Core set of indicators");
	        }else {
	        	model.addAttribute("activeTab", "obligations");
	        	model.addAttribute("titleObl","Reporting obligations");
	        }
        }else {
        	obligations.setAnmode(null);
        	model.addAttribute("activeTab", "obligations");
        	model.addAttribute("titleObl","Reporting obligations");
        }
    	
    	 model.addAttribute("allObligations", obligationsService.findObligationList(obligations.getClientId(),obligations.getIssueId(),obligations.getSpatialId(),terminate,"0",obligations.getAnmode(), null, null, deliveries));

         model.addAttribute("title","Reporting obligations");
         
         model.addAttribute("deliveryColumn",deliveryColumn);
         
       //Environmental issues
     	List<Issue> issues = issueDao.findAllIssuesList();
     	model.addAttribute("allIssues", issues);
     	
     	//Countries/territories
     	List<Spatial> countries = spatialService.findAll();
     	model.addAttribute("allCountries", countries);
         
     	//Countries/territories
     	List<ClientDTO> clients = clientService.getAllClients();
     	model.addAttribute("allClients", clients);
     	
        //model.addAttribute("activeTab", "obligations");
         
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
	        model.addAttribute("id","add");

		}else {
			//insertamos y reenviamos a redirect:edit:id
        
        	Integer obligationID = obligationsService.insertObligation(obligations, allObligationClients, allObligationCountries, allObligationVoluntaryCountries, allSelectedIssues);
			obligations.setObligationId(obligationID);
			model.addAttribute("obligationId", obligationID);
		
			model.addAttribute("obligation", obligations);

			model.addAttribute("id","edit");
			
			model.addAttribute("message", "Obligation " + obligations.getOblTitle() + " added.");
			
			long ts = System.currentTimeMillis();
			
			try {
	        	sendEvent(false, obligations, obligationID, ts);
	        } catch(ServiceException e)
	        {
	        	
	        } 
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
		long ts = System.currentTimeMillis();
        processEditDelete("U", authentication.getName(), obligations.getObligationId(), ts);
        
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
        
		try {
        	sendEvent(true, obligations, obligations.getObligationId(), ts);
        } catch(ServiceException e)
        {
        	e.printStackTrace();
        }
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
	private void processEditDelete(String state, String userName, Integer obligationId, long ts) {		
		
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
	
	private String getUserName()
	{
		Authentication authentication = authenticationFacade.getAuthentication();
		String userName = authentication.getName();
		
		return userName;
	}	
	
	private Vector<String> getChanges(Integer obligationID, long ts) throws ServiceException {
		Vector<String> res_vec = new Vector<String>();
		List<UndoDTO> undoList = undoService.getUndoInformation(ts, "U", "T_OBLIGATION");
		Obligations obligation = obligationsService.findOblId(obligationID);
		String value = "";
		UndoDTO undo = new UndoDTO();
		
		for (int i = 0; i < undoList.size(); i++) {
			undo = undoList.get(i);
			boolean aux = true; 
			
			switch (undo.getCol()) {
			case "PK_RA_ID":
				value = obligationID.toString();
				break;
			case "TITLE":
				value = obligation.getOblTitle();
				break;
			case "DESCRIPTION":
				value = obligation.getDescription();
				break;
			case "FIRST_REPORTING":
				value = obligation.getFirstReporting();
				break;
			case "VALID_TO":
				value = obligation.getValidTo();
				break;
			case "REPORT_FREQ_MONTHS":
				value = obligation.getReportFreqMonths();
				break;
			case "NEXT_DEADLINE":
				value = obligation.getNextDeadline();
				break;
			case "NEXT_REPORTING":
				value = obligation.getNextReporting();
				break;
			case "DATE_COMMENTS":
				value = obligation.getDateComments();
				break;
			case "FORMAT_NAME":
				value = obligation.getFormatName();
				break;
			case "REPORT_FORMAT_URL":
				value = obligation.getReportFormatUrl();
				break;
			case "VALID_SINCE":
				value = obligation.getValidSince();
				break;
			case "REPORTING_FORMAT":
				value = obligation.getReportingFormat();
				break;
			case "LOCATION_INFO":
				value = obligation.getLocationInfo();
				break;
			case "LOCATION_PTR":
				value = obligation.getLocationPtr();
				break;
			case "DATA_USED_FOR":
				value = obligation.getDataUsedFor();
				break;
			case "DATA_USED_FOR_URL":
				value = obligation.getDataUsedForUrl();
				break;
			case "COORDINATOR_ROLE":
				value = obligation.getCoordinatorRole();
				break;
			case "COORDINATOR_ROLE_SUF":
				value = obligation.getCoordinatorRoleSuf();
				break;
			case "COORDINATOR":
				value = obligation.getCoordinator();
				break;
			case "COORDINATOR_URL":
				value = obligation.getCoordinatorUrl();
				break;
			case "RESPONSIBLE_ROLE":
				value = obligation.getResponsibleRole();
				break;
			case "RESPONSIBLE_ROLE_SUF":
				value = obligation.getResponsibleRoleSuf();
				break;
			case "NATIONAL_CONTACT":
				value = obligation.getNationalContact();
				break;
			case "NATIONAL_CONTACT_URL":
				value = obligation.getNationalContactUrl();
				break;
			case "EEA_PRIMARY":
				if (obligation.getEeaPrimary() != null) {
					value = obligation.getEeaPrimary().toString();
				} else {
					value = null;
				}		
				break;
			case "EEA_CORE":
				if (obligation.getEeaCore() != null) {
					value = obligation.getEeaCore().toString();
				} else {
					value = null;
				}		
				break;
			case "FLAGGED":
				if (obligation.getFlagged() != null) {
					value = obligation.getFlagged().toString();
				} else {
					value = null;
				}		
				break;
			case "COMMENT":
				value = obligation.getComment();
				break;
			case "AUTHORITY":
				value = obligation.getAuthority();
				break;
			case "FK_SOURCE_ID":
				value = obligation.getSourceId();
				break;
			case "REPORT_FREQ_DETAIL":
				value = obligation.getReportFreqDetail();
				break;
			case "LAST_UPDATE":
				value = obligation.getLastUpdate();
				break;
			case "TERMINATE":
				value = obligation.getTerminate();
				break;
			case "REPORT_FREQ":
				value = obligation.getTerminate();
				break;
			case "FK_DELIVERY_COUNTRY_IDS":
				value = obligation.getDeliveryCountryId();
				break;
			case "NEXT_DEADLINE2":
				value = obligation.getNextDeadline2();
				break;
			case "LAST_HARVESTED":
				value = obligation.getLastHarvested();
				break;
			case "CONTINOUS_REPORTING":
				value = obligation.getContinousReporting();
				break;
			default:
				aux = false;
				break;
			}
				
			if (aux) {
				String undoValue = undo.getValue();	
				
				if (value != null) {
					if (value.trim().equals("")) {
						value = null;
					}
				}
				
				if (undoValue != null) {
					if (undoValue.trim().equals("")) {
						undoValue = null;
					}
				}
				boolean diff = (value != null && undoValue != null && value.equals(undoValue)) || (value == null && undoValue == null);
				if (!diff) {
					String label = getLabel(undo.getCol(), undoValue, value);
					res_vec.add(label);
				}
			}
		}		
		
		undoList = undoService.getUndoInformation(ts, "U", "T_RASPATIAL_LNK");
		List<Spatial> formallyCountries = obligationsService.findAllCountriesByObligation(obligationID, "N");
		List<Spatial> voluntaryCountries = obligationsService.findAllCountriesByObligation(obligationID, "Y");
		Spatial country = new Spatial();
		StringBuffer addedVoluntaryCountries = new StringBuffer();
		StringBuffer removedVoluntaryCountries = new StringBuffer();
		StringBuffer addedFormallyCountries = new StringBuffer();
		StringBuffer removedFormallyCountries = new StringBuffer();
		List<String>  undoVoluntaryCountries = new ArrayList<String>();
		List<String>  undoFormallyCountries = new ArrayList<String>();
		List<String> currentVoluntaryCountries = new ArrayList<String>();
		List<String> currentFormallyCountries = new ArrayList<String>();
		List<String>  voluntary = new ArrayList<String>();
		
		if (undoList != null) {			
			for (int i = 0; i < undoList.size(); i++) {
				if (undoList.get(i).getCol().equals("VOLUNTARY")) {
					voluntary.add(undoList.get(i).getValue());					
				}
			}
		}		
		
		int countVoluntary = 0;
		if (undoList != null) {			
			for (int i = 0; i < undoList.size(); i++) {
				if (undoList.get(i).getCol().equals("FK_SPATIAL_ID")) {						
					country = spatialService.findOne(Integer.parseInt(undoList.get(i).getValue()));
					if (voluntary.get(countVoluntary).equals("N")) {
						undoFormallyCountries.add(country.getName());
					} else {
						undoVoluntaryCountries.add(country.getName());
					}
					countVoluntary++;
				}
				
			}
		}
		
		if (formallyCountries != null) {
			for (int i = 0; i < formallyCountries.size(); i++) {
				currentFormallyCountries.add(formallyCountries.get(i).getName());
			}
		}
		
		if (voluntaryCountries != null) {
			for (int i = 0; i < voluntaryCountries.size(); i++) {
				currentVoluntaryCountries.add(voluntaryCountries.get(i).getName());
			}
		}
		
		if (currentFormallyCountries.size() > 0) {
			for (int i = 0; i < currentFormallyCountries.size(); i++) {
				if (!undoFormallyCountries.contains(currentFormallyCountries.get(i))) {
					addedFormallyCountries.append(currentFormallyCountries.get(i));
					addedFormallyCountries.append(", ");
				}
			}
			if (addedFormallyCountries.length() > 0) {
				addedFormallyCountries = addedFormallyCountries.replace(addedFormallyCountries.length()-2, addedFormallyCountries.length(), "");
			}
			
		}
		
		if (undoFormallyCountries.size() > 0) {
			for (int i = 0; i < undoFormallyCountries.size(); i++) {
				if (!currentFormallyCountries.contains(undoFormallyCountries.get(i))) {
					removedFormallyCountries.append(undoFormallyCountries.get(i));
					removedFormallyCountries.append(", ");
				}
			}
			if (removedFormallyCountries.length() > 0) {
				removedFormallyCountries = removedFormallyCountries.replace(removedFormallyCountries.length()-2, removedFormallyCountries.length(), "");
			}
			
		}
		
		if (currentVoluntaryCountries.size() > 0) {
			for (int i = 0; i < currentVoluntaryCountries.size(); i++) {
				if (!undoVoluntaryCountries.contains(currentVoluntaryCountries.get(i))) {
					addedVoluntaryCountries.append(currentVoluntaryCountries.get(i));
					addedVoluntaryCountries.append(", ");
				}
			}
			if (addedVoluntaryCountries.length() > 0) {
				addedVoluntaryCountries = addedVoluntaryCountries.replace(addedVoluntaryCountries.length()-2, addedVoluntaryCountries.length(), "");
			}
			
		}
		
		if (undoVoluntaryCountries.size() > 0) {
			for (int i = 0; i < undoVoluntaryCountries.size(); i++) {
				if (!currentVoluntaryCountries.contains(undoVoluntaryCountries.get(i))) {
					removedVoluntaryCountries.append(undoVoluntaryCountries.get(i));
					removedVoluntaryCountries.append(", ");
				}
			}
			if (removedVoluntaryCountries.length() > 0) {
				removedVoluntaryCountries = removedVoluntaryCountries.replace(removedVoluntaryCountries.length()-2, removedVoluntaryCountries.length(), "");
			}
			
		}
		
		if (addedFormallyCountries.length() > 0) {
    		res_vec.add("'Countries reporting formally' added: " + addedFormallyCountries);
    	}
    	if (removedFormallyCountries.length() > 0)	{
    		res_vec.add("'Countries reporting formally' removed: " + removedFormallyCountries);
    	}
    	if (addedVoluntaryCountries.length() > 0) {
    		res_vec.add("'Countries reporting voluntarily' added: " + addedVoluntaryCountries);
    	}
    	if (removedVoluntaryCountries.length() > 0)	{
    		res_vec.add("'Countries reporting voluntarily' removed: " + removedVoluntaryCountries);
    	}
    	
		
		undoList = undoService.getUndoInformation(ts, "U", "T_RAISSUE_LNK");
		List<Issue> obligationIssues = obligationsService.findAllIssuesbyObligation(obligationID);
		Issue issue;		
		StringBuffer addedIssues = new StringBuffer();
		StringBuffer removedIssues = new StringBuffer();
		List<String>  undoIssues = new ArrayList<String>();
		List<String> currentIssues = new ArrayList<String>();
		
		if (obligationIssues != null) {			
			for (int i = 0; i < obligationIssues.size(); i++) {
				currentIssues.add(obligationIssues.get(i).getIssueName());
			}
		}
		
		if (undoList != null) {			
			for (int i = 0; i < undoList.size(); i++) {
				undo = undoList.get(i);
				if (undo.getCol().equals("FK_ISSUE_ID")) {
					issue = issueDao.findById(Integer.parseInt(undo.getValue()));
					undoIssues.add(issue.getIssueName());
				}
			}
		}
		
		if (currentIssues.size() > 0) {
			for (int i = 0; i < currentIssues.size(); i++) {
				if (!undoIssues.contains(currentIssues.get(i))) {
					addedIssues.append(currentIssues.get(i));
					addedIssues.append(", ");
				}
			}
			if (addedIssues.length() > 0) {
				addedIssues = addedIssues.replace(addedIssues.length()-2, addedIssues.length(), "");
			}
			
		}
		
		if (undoIssues.size() > 0) {
			for (int i = 0; i < undoIssues.size(); i++) {
				if (!currentIssues.contains(undoIssues.get(i))) {
					removedIssues.append(undoIssues.get(i));
					removedIssues.append(", ");
				}
			}
			if (removedIssues.length() > 0)	{
				removedIssues = removedIssues.replace(removedIssues.length()-2, removedIssues.length(), "");
			}
			
		}
		
		if (addedIssues.length() > 0) {
    		res_vec.add("'Environmental issues' added: " + addedIssues);
    	}
    	if (removedIssues.length() > 0)	{
    		res_vec.add("'Environmental issues' removed: " + removedIssues);
    	}
    	
    	undoList = undoService.getUndoInformation(ts, "U", "T_CLIENT_OBLIGATION_LNK");
		List<ClientDTO> obligationClients = obligationsService.findAllClientsByObligation(obligationID);
		ClientDTO client;
		StringBuffer addedClients = new StringBuffer();
		StringBuffer removedClients = new StringBuffer();
		List<String>  undoClients = new ArrayList<String>();
		List<String> currentClients = new ArrayList<String>();
		
		if (obligationClients != null) {			
			for (int i = 0; i < obligationClients.size(); i++) {
				currentClients.add(obligationClients.get(i).getName());
			}
		}
		
		if (undoList != null) {			
			for (int i = 0; i < undoList.size(); i++) {
				undo = undoList.get(i);
				if (undo.getCol().equals("FK_CLIENT_ID")) {
					client = clientService.getById(Integer.parseInt(undo.getValue()));
					undoClients.add(client.getName());
				}
			}
		}
		
		if (currentClients.size() > 0) {
			for (int i = 0; i < currentClients.size(); i++) {
				if (!undoClients.contains(currentClients.get(i))) {
					addedClients.append(currentClients.get(i));
					addedClients.append(", ");
				}
			}
			if (addedClients.length() > 0)	{
				addedClients = addedClients.replace(addedClients.length()-2, addedClients.length(), "");
			}
			
		}
		
		if (undoClients.size() > 0) {
			for (int i = 0; i < undoClients.size(); i++) {
				if (!currentClients.contains(undoClients.get(i))) {
					removedClients.append(undoClients.get(i));
					removedClients.append(", ");
				}
			}
			if (removedClients.length() > 0) {
				removedClients = removedClients.replace(removedClients.length()-2, removedClients.length(), "");
			}
			
		}
		
    	if (addedClients.length() > 0)	{
    		res_vec.add("'Other clients using this reporting' added: " + addedClients);
    	}
    	if (removedClients.length() > 0) {
    		res_vec.add("'Other clients using this reporting' removed: " + removedClients);
    	}
    	
    	undoList = undoService.getUndoInformation(ts, "U", "T_OBLIGATION_RELATION");
    	Obligations currentRelation = obligationsService.findObligationRelation(obligationID); 
    	String undoRelation = null;
    	String addedRelation = "";
    	String removedRelation = "";
    	
    	if (undoList != null) {
    		for (int i = 0; i < undoList.size(); i++) {
    			undo = undoList.get(i);
    			if (undo.getCol().equals("FK_RA_ID2")) {
    				undoRelation = obligationsService.findOblId(Integer.parseInt(undo.getValue())).getOblTitle();
    			}
    		}
    	}
    	
    	if (currentRelation.getRelObligationId() != 0 || undoRelation != null) {
    		if (currentRelation.getRelObligationId() == 0 && undoRelation != null) {
    			removedRelation = undoRelation;
    		} else if (currentRelation.getRelObligationId() != 0 && undoRelation == null) {
    			addedRelation = currentRelation.getOblRelationTitle();
    		} else if (!undoRelation.equals(currentRelation.getOblRelationTitle())) {
    			removedRelation = undoRelation;
    			addedRelation = currentRelation.getOblRelationTitle();
    		}
    	}
    	
    	if (addedRelation.length() > 0 || removedRelation.length() > 0) {
    		res_vec.add("'Relation with other obligations' changed from '" + removedRelation + "' to '" + addedRelation + "'");
    	}
    	
    	
		/*int intId = new Integer(obligationID).intValue();
        DifferenceDTO countries_formally = RODServices.getDbService().getDifferencesDao().getDifferencesInCountries(ts, intId, "N", "U");
        if (countries_formally != null) {
            String added = countries_formally.getAdded();
            String removed = countries_formally.getRemoved();
            if (added.length() > 0) {
                res_vec.add("'Countries reporting formally' added: " + added);
            }
            if (removed.length() > 0) {
                res_vec.add("'Countries reporting formally' removed: " + removed);
            }
        }
        
        DifferenceDTO countries_voluntarily = RODServices.getDbService().getDifferencesDao().getDifferencesInCountries(ts, intId, "Y", "U");
        if (countries_voluntarily != null) 
        {
        	String added = countries_voluntarily.getAdded();
        	String removed = countries_voluntarily.getRemoved();
        	if (added.length() > 0)
        	{
        		res_vec.add("'Countries reporting voluntarily' added: " + added);
        	}
        	if (removed.length() > 0)
        	{
        		res_vec.add("'Countries reporting voluntarily' removed: " + removed);
        	}
        }
        
        DifferenceDTO issues = RODServices.getDbService().getDifferencesDao().getDifferencesInIssues(ts, intId, "U");
        if (issues != null)
        {
        	String added = issues.getAdded();
        	String removed = issues.getRemoved();
        	if (added.length() > 0)
        	{
        		res_vec.add("'Environmental issues' added: " + added);
        	}
        	if (removed.length() > 0)
        	{
        		res_vec.add("'Environmental issues' removed: " + added);
        	}
        }
        
        DifferenceDTO clients = RODServices.getDbService().getDifferencesDao().getDifferencesInClients(ts, intId, "C", "U", "A");
        if (clients != null)
        {
        	String added = clients.getAdded();
        	String removed = clients.getRemoved();
        	if (added.length() > 0)
        	{
        		res_vec.add("'Other clients using this reporting' added: " + added);
        	}
        	if (removed.length() > 0)
        	{
        		res_vec.add("'Other clients using this reporting' removed: " + added);
        	}
        }
        
        DifferenceDTO info = RODServices.getDbService().getDifferencesDao().getDifferencesInInfo(ts, intId, "U", "I");
        if (info != null)
        {
        	String added = info.getAdded();
        	String removed = info.getRemoved();
        	if (added.length() > 0)
        	{
        		res_vec.add("'Type of info reported' added: " + added);
        	}
        	if (removed.length() > 0)
        	{
        		res_vec.add("'Type of info reported' removed: " + removed);
        	}
        }*/
    			
		return res_vec;
	}
	
	private String getSuffixValue(String value)throws ServiceException
	{
		String ret = null;
		int b = Integer.parseInt(value); //new Integer(value).intValue();
		if (b==0)
		{
			ret = "checked";
		}else if (b==1)
		{
			ret = "unchecked";					
		}
		
		return ret;
	}
	
	private String getChkValue(String value) throws ServiceException
	{
		
		String ret = null;
		int b = Integer.parseInt(value); //new Integer(value).intValue();
		if (b == 0)
		{
			ret = "unchecked";
		}else if (b==1)
		{
			ret = "checked";
		}
		return ret;
	}
	
	private String getDpsirValue(String value) throws ServiceException
	{
		String ret = null;
		if(value.equalsIgnoreCase("null") || value.equalsIgnoreCase("no"))
		{
			ret = "unchecked";
		}else if (value.equalsIgnoreCase("yes"))
		{
			ret = "checked";
		}
		return ret;
	}
	
	private String getLabel(String col, String value, String currentValue) throws ServiceException {
		
		String label = "";
		
		if (col!=null && col.equalsIgnoreCase("TITLE"))
		{
			label = "'Title' changed ";
		}else if (col != null && col.equalsIgnoreCase("DESCRIPTION"))
		{
			label = "'Description' changed";
		}else if (col != null && col.equalsIgnoreCase("COORDINATOR_ROLE"))
		{
			label = "'National reporting coordinators role' changed";
		}else if (col != null && col.equalsIgnoreCase("COORDINATOR_ROLE_SUF"))
		{
			label = "'National reporting coordinators suffix' changed";
			value = getSuffixValue(value);
			currentValue = getSuffixValue(currentValue);
		}else if (col != null && col.equalsIgnoreCase("COORDINATOR"))
		{
			label = "'National reporting coordinators name' changed";
		}else if (col != null && col.equalsIgnoreCase("COORDINATOR_URL"))
		{
			label = "'National reporting coordinators URL' changed";
		}else if (col != null && col.equalsIgnoreCase("RESPONSIBLE_ROLE"))
		{
			label = "'National reporting contacts role' changed";
		}else if (col != null && col.equalsIgnoreCase("RESPONSIBLE_ROLE_SUF"))
		{
			label = "'National reporting contacts suffix' changed";
			value = getSuffixValue(value);
			currentValue = getSuffixValue(currentValue);
		}else if (col != null && col.equalsIgnoreCase("NATIONAL_CONTACT"))
		{
			label = "'National reporting contacts name' changed";
		}else if (col != null && col.equalsIgnoreCase("NATIONAL_CONTACT_URL"))
		{
			label = "'National reporting contacts URL' changed";
		}else if (col != null && col.equalsIgnoreCase("REPORT_FREQ_MONTHS"))
		{
			label = "'Reporting frequency in months' changed";
		}else if (col != null && col.equalsIgnoreCase("REPORT_FREQ"))
		{
			label = "'Reporting frequency' changed";
		}else if (col != null && col.equalsIgnoreCase("REPORT_FREQ_DETAILS"))
		{
			label = "'Reporting frequency details' changed";
		}else if (col != null && col.equalsIgnoreCase("FIRST_REPORTING"))
		{
			label = "'Baseline reporting date' changed";
		}else if (col != null && col.equalsIgnoreCase("VALID_TO"))
		{
			label = "'Valid to' changed";
		}else if (col != null && col.equalsIgnoreCase("NEXT_DEADLINE"))
		{
			label = "'Next due date' changed";
		}else if (col != null && col.equalsIgnoreCase("NEXT_DEADLINE2"))
		{
			label = "'Due date after next due (calculated automatically)' changed";
		}else if (col != null && col.equalsIgnoreCase("NEXT_REPORTING"))
		{
			label = "'Reporting date' changed";
		}else if (col != null && col.equalsIgnoreCase("DATE_COMMENTS"))
		{
			label = "'Date comments' changed";
		}else if (col != null && col.equalsIgnoreCase("FORMAT_NAME"))
		{
			label = "'Name of reporting guidelines' changed";
		}else if (col != null && col.equalsIgnoreCase("REPORT_FORMAT_URL"))
		{
			label = "'URL to reporting guidelines' changed";
		}else if (col != null && col.equalsIgnoreCase("VALID_SINCE"))
		{
			label = "'Format valid since' changed";
		}else if (col != null && col.equalsIgnoreCase("REPORTING_FORMAT"))
		{
			label = "'Reporting guidelines -Extra info' changed";
		}else if (col != null && col.equalsIgnoreCase("LOCATION_INFO"))
		{
			label = "'Name of repository' changed";
		}else if (col != null && col.equalsIgnoreCase("LOCATION_PTR"))
		{
			label = "'URL to repository' changed";
		}else if (col != null && col.equalsIgnoreCase("DATA_USED_FOR"))
		{
			label = "'Data used for (URL)' changed";
		}else if (col != null && col.equalsIgnoreCase("LEGAL_MORAL"))
		{
			label = "'Obligation type' changed";
		}else if (col != null && col.equalsIgnoreCase("PARAMETERS"))
		{
			label = "'Parameters' changed";
		}else if (col != null && col.equalsIgnoreCase("EEA_PRIMARY"))
		{
			label = "'This obligation is an Eionet core data flow' changed";
			value = getChkValue(value);
			currentValue = getChkValue(currentValue);
		}else if (col != null && col.equalsIgnoreCase("EEA_CORE"))
		{
			label = "'This obligation is used for EEA Core set of indicators' changed";
			value = getChkValue(value);
			currentValue = getChkValue(currentValue);
		}else if (col != null && col.equalsIgnoreCase("FLAGGED"))
		{
			label = "'This obligation is flagged' changed";
			value = getChkValue(value);
			currentValue = getChkValue(currentValue);
		}else if (col != null && col.equalsIgnoreCase("DPSIR_D"))
		{
			label = "'DPSIR D' changed";
			value = getDpsirValue(value);
			currentValue = getDpsirValue(value);
		}else if (col != null && col.equalsIgnoreCase("DPSIR_P"))
		{
			label = "'DPSIR P' changed";
			value = getDpsirValue(value);
			currentValue = getDpsirValue(currentValue);
		}else if (col != null && col.equalsIgnoreCase("OVERLAP_URL"))
		{
			label = "'URL of overlapping obligation' changed";
		}else if (col != null && col.equalsIgnoreCase("COMMENT"))
		{
			label = "'General comments' changed";
		}else if (col != null && col.equalsIgnoreCase("AUTHORITY"))
		{
			label = "'Authority giving rise to the obligation' changed";
		}else if (col != null && col.equalsIgnoreCase("RM_VERIFIED"))
		{
			label = "'Verified' changed";
		}else if (col != null && col.equalsIgnoreCase("RM_VERIFIED_BY"))
		{
			label = "'Verified by' changed";
		}else if (col != null && col.equalsIgnoreCase("RM_NEXT_UPDATE"))
		{
			label = "'Next update due' changed";
		}else if (col != null && col.equalsIgnoreCase("VALIDATED_BY"))
		{
			label = "'Validated by' changed";
		}else if (col != null && col.equalsIgnoreCase("FK_CLIENT_ID"))
		{
			label = "'Report to' changed";
			value = clientService.getOrganisationNameByID(value);
			currentValue = clientService.getOrganisationNameByID(currentValue);
		}else if (col != null && col.equalsIgnoreCase("CONTINOUS_REPORTING"))
		{
			label = "'Continuous reporting' changed";
		}else if (col != null && col.equalsIgnoreCase("LAST_UPDATE"))
		{
			label = "'Last update' changed";
		}else if (col != null && col.equalsIgnoreCase("LAST_HARVESTED"))
		{
			label = "'Last harvested date' changed";
		}else if (col != null && col.equalsIgnoreCase("TERMINATE"))
		{
			label = "'Terminate' changed";
		}
		
		label = label + " from '" + value + "' to '" + currentValue + "'";
		
		return label;
				
	}
	
	private void sendEvent(boolean isUpdate, Obligations pObligations, Integer obligation_id, long ts) throws ServiceException
	{
		String userName = getUserName();
		FileServiceIF fileService = RODServices.getFileService();
		
		try {		
			Vector<Vector<String>> lists = new Vector<Vector<String>>();
			Vector<String> list = new Vector<String>();
			long timestamp = System.currentTimeMillis();
			String events = "http://rod.eionet.europa.eu/events/" + timestamp;
			
			//int obligation_id = Integer.valueOf(pObligationsID).intValue();
			
			if (isUpdate)
			{
				list.add(events);
				list.add("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
				list.add(Attrs.SCHEMA_RDF + "ObligationChange");
				lists.add(list);
				
				list = new Vector<String>();
				list.add(events);
				String et_schema = fileService.getStringProperty(FileServiceIF.UNS_EVENTTYPE_PREDICATE);
				list.add(et_schema);
				list.add("Obligation change");
				lists.add(list);
				
				list = new Vector<String>();
				list.add(events);
				list.add("http://purl.org/dc/elements/1.1/title");
				list.add("Obligation change");
				lists.add(list);
				
			} else
			{
				list.add(events);
				list.add("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
				list.add(Attrs.SCHEMA_RDF + "NewObligation");
				lists.add(list);
				
				list = new Vector<String>();
				list.add(events);
				String et_schema = fileService.getStringProperty(FileServiceIF.UNS_EVENTTYPE_PREDICATE);
				list.add(et_schema);
				list.add("New Obligation");
				lists.add(list);
				
				list = new Vector<String>();
				list.add(events);
				list.add("http://purl.org/dc/elements/1.1/title");
				list.add("New Obligation");
				lists.add(list);
			}
			
			list = new Vector<String>();
			list.add(events);
			String obl_schema = fileService.getStringProperty(FileServiceIF.UNS_OBLIGATION_PREDICATE);
			list.add(obl_schema);
			list.add(pObligations.getOblTitle());
			lists.add(list);
			
			/*Vector<Map<String, String>> countries = RODServices.getDbService().getSpatialDao().getObligationsCountries(obligation_id);
			
			for (Enumeration<Map<String,String>> en = countries.elements(); en.hasMoreElements();)
			{
				Map<String, String> hash = en.nextElement();
				list = new Vector<String>();
				list.add(events);
				String loc_schema = fileService.getStringProperty(FileServiceIF.UNS_COUNTRY_PREDICATE);
				list.add(loc_schema);
				list.add(hash.get("name"));
				lists.add(list);
			}*/
			
			list = new Vector<String>();
			list.add(events);
			list.add(Attrs.SCHEMA_RDF + "responsiblerole");
			list.add(pObligations.getResponsibleRole());
			lists.add(list);
			
			list = new Vector<String>();
			list.add(events);
			list.add(Attrs.SCHEMA_RDF + "actor");
			list.add(userName);
			lists.add(list);
			
			if (isUpdate) 
			{
				Vector<String> changes = getChanges(obligation_id, ts);
				for (Enumeration<String> en = changes.elements(); en.hasMoreElements();)
				{
					String label = en.nextElement();
					list = new Vector<String>();
					list.add(events);
					list.add(Attrs.SCHEMA_RDF + "change");
					list.add(label);
					lists.add(list);
				}
			}
			
			list = new Vector<String>();
			list.add(events);
			list.add("http://purl.org/dc/elements/1.1/identifier");
			String url = "http://rod.eionet.europa.eu/obligations/" + obligation_id;
			list.add(url);
			
			lists.add(list);
			
			if (lists.size() > 0)
			{
				UNSEventSender.makeCall(lists);
			}
		}catch (RuntimeException e) {
			throw new ResourceNotFoundException(e.getMessage());
		}
		
		
	}
		
}

