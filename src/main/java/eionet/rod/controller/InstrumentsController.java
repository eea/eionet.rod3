package eionet.rod.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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

import eionet.rod.dao.ClientService;
import eionet.rod.dao.SourceService;
import eionet.rod.model.ClientDTO;
import eionet.rod.model.HierarchyInstrumentDTO;
import eionet.rod.model.InstrumentClassificationDTO;
import eionet.rod.model.InstrumentFactsheetDTO;
import eionet.rod.model.InstrumentsListDTO;
import eionet.rod.util.BreadCrumbs;
import eionet.rod.util.RODUtil;
import eionet.rod.util.exception.ResourceNotFoundException;

/**
 * Instruments managing controller.
 */
@Controller
@RequestMapping("/instruments")
public class InstrumentsController {
	
	@Autowired
	SourceService sourceService;
	
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
    
	
	@RequestMapping({"", "/", "/view"})
	public String instrumentsHome(Model model, @RequestParam(required = false) Integer id, @RequestParam(required = false) String mode, HttpServletRequest request) {
        
		
		String urlInstruments = request.getRequestURL().toString();
		
		BreadCrumbs.set(model, "Hierarchy");
		if (id == null) {
			id = 1;
		}
		InstrumentsListDTO hierarchyInstrument = sourceService.getHierarchyInstrument(id);
		boolean hasParent = true;
		if (hierarchyInstrument == null) {
			hasParent = false;
		}
				
		String hierarchyTree = sourceService.getHierarchy(id, hasParent, mode, urlInstruments);
		List<HierarchyInstrumentDTO> hierarchyInstruments = sourceService.getHierarchyInstruments(id);
		model.addAttribute("hierarchyInstrument", hierarchyInstrument);
		model.addAttribute("hierarchyTree", hierarchyTree);
		model.addAttribute("hierarchyInstruments", hierarchyInstruments);
		model.addAttribute("activeTab", "instruments");
		model.addAttribute("title","Legal instruments");
		
		if (mode != null && mode.equals("X")) {
			return "instrumentsx";
		} else {
			return "instruments";
		}
				
	}
		
	@RequestMapping(value = "/{sourceId}")
	public String sourceFactsheet(
			@PathVariable("sourceId") Integer sourceId, final Model model) throws Exception {
		
		model.addAttribute("sourceId", sourceId);
		InstrumentFactsheetDTO instrument = sourceService.getById(sourceId);
		if (instrument.getSourceValidFrom() != null && !instrument.getSourceValidFrom().equals("")) {
        	instrument.setSourceValidFrom(RODUtil.strDate(instrument.getSourceValidFrom()));
        }
        if (instrument.getSourceEcEntryIntoForce() != null && !instrument.getSourceEcEntryIntoForce().equals("")) {
        	instrument.setSourceEcEntryIntoForce(RODUtil.strDate(instrument.getSourceEcEntryIntoForce()));
        }
        if (instrument.getSourceEcAccession() != null && !instrument.getSourceEcAccession().equals("")) {
        	instrument.setSourceEcAccession(RODUtil.strDate(instrument.getSourceEcAccession()));
        }
        instrument.setSourceAbstract(RODUtil.replaceTags(instrument.getSourceAbstract()));
        instrument.setSourceComment(RODUtil.replaceTags(instrument.getSourceComment()));
		model.addAttribute("instrument", instrument);
		BreadCrumbs.set(model, instrument.getSourceAlias());
		model.addAttribute("activeTab", "instruments");
		model.addAttribute("title","Legal instruments");
		return "instrumentFactsheet";
		
	}

	@RequestMapping("/edit")
    public String editInstrumentForm(@RequestParam Integer sourceId, Model model,
        @RequestParam(required = false) String message) {
		model.addAttribute("form", "edit");
        model.addAttribute("sourceId", sourceId);
        BreadCrumbs.set(model, "Edit a Legislative Instrument");
        InstrumentFactsheetDTO instrument = sourceService.getById(sourceId);
        if (instrument.getParent() != null) {
        	instrument.setSourceLnkFKSourceParentId(instrument.getParent().getSourceId());
        }else {
        	instrument.setSourceLnkFKSourceParentId(-1);
        }
        if (instrument.getSourceValidFrom() != null && !instrument.getSourceValidFrom().equals("")) {
        	instrument.setSourceValidFrom(RODUtil.strDate(instrument.getSourceValidFrom()));
        }
        if (instrument.getSourceEcEntryIntoForce() != null && !instrument.getSourceEcEntryIntoForce().equals("")) {
        	instrument.setSourceEcEntryIntoForce(RODUtil.strDate(instrument.getSourceEcEntryIntoForce()));
        }
        if (instrument.getSourceEcAccession() != null && !instrument.getSourceEcAccession().equals("")) {
        	instrument.setSourceEcAccession(RODUtil.strDate(instrument.getSourceEcAccession()));
        }
        model.addAttribute("instrument", instrument);
        List<ClientDTO> clients = clientService.getAllClients();
        model.addAttribute("clients", clients);
        List<InstrumentFactsheetDTO> instruments = sourceService.getAllInstruments();
        model.addAttribute("instruments", instruments);
        model.addAttribute("title","Legal instruments");
        List<InstrumentClassificationDTO> selClassifications = sourceService.getAllClassifications();        
        for (int i = 0; instrument.getClassifications().size() > i; i++) {
        	for (int j = 0; selClassifications.size() > j; j++) {
        		if (selClassifications.get(j).getClassId() == instrument.getClassifications().get(i).getClassId()) {
        			selClassifications.remove(j);
            	}
        	}
        	
        }
        model.addAttribute("selClassifications", selClassifications);
        model.addAttribute("activeTab", "instruments");
        model.addAttribute("title","Legal instruments");
        if (message != null) model.addAttribute("message", message);        
        return "instrumentEditForm";
    }
	
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String editInstrument(InstrumentFactsheetDTO instrument, BindingResult bindingResult, ModelMap model) {
		sourceService.update(instrument);
        model.addAttribute("sourceId", instrument.getSourceId());
        return "redirect:edit";
    }
	
	@RequestMapping("/add")
	public String addInstrumentForm(Model model, @RequestParam(required = false) String message) {
		model.addAttribute("form", "add");
		List<ClientDTO> clients = clientService.getAllClients();
        model.addAttribute("clients", clients);
        List<InstrumentFactsheetDTO> instruments = sourceService.getAllInstruments();
        model.addAttribute("instruments", instruments);
        List<InstrumentClassificationDTO> selClassifications = sourceService.getAllClassifications();
        model.addAttribute("selClassifications", selClassifications);
        InstrumentFactsheetDTO instrument = new InstrumentFactsheetDTO();
        List<String> selectedClassifications = new ArrayList<String>();
        instrument.setSelectedClassifications(selectedClassifications);
        model.addAttribute("instrument", instrument);
        BreadCrumbs.set(model, "Edit a Legislative Instrument");
        model.addAttribute("activeTab", "instruments");
        model.addAttribute("title","Legal instruments");
        if (message != null) model.addAttribute("message", message);
        return "instrumentEditForm";		
	}
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String addInstrument(InstrumentFactsheetDTO instrument, BindingResult bindingResult, ModelMap model) {
		Integer sourdeId = sourceService.insert(instrument);
		model.addAttribute("sourceId", sourdeId);
		return "redirect:edit";		
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public String deleteInstrument(InstrumentFactsheetDTO instrument) {
		sourceService.delete(instrument.getSourceId());
		return "redirect:/instruments";
	}
	
}
