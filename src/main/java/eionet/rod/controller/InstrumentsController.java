package eionet.rod.controller;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eionet.rod.Attrs;
import eionet.rod.IAuthenticationFacade;
import eionet.rod.UNSEventSender;
import eionet.rod.dao.ClientService;
import eionet.rod.dao.SourceService;
import eionet.rod.dao.UndoService;
import eionet.rod.model.ClientDTO;
import eionet.rod.model.HierarchyInstrumentDTO;
import eionet.rod.model.InstrumentClassificationDTO;
import eionet.rod.model.InstrumentFactsheetDTO;
import eionet.rod.model.InstrumentsListDTO;
import eionet.rod.model.UndoDTO;
import eionet.rod.service.FileServiceIF;
import eionet.rod.util.BreadCrumbs;
import eionet.rod.util.RODServices;
import eionet.rod.util.RODUtil;
import eionet.rod.util.exception.ResourceNotFoundException;
import eionet.rod.util.exception.ServiceException;

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
	
	@Autowired
	UndoService undoService;
	
	@Autowired
    IAuthenticationFacade authenticationFacade;
	
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
        
		BreadCrumbs.set(model, "Hierarchy");
		if (id == null) {
			id = 1;
		}
		InstrumentsListDTO hierarchyInstrument = sourceService.getHierarchyInstrument(id);
		boolean hasParent = true;
		if (hierarchyInstrument == null) {
			hasParent = false;
		}
				
		String hierarchyTree = sourceService.getHierarchy(id, hasParent, mode);
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
		if (!RODUtil.isNullOrEmpty(instrument.getSourceValidFrom())) {
        	instrument.setSourceValidFrom(RODUtil.strDate(instrument.getSourceValidFrom()));
        }
        if (!RODUtil.isNullOrEmpty(instrument.getSourceEcEntryIntoForce())) {
        	instrument.setSourceEcEntryIntoForce(RODUtil.strDate(instrument.getSourceEcEntryIntoForce()));
        }
        if (!RODUtil.isNullOrEmpty(instrument.getSourceEcAccession())) {
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
        if (!RODUtil.isNullOrEmpty(instrument.getSourceValidFrom())) {
        	instrument.setSourceValidFrom(RODUtil.strDate(instrument.getSourceValidFrom()));
        }
        if (!RODUtil.isNullOrEmpty(instrument.getSourceEcEntryIntoForce())) {
        	instrument.setSourceEcEntryIntoForce(RODUtil.strDate(instrument.getSourceEcEntryIntoForce()));
        }
        if (!RODUtil.isNullOrEmpty(instrument.getSourceEcAccession())) {
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
        		if (instrument.getClassifications().get(i).getClassId() != null) {
	        		if (selClassifications.get(j).getClassId().equals(instrument.getClassifications().get(i).getClassId())) {
	        			selClassifications.remove(j);
	            	}
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
		Authentication authentication = authenticationFacade.getAuthentication();
		long ts = System.currentTimeMillis();
		processEditDelete("U", authentication.getName(), instrument.getSourceId(), ts);
		sourceService.update(instrument);
		model.addAttribute("sourceId", instrument.getSourceId());        
		try {
			sendEvent(true, instrument, instrument.getSourceId(), ts);
		}catch(ServiceException e)
		{
			
		}
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
        BreadCrumbs.set(model, "Create a Legislative Instrument");
        model.addAttribute("activeTab", "instruments");
        model.addAttribute("title","Create a Legislative Instrument");
        if (message != null) model.addAttribute("message", message);
        return "instrumentEditForm";		
	}
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String addInstrument(InstrumentFactsheetDTO instrument, BindingResult bindingResult, ModelMap model) {
		Integer sourdeId = sourceService.insert(instrument);
		model.addAttribute("sourceId", sourdeId);
		try {
			sendEvent(false, instrument, sourdeId, 0);
		} catch (ServiceException e)
		{
			
		}
		return "redirect:edit";		
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public String deleteInstrument(InstrumentFactsheetDTO instrument) {
		Authentication authentication = authenticationFacade.getAuthentication();
		long ts = System.currentTimeMillis();
		processEditDelete("D", authentication.getName(), instrument.getSourceId(), ts);
		sourceService.delete(instrument.getSourceId());
		return "redirect:/instruments";
	}
	
	 private void processEditDelete(String state, String userName, Integer sourceId, long ts) {

		 if (state != null && state.equals("U")) {
			 undoService.insertIntoUndo(sourceId, "U", "T_SOURCE", "PK_SOURCE_ID", ts, "", "y");
			 undoService.insertIntoUndo(sourceId, "U", "T_CLIENT_SOURCE_LNK", "FK_SOURCE_ID", ts, "", "y");
		 }
		 
		 String url = "instruments/" + sourceId;
		 
		 undoService.insertIntoUndo(ts, "T_SOURCE", "REDIRECT_URL", "L", "y", "n", url, 0, "n");
		 undoService.insertIntoUndo(ts, "T_SOURCE", "A_USER", "K", "y", "n", userName, 0, "n");
		 undoService.insertIntoUndo(ts, "T_SOURCE", "TYPE", "T", "y", "n", "L", 0, "n");
		 
		 if (state != null && state.equals("D")) {
            String acl_path = "/instruments/" + sourceId;
            undoService.insertIntoUndo(ts, "T_SOURCE", "ACL", "ACL", "y", "n", acl_path, 0, "n");
        }
 
		 delActivity(state, sourceId, ts);
		 
	 }
	 
	 private void delActivity(String op, Integer sourceId, long ts) {
		 	
		 undoService.insertTransactionInfo(sourceId, "A", "T_CLIENT_SOURCE_LNK", "FK_SOURCE_ID", ts, "");
		 undoService.insertTransactionInfo(sourceId, "A", "T_SOURCE_LNK", "FK_SOURCE_CHILD_ID", ts, "AND CHILD_TYPE='S'");
		 undoService.insertTransactionInfo(sourceId, "A", "T_SOURCE_LNK", "FK_SOURCE_PARENT_ID", ts, "AND PARENT_TYPE='S'");
		 undoService.insertTransactionInfo(sourceId, "A", "T_SOURCE", "PK_SOURCE_ID", ts, "");
		 undoService.insertIntoUndo(sourceId, op, "T_SOURCE_LNK", "FK_SOURCE_CHILD_ID", ts, "AND CHILD_TYPE='S'", "y");
		 
		 if (op != null && op.equals("D")) {
			 undoService.insertIntoUndo(sourceId, op, "T_CLIENT_SOURCE_LNK", "FK_SOURCE_ID", ts, "", "y");
			 undoService.insertIntoUndo(sourceId, op, "T_SOURCE_LNK", "FK_SOURCE_PARENT_ID", ts, "AND PARENT_TYPE='S'", "y");
			 undoService.addObligationIdsIntoUndo(sourceId, ts, "T_SOURCE");
			 undoService.insertIntoUndo(sourceId, "D", "T_SOURCE", "PK_SOURCE_ID", ts, "", "y");
		 
		 }
	 }
		 
	private String getUserName()
	 {
		Authentication authentication = authenticationFacade.getAuthentication();
		String userName = authentication.getName();
		
		return userName;
	 }
	 
	 private String getLabel(String col, String value, String currentValue) throws ServiceException
	 {
		 String label = "";
		 
		 if (col != null && col.equalsIgnoreCase("TITLE")) {
             label = "'Title' changed ";
         } else if (col != null && col.equalsIgnoreCase("ALIAS")) {
             label = "'Short name' changed ";
         } else if (col != null && col.equalsIgnoreCase("SOURCE_CODE")) {
             label = "'Identification number' changed ";
         } else if (col != null && col.equalsIgnoreCase("DRAFT")) {
             label = "'Draft' changed ";
         } else if (col != null && col.equalsIgnoreCase("URL")) {
             label = "'URL to official text' changed ";
         } else if (col != null && col.equalsIgnoreCase("CELEX_REF")) {
             label = "'CELEX reference' changed ";
         } else if (col != null && col.equalsIgnoreCase("FK_CLIENT_ID")) {
             label = "'Issued by' changed ";
             value = clientService.getOrganisationNameByID(value);
             currentValue = clientService.getOrganisationNameByID(currentValue);
         } else if (col != null && col.equalsIgnoreCase("ISSUED_BY")) {
             label = "'Issuer' changed ";
         } else if (col != null && col.equalsIgnoreCase("ISSUED_BY_URL")) {
             label = "'URL to issuer' changed ";
         } else if (col != null && col.equalsIgnoreCase("VALID_FROM")) {
             label = "'Valid from' changed ";
         } else if (col != null && col.equalsIgnoreCase("GEOGRAPHIC_SCOPE")) {
             label = "'Geographic scope' changed ";
         } else if (col != null && col.equalsIgnoreCase("ABSTRACT")) {
             label = "'Abstract' changed ";
         } else if (col != null && col.equalsIgnoreCase("COMMENT")) {
             label = "'Comments' changed ";
         } else if (col != null && col.equalsIgnoreCase("EC_ENTRY_INTO_FORCE")) {
             label = "'EC entry into force' changed ";
         } else if (col != null && col.equalsIgnoreCase("EC_ACCESSION")) {
             label = "'EC accession' changed ";
         } else if (col != null && col.equalsIgnoreCase("SECRETARIAT")) {
             label = "'Secretariat' changed ";
         } else if (col != null && col.equalsIgnoreCase("SECRETARIAT_URL")) {
             label = "'URL to Secretariat homepage' changed ";
         } else if (col != null && col.equalsIgnoreCase("RM_VERIFIED")) {
             label = "'Verified' changed ";
         } else if (col != null && col.equalsIgnoreCase("RM_VERIFIED_BY")) {
             label = "'Verified by' changed ";
         } else if (col != null && col.equalsIgnoreCase("RM_NEXT_UPDATE")) {
             label = "'Next update due' changed ";
         } else if (col != null && col.equalsIgnoreCase("RM_VALIDATED_BY")) {
             label = "'Validated by' changed ";
         } else if (col != null && col.equalsIgnoreCase("LAST_UPDATE")) {
             label = "'Last update' changed ";
         } else if (col != null && col.equalsIgnoreCase("LAST_MODIFIED")) {
             label = "'Last modification' changed ";
         } else if (col != null && col.equalsIgnoreCase("LEGAL_NAME")) {
             label = "'Legal name' changed ";
         }
		 
		 label = label + " from '" + value + "' to '" + currentValue + "'";
		 
		 return label;
	 }
	 
	 private Vector<String> getChanges(Integer pInstrumentID, long ts) throws ServiceException
	 {
		 Vector<String> res_vec = new Vector<String>();
		 List<UndoDTO> undoList = undoService.getUndoInformation(ts, "U", "T_SOURCE");
		 InstrumentFactsheetDTO instrument = sourceService.getById(pInstrumentID);
		 String value = "";
		 for (int i = 0; i < undoList.size(); i++) {			 
			
			boolean aux = true; 
			UndoDTO undo = undoList.get(i);
			
			switch (undo.getCol()) {
			case "PK_SOURCE_ID":
				value = instrument.getSourceId().toString();				
				break;
			case "SOURCE_CODE": 
				value = instrument.getSourceCode();				
				break;
			case "TITLE":
				value = instrument.getSourceTitle();				
				break;
			case "CELEX_REF":
				value = instrument.getSourceCelexRef();				
				break;
			case "URL":
				value = instrument.getSourceUrl();				
				break;
			case "ALIAS":
				value = instrument.getSourceAlias();				
				break;
			case "VALID_FROM":
				value = instrument.getSourceValidFrom();				
				break;
			case "ABSTRACT":
				value = instrument.getSourceAbstract();				
				break;
			case "COMMENT":
				value = instrument.getSourceComment();				
				break;
			case "ISSUED_BY_URL":
				value = instrument.getSourceIssuedByUrl();				
				break;
			case "EC_ENTRY_INTO_FORCE":
				value = instrument.getSourceEcEntryIntoForce();				
				break;
			case "EC_ACCESSION":
				value = instrument.getSourceEcAccession();				
				break;
			case "SECRETARIAT":
				value = instrument.getSourceSecretariat();				
				break;
			case "SECRETARIAT_URL":
				value = instrument.getSourceSecretariatUrl();				
				break;
			case "TERMINATE":
				value = instrument.getSourceTerminate();				
				break;
			case "FK_TYPE_ID":
				value = instrument.getSourceFkTypeId().toString();				
				break;
			case "LAST_MODIFIED":
				value = instrument.getSourceLastModified();				
				break;
			case "ISSUED_BY":
				value = instrument.getSourceIssuedBy();				
				break;
			case "FK_CLIENT_ID":
				value = instrument.getClientId().toString();				
				break;
			case "LAST_UPDATE":
				value = instrument.getSourceLastUpdate();				
				break;
			case "LEGAL_NAME":
				value = instrument.getSourceLegalName();				
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
		 
		 return res_vec;
	 }
	 
	 private void sendEvent(boolean isUpdate, InstrumentFactsheetDTO pInstrument, Integer pInstrumentID, long ts) throws ServiceException
	 {
		 String userName = getUserName();
		 FileServiceIF fileService = RODServices.getFileService();
		 
		 try 
		 {
			 Vector<Vector<String>> lists = new Vector<Vector<String>>();
			 Vector<String> list = new Vector<String>();
			 long timestamp = System.currentTimeMillis();
			 String events = "http://rod.eionet.europa.eu/events/" + timestamp;
			 
			 if (isUpdate)
			 {
				 list.add(events);
				 list.add("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
				 list.add(Attrs.SCHEMA_RDF + "InstrumentChange");
				 lists.add(list);
				 
				 list = new Vector<String>();
				 list.add(events);
				 String et_schema = fileService.getStringProperty(FileServiceIF.UNS_EVENTTYPE_PREDICATE);
				 list.add(et_schema);
				 list.add("instrument change");
				 lists.add(list);
				 
				 list = new Vector<String>();
				 list.add(events);
				 list.add("http://purl.org/dc/elements/1.1/title");
				 list.add("instrument change");
				 lists.add(list);
			 } else
			 {
				 
				 list.add(events);
				 list.add("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
				 list.add(Attrs.SCHEMA_RDF + "NewInstrument");
				 lists.add(list);
				 
				 list =  new Vector<String>();
				 list.add(events);
				 String et_schema = fileService.getStringProperty(FileServiceIF.UNS_EVENTTYPE_PREDICATE);
				 list.add(et_schema);
				 list.add("New instrument");
				 lists.add(list);
				 
				 list = new Vector<String>();
				 list.add(events);
				 list.add("http://purl.org/dc/elements/1.1/title");
				 list.add("New instrument");
				 lists.add(list);
			 }
			 
			 list = new Vector<String>();
			 list.add(events);
			 String inst_schema = fileService.getStringProperty(FileServiceIF.UNS_INSTRUMENT_PREDICATE);
			 list.add(inst_schema);
			 list.add(pInstrument.getSourceTitle());
			 lists.add(list);
			 
			 list = new Vector<String>();
			 list.add(events);
			 list.add(Attrs.SCHEMA_RDF + "actor");
			 list.add(userName);
			 lists.add(list);
			 
			 if (isUpdate)
			 {
				 Vector <String> changes = getChanges(pInstrumentID, ts);
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
			 String url = "http://rod.eionet.europa.eu/instruments/" + pInstrumentID;
			 list.add(url);
			 lists.add(list);
			 
			 if (lists.size() > 0)
			 {
				 UNSEventSender.makeCall(lists);
			 }
			 
		 } catch (Exception e)
		 {
			 e.printStackTrace();
		 }
	 }
}
	

