package eionet.rod.controller;

import eionet.rod.service.SpatialService;
import eionet.rod.model.Spatial;
import eionet.rod.model.BreadCrumb;
import eionet.rod.util.BreadCrumbs;
import eionet.rod.util.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

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

  
}
