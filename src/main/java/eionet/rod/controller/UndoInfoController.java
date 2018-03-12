package eionet.rod.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eionet.rod.dao.ClientService;
import eionet.rod.dao.IssueDaoImpl;
import eionet.rod.dao.SourceService;
import eionet.rod.dao.UndoService;
import eionet.rod.model.ClientDTO;
import eionet.rod.model.InstrumentFactsheetDTO;
import eionet.rod.model.Issue;
import eionet.rod.model.Obligations;
import eionet.rod.model.Spatial;
import eionet.rod.model.UndoDTO;
import eionet.rod.service.ObligationService;
import eionet.rod.service.SpatialService;
import eionet.rod.util.BreadCrumbs;
import eionet.rod.util.RODUtil;
import eionet.rod.util.exception.ResourceNotFoundException;

@Controller
@RequestMapping("/undoinfo")
public class UndoInfoController {
	
	@ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFoundException() {
        return "404";
    }
	
	@Autowired
    ObligationService obligationsService;
	
	@Autowired
    UndoService undoService;
	
	@Autowired
	IssueDaoImpl issueService;
	
	@Autowired
	ClientService clientService;
	
	@Autowired
	SpatialService spatialService;
	
	@Autowired
	SourceService sourceService;
	
	@RequestMapping({"", "/"}) 
	public String undoInfoHome(Model model, @RequestParam long ts, @RequestParam String tab, @RequestParam String op, @RequestParam Integer id, @RequestParam String user) {
		
		String time = RODUtil.miliseconds2Date(ts);
		model.addAttribute("time", time);
		model.addAttribute("user", user);
		model.addAttribute("table", tab);
		switch (op) {
			case "U":
				model.addAttribute("operation", "UPDATE");	
				break;
			case "D":
				model.addAttribute("operation", "DELETE");	
				break;
			case "UN":
				model.addAttribute("operation", "UNDO");	
				break;
			default: break;
		}			
		
		List<UndoDTO> undoList = undoService.getUndoList(ts, tab, op);
		model.addAttribute("undoList", undoList);
		
		ArrayList<String> currentValues = new ArrayList<String>();
		
		if (tab.equals("T_OBLIGATION")) {
			
			if (!op.equals("D")) {
				
				boolean isDelete = undoService.isDelete(tab, "PK_RA_ID", id);
				
				if (isDelete == false) {
					Obligations obligation = obligationsService.findOblId(id);					
					
					currentValues.add(obligation.getAuthority());	
					currentValues.add(obligation.getComment());	
					currentValues.add(obligation.getContinousReporting());	
					currentValues.add(obligation.getCoordinator());	
					currentValues.add(obligation.getCoordinatorRole());	
					currentValues.add(obligation.getCoordinatorRoleSuf());	
					currentValues.add(obligation.getCoordinatorUrl());
					currentValues.add(obligation.getDataUsedFor());
					currentValues.add(obligation.getDataUsedForUrl());
					currentValues.add(obligation.getDateComments());
					currentValues.add(obligation.getDescription());
					if (obligation.getEeaCore() == null) {
						currentValues.add(null);
					} else {
						currentValues.add(obligation.getEeaCore().toString());
					}
					if (obligation.getEeaPrimary() == null) {
						currentValues.add(null);
					} else {
						currentValues.add(obligation.getEeaPrimary().toString());
					}
					currentValues.add(obligation.getFirstReporting());
					currentValues.add(obligation.getDeliveryCountryId());
					currentValues.add(obligation.getSourceId());
					if (obligation.getFlagged() == null) {
						currentValues.add(null);
					} else {
						currentValues.add(obligation.getFlagged().toString());
					}
					currentValues.add(obligation.getFormatName());
					currentValues.add(obligation.getLastHarvested());
					currentValues.add(obligation.getLastUpdate());
					currentValues.add(obligation.getLocationInfo());
					currentValues.add(obligation.getLocationPtr());
					currentValues.add(obligation.getNationalContact());
					currentValues.add(obligation.getNationalContactUrl());
					currentValues.add(obligation.getNextDeadline());
					currentValues.add(obligation.getNextDeadline2());
					currentValues.add(obligation.getNextReporting());
					currentValues.add(obligation.getObligationId().toString());
					currentValues.add(obligation.getReportingFormat());
					currentValues.add(obligation.getReportFormatUrl());
					currentValues.add(obligation.getReportFreq());
					currentValues.add(obligation.getReportFreqDetail());
					currentValues.add(obligation.getReportFreqMonths());
					currentValues.add(obligation.getResponsibleRole());
					currentValues.add(obligation.getResponsibleRoleSuf());
					currentValues.add(obligation.getTerminate());
					currentValues.add(obligation.getOblTitle());
					currentValues.add(obligation.getValidSince());
					currentValues.add(obligation.getValidTo());
					
				} else {
					
					currentValues.add("");
					currentValues.add("");
					currentValues.add("");
					currentValues.add("");
					currentValues.add("");
					currentValues.add("");
					currentValues.add("");
					currentValues.add("");
					currentValues.add("");
					currentValues.add("");
					
					currentValues.add("");
					currentValues.add(null);
					currentValues.add(null);
					currentValues.add(null);
					currentValues.add(null);
					currentValues.add("");
					currentValues.add(null);
					currentValues.add("");
					currentValues.add(null);
					currentValues.add("");
					
					currentValues.add("");
					currentValues.add("");
					currentValues.add("");
					currentValues.add("");
					currentValues.add(null);
					currentValues.add(null);
					currentValues.add("");
					currentValues.add("");
					currentValues.add("");
					currentValues.add("");
					
					currentValues.add("");
					currentValues.add(null);
					currentValues.add("");
					currentValues.add("");
					currentValues.add("");
					currentValues.add("");
					currentValues.add("");
					currentValues.add(null);
					currentValues.add(null);
					
				}
				
			}						
			
			List<UndoDTO> undoCountries = undoService.getUndoList(ts, "T_RASPATIAL_LNK", op);
			HashMap<Integer, String> fkSpatialIds = new HashMap<Integer, String>();
			HashMap<Integer, String> voluntaries = new HashMap<Integer, String>();
			StringBuffer undoCountriesString = new StringBuffer();
			StringBuffer undoCountriesVolString = new StringBuffer();
			if (undoCountries != null && !op.equals("D")) {
				for (int i = 0; i < undoCountries.size(); i++) {
					if (undoCountries.get(i).getCol().equals("FK_SPATIAL_ID")) {
						fkSpatialIds.put(undoCountries.get(i).getSubTransNr(), undoCountries.get(i).getValue());
					}
					if (undoCountries.get(i).getCol().equals("VOLUNTARY")) {
						voluntaries.put(undoCountries.get(i).getSubTransNr(), undoCountries.get(i).getValue());
					}
				}
				
				Spatial country = null;
				
				for (int i = 0; i < fkSpatialIds.size(); i++) {
					country = spatialService.findOne(Integer.parseInt(fkSpatialIds.get(i)));
					if (voluntaries.get(i).equals("Y")) {					
						undoCountriesVolString.append(country.getName()).append(", ");
					} else {
						undoCountriesString.append(country.getName()).append(", ");
					}
				}
				if (undoCountriesString.length() > 1) {
					undoCountriesString.replace(undoCountriesString.length()-2, undoCountriesString.length(), "");
				}
				if (undoCountriesVolString.length() > 1) {
					undoCountriesVolString.replace(undoCountriesVolString.length()-2, undoCountriesVolString.length(), "");
				}
				model.addAttribute("undoCountries", undoCountriesString.toString());
				model.addAttribute("undoCountriesVol", undoCountriesVolString.toString());
			} else {
				model.addAttribute("undoCountries", "");
				model.addAttribute("undoCountriesVol", "");
			}
			
			List<Spatial> currentCountries = obligationsService.findAllCountriesByObligation(id, "N");
			StringBuffer currentCountriesString = new StringBuffer();
			if (currentCountries != null) {			
				for (int i = 0; i < currentCountries.size(); i++) {
					currentCountriesString.append(currentCountries.get(i).getName());
					if (i+1 < currentCountries.size()) {
						currentCountriesString.append(", ");
					}
				}
				model.addAttribute("currentCountries", currentCountriesString.toString());
			} else {
				model.addAttribute("currentCountries", "");
			}
			
			List<Spatial> currentCountriesVol = obligationsService.findAllCountriesByObligation(id, "Y");
			StringBuffer currentCountriesVolString = new StringBuffer();
			if (currentCountriesVol != null) {			
				for (int i = 0; i < currentCountriesVol.size(); i++) {
					currentCountriesVolString.append(currentCountriesVol.get(i).getName());
					if (i+1 < currentCountriesVol.size()) {
						currentCountriesVolString.append(", ");
					}
				}
				model.addAttribute("currentCountriesVol", currentCountriesVolString.toString());
			} else {
				model.addAttribute("currentCountriesVol", "");
			}
			
			StringBuffer addedCountries = new StringBuffer(); 		
			StringBuffer removedCountries = new StringBuffer();
			if (undoCountries == null && currentCountries == null) {
				model.addAttribute("addedCountries", "");
				model.addAttribute("removedCountries", "");
			} else if (undoCountries != null && currentCountries == null) {
				model.addAttribute("addedCountries", ""); 
				model.addAttribute("removedCountries", undoCountriesString.toString());
			} else if (undoCountries == null && currentCountries != null) {
				model.addAttribute("addedCountries", currentCountriesString.toString()); 
				model.addAttribute("removedCountries", "");
			} else {
				String[] currentCountriesStringList = currentCountriesString.toString().split(", ");
				ArrayList<String> current = new ArrayList<String>();
				String[] undoCountriesStringList  = undoCountriesString.toString().split(", ");
				ArrayList<String> undo = new ArrayList<String>();
				
				for (int i = 0; i < currentCountriesStringList.length; i++) {
					current.add(currentCountriesStringList[i]);
				}
				
				for (int i = 0; i < undoCountriesStringList.length; i++) {
					undo.add(undoCountriesStringList[i]);
				}
				
				for (int i = 0; i < undo.size(); i++) {
					if (!current.contains(undo.get(i))) {
						removedCountries.append(undo.get(i)).append(", ");
					}
				}
				String removedCountriesString = "";
				if (removedCountries.length() > 1) {
					removedCountriesString = removedCountries.substring(0, removedCountries.length()-2);
				}
				
				for (int i = 0; i < current.size(); i++) {
					if (!undo.contains(current.get(i))) {
						addedCountries.append(current.get(i)).append(", ");
					}
				}
				String addedCountriesString = "";
				if (addedCountries.length() > 1) {
					addedCountriesString = addedCountries.substring(0, addedCountries.length()-2);
				}
				
				model.addAttribute("addedCountries", addedCountriesString);
				model.addAttribute("removedCountries", removedCountriesString);
				
			}
			
			StringBuffer addedCountriesVol = new StringBuffer(); 		
			StringBuffer removedCountriesVol = new StringBuffer();
			if (undoCountries == null && currentCountriesVol == null) {
				model.addAttribute("addedCountriesVol", "");
				model.addAttribute("removedCountriesVol", "");
			} else if (undoCountries != null && currentCountriesVol == null) {
				model.addAttribute("addedCountriesVol", ""); 
				model.addAttribute("removedCountriesVol", undoCountriesVolString.toString());
			} else if (undoCountries == null && currentCountriesVol != null) {
				model.addAttribute("addedCountriesVol", currentCountriesVolString.toString()); 
				model.addAttribute("removedCountriesVol", "");
			} else {
				String[] currentCountriesVolStringList = currentCountriesVolString.toString().split(", ");
				ArrayList<String> current = new ArrayList<String>();
				String[] undoCountriesVolStringList  = undoCountriesVolString.toString().split(", ");
				ArrayList<String> undo = new ArrayList<String>();
				
				for (int i = 0; i < currentCountriesVolStringList.length; i++) {
					current.add(currentCountriesVolStringList[i]);
				}
				
				for (int i = 0; i < undoCountriesVolStringList.length; i++) {
					undo.add(undoCountriesVolStringList[i]);
				}
				
				for (int i = 0; i < undo.size(); i++) {
					if (!current.contains(undo.get(i))) {
						removedCountriesVol.append(undo.get(i)).append(", ");
					}
				}
				String removedCountriesVolString = "";
				if (removedCountriesVol.length() > 1) {
					removedCountriesVolString = removedCountriesVol.substring(0, removedCountriesVol.length()-2);
				}
				
				for (int i = 0; i < current.size(); i++) {
					if (!undo.contains(current.get(i))) {
						addedCountriesVol.append(current.get(i)).append(", ");
					}
				}
				String addedCountriesVolString = "";
				if (addedCountriesVol.length() > 1) {
					addedCountriesVolString = addedCountriesVol.substring(0, addedCountriesVol.length()-2);
				}
				
				model.addAttribute("addedCountriesVol", addedCountriesVolString);
				model.addAttribute("removedCountriesVol", removedCountriesVolString);
				
			}
			
			List<UndoDTO> undoIssues = undoService.getUndoList(ts, "T_RAISSUE_LNK", op);
			//ArrayList<Issue> undoIssues = new ArrayList<Issue>();
			StringBuffer undoIssuesString = new StringBuffer();
			if (undoIssues != null && !op.equals("D")) {					
				for (int i = 0; i < undoIssues.size(); i++ ) {
					if (undoIssues.get(i).getCol().equals("FK_ISSUE_ID")) {
						Issue issue = issueService.findById(Integer.parseInt(undoIssues.get(i).getValue()));
						//undoIssues.add(issue);
						undoIssuesString.append(issue.getIssueName());
						undoIssuesString.append(", ");
					}
					
				}
				if (undoIssuesString.length() > 1) {
					undoIssuesString.replace(undoIssuesString.length()-2, undoIssuesString.length(), "");
				}			
				model.addAttribute("undoIssues", undoIssuesString.toString());
			} else {
				model.addAttribute("undoIssues", "");
			}
			
			List<Issue> currentIssues = obligationsService.findAllIssuesbyObligation(id);
			StringBuffer currentIssuesString = new StringBuffer();
			if (currentIssues != null) {			
				for (int i = 0; i < currentIssues.size(); i++) {
					currentIssuesString.append(currentIssues.get(i).getIssueName());
					if (i+1 < currentIssues.size()) {
						currentIssuesString.append(", ");
					}
				}
				model.addAttribute("currentIssues", currentIssuesString.toString());
			} else {
				model.addAttribute("currentIssues", "");
			}
			
			StringBuffer addedIssues = new StringBuffer(); 		
			StringBuffer removedIssues = new StringBuffer();
			if (undoIssues == null && currentIssues == null) {
				model.addAttribute("addedIssues", "");
				model.addAttribute("removedIssues", "");
			} else if (undoIssues != null && currentIssues == null) {
				model.addAttribute("addedIssues", ""); 
				model.addAttribute("removedIssues", undoIssuesString.toString());
			} else if (undoIssues == null && currentIssues != null) {
				model.addAttribute("addedIssues", currentIssuesString.toString()); 
				model.addAttribute("removedIssues", "");
			} else {
				String[] currentIssuesStringList = currentIssuesString.toString().split(", ");
				ArrayList<String> current = new ArrayList<String>();
				String[] undoIssuesStringList  = undoIssuesString.toString().split(", ");
				ArrayList<String> undo = new ArrayList<String>();
				
				for (int i = 0; i < currentIssuesStringList.length; i++) {
					current.add(currentIssuesStringList[i]);
				}
				
				for (int i = 0; i < undoIssuesStringList.length; i++) {
					undo.add(undoIssuesStringList[i]);
				}
				
				for (int i = 0; i < undo.size(); i++) {
					if (!current.contains(undo.get(i))) {
						removedIssues.append(undo.get(i)).append(", ");
					}
				}
				String removedIssuesString = "";
				if (removedIssues.length() > 1) {
					removedIssuesString = removedIssues.substring(0, removedIssues.length()-2);
				}
				
				for (int i = 0; i < current.size(); i++) {
					if (!undo.contains(current.get(i))) {
						addedIssues.append(current.get(i)).append(", ");
					}
				}
				String addedIssuesString = "";
				if (addedIssues.length() > 1) {
					addedIssuesString = addedIssues.substring(0, addedIssues.length()-2);
				}
				
				model.addAttribute("addedIssues", addedIssuesString); 
				model.addAttribute("removedIssues", removedIssuesString);
			}
			
			List<UndoDTO> undoClients = undoService.getUndoList(ts, "T_CLIENT_OBLIGATION_LNK", op);
			StringBuffer undoClientsString = new StringBuffer();
			if (undoClients != null && !op.equals("D")) {					
				for (int i = 0; i < undoClients.size(); i++ ) {
					if (undoClients.get(i).getCol().equals("FK_CLIENT_ID")) {
						ClientDTO client = clientService.getById((Integer.parseInt(undoClients.get(i).getValue())));
						undoClientsString.append(client.getName());
						undoClientsString.append(", ");
					}
					
				}
				if (undoClientsString.length() > 1) {
					undoClientsString.replace(undoClientsString.length()-2, undoClientsString.length(), "");
				}			
				model.addAttribute("undoClients", undoClientsString.toString());
			} else {
				model.addAttribute("undoClients", "");
			}
			
			List<ClientDTO> currentClients = obligationsService.findAllClientsByObligation(id);
			StringBuffer currentClientsString = new StringBuffer();
			if (currentClients != null) {			
				for (int i = 0; i < currentClients.size(); i++) {
					currentClientsString.append(currentClients.get(i).getName());
					if (i+1 < currentClients.size()) {
						currentClientsString.append(", ");
					}
				}
				model.addAttribute("currentClients", currentClientsString.toString());
			} else {
				model.addAttribute("currentClients", "");
			}
			
			StringBuffer addedClients = new StringBuffer(); 		
			StringBuffer removedClients = new StringBuffer();
			if (undoClients == null && currentClients == null) {
				model.addAttribute("addedClients", "");
				model.addAttribute("removedClients", "");
			} else if (undoClients != null && currentClients == null) {
				model.addAttribute("addedClients", ""); 
				model.addAttribute("removedClients", undoClientsString.toString());
			} else if (undoClients == null && currentClients != null) {
				model.addAttribute("addedClients", currentClientsString.toString()); 
				model.addAttribute("removedClients", "");
			} else {
				String[] currentClientsStringList = currentClientsString.toString().split(", ");
				ArrayList<String> current = new ArrayList<String>();
				String[] undoClientsStringList  = undoClientsString.toString().split(", ");
				ArrayList<String> undo = new ArrayList<String>();
				
				for (int i = 0; i < currentClientsStringList.length; i++) {
					current.add(currentClientsStringList[i]);
				}
				
				for (int i = 0; i < undoClientsStringList.length; i++) {
					undo.add(undoClientsStringList[i]);
				}
				
				for (int i = 0; i < current.size(); i++) {
					if (!undo.contains(current.get(i))) {
						addedClients.append(current.get(i)).append(", ");
					}
				}
				String addedClientsString = "";
				if (addedClients.length() > 1) {
					addedClientsString = addedClients.substring(0, addedClients.length()-2);
				}
				
				for (int i = 0; i < undo.size(); i++) {
					if (!current.contains(undo.get(i))) {
						removedClients.append(undo.get(i)).append(", ");
					}
				}
				String removedClientsString = "";
				if (removedClients.length() > 1) {
					removedClientsString = removedClients.substring(0, removedClients.length()-2);
				}
				
				model.addAttribute("addedClients", addedClientsString);
				model.addAttribute("removedClients", removedClientsString);
				
			}
			
			List<UndoDTO> undoObligations = undoService.getUndoList(ts, "T_OBLIGATION_RELATION", op);
			StringBuffer undoObligationsString = new StringBuffer();
			String relation = "";
			Obligations obligationRelation = new Obligations();
			if (undoObligations != null && !op.equals("D")) {
				for (int i = 0; i < undoObligations.size(); i++) {
					if (undoObligations.get(i).getCol().equals("FK_RA_ID2")) {
						obligationRelation = obligationsService.findOblId((Integer.parseInt(undoObligations.get(i).getValue())));
						undoObligationsString.append(obligationRelation.getOblTitle());					
					} else if (undoObligations.get(i).getCol().equals("RELATION")) {
						relation = undoObligations.get(i).getValue();
					}
				}
				switch (relation) {
					case "replaces":
						undoObligationsString.append(" (").append("Replaces").append(")");
						break;
					case "is-replaced-by":
						undoObligationsString.append(" (").append("Is replaced by").append(")");
						break;
					case "see-also":
						undoObligationsString.append(" (").append("See also").append(")");
						break;
					case "same-as":
						undoObligationsString.append(" (").append("Same as").append(")");
						break;
					default: break;
				}
				model.addAttribute("undoObligations", undoObligationsString.toString());
			} else {
				model.addAttribute("undoObligations", "");
			}
			
			obligationRelation = obligationsService.findObligationRelation(id);
			StringBuffer currentObligationsString = new StringBuffer();
			if (obligationRelation.getRelObligationId() != 0) {
				currentObligationsString.append(obligationRelation.getOblRelationTitle());
				switch (obligationRelation.getOblRelationId()) {
					case "replaces":
						currentObligationsString.append(" (").append("Replaces").append(")");
						break;
					case "is-replaced-by":
						currentObligationsString.append(" (").append("Is replaced by").append(")");
						break;
					case "see-also":
						currentObligationsString.append(" (").append("See also").append(")");
						break;
					case "same-as":
						currentObligationsString.append(" (").append("Same as").append(")");
						break;
					default: break;
				}
				model.addAttribute("currentObligations", currentObligationsString.toString());
			} else {
				model.addAttribute("currentObligations", "");
			}
			
			if (undoObligationsString.toString().equals(currentObligationsString.toString())) {
				model.addAttribute("addedObligations", "");
				model.addAttribute("removedObligations", "");
			} else if (undoObligationsString.length() == 0 && currentObligationsString.length() > 0) {
				model.addAttribute("addedObligations", currentObligationsString.toString());
				model.addAttribute("removedObligations", "");
			} else if (undoObligationsString.length() > 0 && currentObligationsString.length() == 0) {
				model.addAttribute("addedObligations", "");
				model.addAttribute("removedObligations", undoObligationsString.toString());
			} else {
				model.addAttribute("addedObligations", currentObligationsString.toString());
				model.addAttribute("removedObligations", undoObligationsString.toString());
			}
			
			model.addAttribute("activeTab", "obligations");
			
		} else {
			
			if (!op.equals("D")) {
				
				boolean isDelete = undoService.isDelete(tab, "PK_SOURCE_ID", id);
				
				if (isDelete == false) {
			
					InstrumentFactsheetDTO instrument = sourceService.getById(id);
					
					currentValues.add(instrument.getSourceAbstract());
					currentValues.add(instrument.getSourceAlias());
					currentValues.add(instrument.getSourceCelexRef());
					currentValues.add(instrument.getSourceComment());
					currentValues.add(instrument.getSourceEcAccession());
					currentValues.add(instrument.getSourceEcEntryIntoForce());
					currentValues.add(instrument.getClientId().toString());
					currentValues.add(instrument.getSourceFkTypeId().toString());
					currentValues.add(instrument.getSourceIssuedBy());
					currentValues.add(instrument.getSourceIssuedByUrl());	
					currentValues.add(instrument.getSourceLastModified());
					currentValues.add(instrument.getSourceLastUpdate());
					currentValues.add(instrument.getSourceLegalName());
					currentValues.add(instrument.getSourceId().toString());
					currentValues.add(instrument.getSourceSecretariat());
					currentValues.add(instrument.getSourceSecretariatUrl());
					currentValues.add(instrument.getSourceCode());
					currentValues.add(instrument.getSourceTerminate());
					currentValues.add(instrument.getSourceTitle());
					currentValues.add(instrument.getSourceUrl());
					currentValues.add(instrument.getSourceValidFrom());
					
				} else {
					currentValues.add("");
					currentValues.add("");
					currentValues.add("");
					currentValues.add("");
					currentValues.add(null);
					currentValues.add(null);
					currentValues.add("");
					currentValues.add("");
					currentValues.add(null);
					currentValues.add("");	
					currentValues.add(null);
					currentValues.add(null);
					currentValues.add("");
					currentValues.add("");
					currentValues.add("");
					currentValues.add("");
					currentValues.add("");
					currentValues.add("");
					currentValues.add("");
					currentValues.add("");
					currentValues.add(null);
				}
				
			}
			
			model.addAttribute("activeTab", "instruments");
		}
		
		model.addAttribute("currentValues", currentValues);
		
		BreadCrumbs.set(model, "Previous Actions");		
		model.addAttribute("title","Previous Actions");
		
		return "undoinfo";
	}

}
