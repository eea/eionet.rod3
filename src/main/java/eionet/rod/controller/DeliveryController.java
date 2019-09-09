package eionet.rod.controller;

import eionet.rod.model.ClientDTO;
import eionet.rod.model.Delivery;
import eionet.rod.model.Obligations;
import eionet.rod.model.Spatial;
import eionet.rod.service.ClientService;
import eionet.rod.service.DeliveryService;
import eionet.rod.service.ObligationService;
import eionet.rod.service.SpatialService;
import eionet.rod.util.BreadCrumbs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
@RequestMapping("/countrydeliveries")
public class DeliveryController {

    @Autowired
    DeliveryService deliveryService;

    @Autowired
    SpatialService spatialService;

    @Autowired
    ObligationService obligationService;

    @Autowired
    ClientService clientService;

    /**
     * View for all deliveries.
     *
     * @param model        - contains attributes for the view
     * @param actDetailsId
     * @param spatialId
     * @return view name
     */
    @RequestMapping({"", "/", "/view"})
    public String viewClients(Model model,
                              @RequestParam("actDetailsId") String actDetailsId,
                              @RequestParam("spatialId") String spatialId) {

        List<Delivery> deliveries = deliveryService.getAllDelivery(actDetailsId, spatialId);
        model.addAttribute("allDeliveries", deliveries);

        Spatial country = spatialService.findOne(Integer.parseInt(spatialId));
        model.addAttribute("country", country);

        Obligations obligation = obligationService.findOblId(Integer.parseInt(actDetailsId));
        model.addAttribute("obligation", obligation);

        String status = "C";
        List<ClientDTO> clients = clientService.findOblClients(Integer.parseInt(actDetailsId), status);

        if (clients != null) {
            model.addAttribute("clients", clients);
        }

        BreadCrumbs.set(model, "Status of deliveries: " + country.getName());
        model.addAttribute("title", "Status of deliveries: " + country.getName());
        model.addAttribute("activeTab", "spatial");

        return "countrydeliveries";
    }


}
