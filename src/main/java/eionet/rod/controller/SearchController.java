package eionet.rod.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eionet.rod.util.BreadCrumbs;
import eionet.sparqlClient.helpers.QueryExecutor;
import eionet.sparqlClient.helpers.QueryResult;
import eionet.sparqlClient.helpers.ResultValue;


@Controller
public class SearchController {
	
	private QueryResult result;
	
	@RequestMapping(value = "/simpleSearch")
    public String simpleSearchHome(@RequestParam(required = false, value="expression") String expression, Model model) {
		model.addAttribute("title","Search");
        BreadCrumbs.set(model, "Search");
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
			
			String CRSparqlEndpoint = "http://cr.eionet.europa.eu/sparql";
			QueryExecutor executor = new QueryExecutor();
            executor.executeQuery(CRSparqlEndpoint, query);
            result = executor.getResults();
            ArrayList<ArrayList<String>> lists = removeDuplicates();
            model.addAttribute("listItem", lists);
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
        List<String> existingSubjects = new ArrayList<String>();
		ArrayList<ArrayList<String>> lists = new ArrayList<ArrayList<String>>();
        if (result != null && result.getRows() != null) {
            ArrayList<HashMap<String, ResultValue>> rows = result.getRows();
            for (Iterator<HashMap<String, ResultValue>> it = rows.iterator(); it.hasNext(); ){
                HashMap<String, ResultValue> row = it.next();
                String found = row.get("found").toString();
                if (found.length() > 400) {
                	found = found.substring(0, 400);
                	found = found.concat("...");
                }
                String subject;
                if ( row.get("subject") != null) {
                	subject = row.get("subject").toString();
                } else {
                	subject = null;
                }       
                String name;
                if ( row.get("name") != null) {
                	name = row.get("name").toString();
                } else {
                	name = null;
                }
                String type = row.get("type").toString();
                type = type.substring(type.indexOf("#")+1);
                ArrayList<String> list = new ArrayList<String>();
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
	
	/*@RequestMapping(value = "/simpleSearch", method = RequestMethod.POST)
    public String simpleSearchHome(@RequestParam("expression") String expression, Model model) {
		System.out.println(expression);
		return "simpleSearch";    	
    }*/
	
}
