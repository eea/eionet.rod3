package eionet.rod.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eionet.rod.Constants;
import eionet.rod.util.RODUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eionet.rod.dao.ClientService;
import eionet.rod.dao.IssueDao;

import eionet.rod.model.ClientDTO;
import eionet.rod.model.Issue;
import eionet.rod.model.Obligations;
import eionet.rod.model.Spatial;
import eionet.rod.service.FileServiceIF;
import eionet.rod.service.ObligationService;
import eionet.rod.service.SpatialService;
import eionet.rod.util.BreadCrumbs;
import eionet.rod.util.RODServices;
import eionet.rod.util.exception.ResourceNotFoundException;
import eionet.rod.util.exception.ServiceException;
import eionet.sparqlClient.helpers.QueryExecutor;
import eionet.sparqlClient.helpers.QueryResult;
import eionet.sparqlClient.helpers.ResultValue;


@Controller
public class SearchController {
	
	@Autowired
    SpatialService spatialService;

    @Autowired
    IssueDao issueDao;
    
    @Autowired
    ClientService clientService;
    
    @Autowired
    ObligationService obligationsService;

	private QueryResult result;
	
	FileServiceIF fileSrv;
	
	@RequestMapping(value = "/simpleSearch")
    public String simpleSearchHome(@RequestParam(required = false, value="expression") String expression, Model model) {
		model.addAttribute("title","Search");
        BreadCrumbs.set(model, "Search");
        model.addAttribute("activeTab", "home");
		if (expression != null) {
			String query = "PREFIX rod: <http://rod.eionet.europa.eu/schema.rdf#> "
                + "PREFIX dct: <http://purl.org/dc/terms/> "
                + "SELECT DISTINCT ?subject ?type ?found ?name "
                + "FROM <http://rod.eionet.europa.eu/obligations/rdf> "
                + "FROM <http://rod.eionet.europa.eu/instruments/rdf> "
                + "FROM <http://rod.eionet.europa.eu/clients/rdf> "
                + "WHERE { "
                + "?subject a ?type . FILTER (?type IN (rod:Instrument, rod:Obligation, rod:Client)) "
                + "?subject ?p ?found . ?found bif:contains \"'"+expression+"'\" . "
                + "OPTIONAL { { ?subject dct:title ?name } UNION { ?subject foaf:name ?name } } "
                + "}";
			
			
			try {
				fileSrv = RODServices.getFileService();
				String endpointURL = fileSrv.getStringProperty(FileServiceIF.CR_SPARQL_ENDPOINT);
				//String CRSparqlEndpoint = "http://cr.eionet.europa.eu/sparql";
				QueryExecutor executor = new QueryExecutor();
	            executor.executeQuery(endpointURL, query);
	            result = executor.getResults();
	            ArrayList<ArrayList<String>> lists = removeDuplicates();
	            model.addAttribute("listItem", lists);
			} catch (ServiceException e) {
				String messageExcep = e.toString();
	            model.addAttribute("message", messageExcep);
	            
			}
			
            model.addAttribute("expression", expression);
		}
		return "simpleSearch";    	
    }
	
	@RequestMapping(value = "/simpleSearch", method = RequestMethod.POST)
    public String simpleSearch(@RequestParam("expression") String expression, ModelMap model) {
		model.addAttribute("expression", expression);
		return "redirect:simpleSearch";    	
    }	
	
	private ArrayList<ArrayList<String>> removeDuplicates() {
        List<String> existingSubjects = new ArrayList<>();
		ArrayList<ArrayList<String>> lists = new ArrayList<>();
        if (result != null && result.getRows() != null) {
            ArrayList<HashMap<String, ResultValue>> rows = result.getRows();
            for (HashMap<String, ResultValue> row : rows) {
                String found = row.get("found").toString();
                found = RODUtil.truncateText(found, Constants.TEXT_MAX_LEN);
                String subject;
                if (row.get("subject") != null) {
                    subject = row.get("subject").toString();
                } else {
                    subject = null;
                }
                String name;
                if (row.get("name") != null) {
                    name = row.get("name").toString();
                } else {
                    name = null;
                }
                String type = row.get("type").toString();
                type = type.substring(type.indexOf("#") + 1);
                ArrayList<String> list = new ArrayList<>();
                list.add(found);
                list.add(subject);
                list.add(name);
                list.add(type);
                if (subject != null && !existingSubjects.contains(subject)) {
                    existingSubjects.add(subject);
                    lists.add(list);
                }
            }
        }
        return lists;
    }	

    @RequestMapping(value = "/advancedSearch")
    public String search_deadlines(final Model model) throws Exception {
       
    	model.addAttribute("title","Advanced search");
        BreadCrumbs.set(model, "Advanced search");
        
        //Countries/territories
        List<Spatial> countries = spatialService.findAll();
        model.addAttribute("allCountries", countries);
       
        //Environmental issues
        List<Issue> issues = issueDao.findAllIssuesList();
        model.addAttribute("allIssues", issues);
    	
        //Countries/territories
        List<ClientDTO> clients = clientService.getAllClients();
        model.addAttribute("allClients", clients);
        model.addAttribute("activeTab", "advancedSearch");
        
        Obligations obligation = new Obligations();
        model.addAttribute("obligation", obligation);
        
        model.addAttribute("resultMessage", "0");
        
        return "search";
    	
    }
	
    @RequestMapping(value = "/advancedSearch", method = RequestMethod.POST)
    public String search_deadlines(Obligations obligation, final Model model) throws ResourceNotFoundException {
    
    	model.addAttribute("title","Advanced search");
    	BreadCrumbs.set(model, "Advanced search");
   
    	String anmode = null;
    	String issue = "0";
    	if (!"NI".equals(obligation.getIssueId())) {
    		issue = obligation.getIssueId();
       	}else {
       		anmode = obligation.getIssueId();
       	}
    	
    	String deadline = "5";
    	
    	model.addAttribute("allObligations",obligationsService.findObligationList(obligation.getClientId(),issue, obligation.getSpatialId(),null,deadline,anmode, obligation.getNextDeadlineFrom(), obligation.getNextDeadlineTo(), true));
        
    	model.addAttribute("obligation",obligation);
     	
    	//Countries/territories
    	List<Spatial> countries = spatialService.findAll();
    	model.addAttribute("allCountries", countries);
       
    	//Environmental issues
    	List<Issue> issues = issueDao.findAllIssuesList();
    	model.addAttribute("allIssues", issues);
    	
    	//Countries/territories
    	List<ClientDTO> clients = clientService.getAllClients();
    	model.addAttribute("allClients", clients);
    	
    	model.addAttribute("activeTab", "advancedSearch");
        
    	 model.addAttribute("resultMessage", "1");
    	
    	return "search";
    	
    }
    
	
}
