package eionet.rod.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eionet.rod.model.Delivery;
import eionet.rod.service.DeliveryService;
import eionet.rod.util.BreadCrumbs;


@Controller
@RequestMapping("/countrydeliveries")
public class DeliveryController {

    @Autowired
    DeliveryService deliveryService;
	
	 /**
     * View for all deliveries.
     *
     * @param model - contains attributes for the view
     * @param message
     * @return view name
     */
    @RequestMapping({"", "/", "/view"})
    public String viewClients(Model model, 
    							@RequestParam("actDetailsId") String actDetailsId, 
    							@RequestParam("spatialId") String spatialId) {
        BreadCrumbs.set(model, "Status of deliveries: ");
        model.addAttribute("title","Status of deliveries: ");
        model.addAttribute("activeTab", "spatial");
        
       List<Delivery> deliveries = deliveryService.getAllDelivery(actDetailsId, spatialId);
       model.addAttribute("allDeliveries", deliveries);
       
       return "countrydeliveries";
    }

	
	
	
}
