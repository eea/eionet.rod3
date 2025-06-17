package eionet.rod.controller;

import eionet.rod.dao.IssueDao;
import eionet.rod.model.*;
import eionet.rod.service.*;
import eionet.rod.util.BreadCrumbs;
import eionet.rod.util.RODUtil;
import eionet.rod.util.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

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
    IssueDao issueService;

    @Autowired
    ClientService clientService;

    @Autowired
    SpatialService spatialService;

    @Autowired
    SourceService sourceService;

    @RequestMapping({"", "/"})
    public String undoInfoHome(Model model, @RequestParam long ts, @RequestParam String tab, @RequestParam String op, @RequestParam Integer id, @RequestParam String user) {

        model.addAttribute("time", RODUtil.miliseconds2Date(ts));
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
            default:
                break;
        }

        List<UndoDTO> undoList = undoService.getUndoList(ts, tab, op);
        model.addAttribute("undoList", undoList);

        Map<String,String> currentValues = new LinkedHashMap<>();

        if ("T_OBLIGATION".equals(tab)) {

            if (!"D".equals(op)) {
                boolean isDelete = undoService.isDelete(tab, "PK_RA_ID", id);
                Obligations obligation = obligationsService.findOblId(id);

                currentValues.put("AUTHORITY", isDelete ? "" : obligation.getAuthority());
                currentValues.put("COMMENT", isDelete ? "" : obligation.getComment());
                currentValues.put("CONTINOUS_REPORTING", isDelete ? "" : obligation.getContinousReporting());
                currentValues.put("COORDINATOR", isDelete ? "" : obligation.getCoordinator());
                currentValues.put("COORDINATOR_ROLE", isDelete ? "" : obligation.getCoordinatorRole());
                currentValues.put("COORDINATOR_ROLE_SUF", isDelete ? "" : obligation.getCoordinatorRoleSuf());
                currentValues.put("COORDINATOR_URL", isDelete ? "" : obligation.getCoordinatorUrl());
                currentValues.put("DATA_USED_FOR", isDelete ? "" : obligation.getDataUsedFor());
                currentValues.put("DATA_USED_FOR_URL", isDelete ? "" : obligation.getDataUsedForUrl());
                currentValues.put("DATE_COMMENTS", isDelete ? "" : obligation.getDateComments());
                currentValues.put("DESCRIPTION", isDelete ? "" : obligation.getDescription());
                currentValues.put("EEA_CORE", isDelete ? "" : toStringOrEmpty(obligation.getEeaCore()));
                currentValues.put("EEA_PRIMARY", isDelete ? "" : toStringOrEmpty(obligation.getEeaPrimary()));
                currentValues.put("FIRST_REPORTING", isDelete ? "" : toStringOrEmpty(obligation.getFirstReporting()));
                currentValues.put("FK_DELIVERY_COUNTRY_IDS", isDelete ? "" : toStringOrEmpty(obligation.getDeliveryCountryId()));
                currentValues.put("FK_SOURCE_ID", isDelete ? "" : obligation.getSourceId());
                currentValues.put("FLAGGED", isDelete ? "" : toStringOrEmpty(obligation.getFlagged()));
                currentValues.put("FORMAT_NAME", isDelete ? "" : obligation.getFormatName());
                currentValues.put("LAST_HARVESTED", isDelete ? "" : toStringOrEmpty(obligation.getLastHarvested()));
                currentValues.put("LAST_UPDATE", isDelete ? "" : toStringOrEmpty(obligation.getLastUpdate()));
                currentValues.put("LOCATION_INFO", isDelete ? "" : obligation.getLocationInfo());
                currentValues.put("LOCATION_PTR", isDelete ? "" : obligation.getLocationPtr());
                currentValues.put("NATIONAL_CONTACT", isDelete ? "" : obligation.getNationalContact());
                currentValues.put("NATIONAL_CONTACT_URL", isDelete ? "" : obligation.getNationalContactUrl());
                currentValues.put("NEXT_DEADLINE", isDelete ? "" : toStringOrEmpty(obligation.getNextDeadline()));
                currentValues.put("NEXT_DEADLINE2", isDelete ? "" : toStringOrEmpty(obligation.getNextDeadline2()));
                currentValues.put("NEXT_REPORTING", isDelete ? "" : obligation.getNextReporting());
                currentValues.put("PK_RA_ID", isDelete ? "" : obligation.getObligationId().toString());
                currentValues.put("REPORTING_FORMAT", isDelete ? "" : obligation.getReportingFormat());
                currentValues.put("REPORT_FORMAT_URL", isDelete ? "" : obligation.getReportFormatUrl());
                currentValues.put("REPORT_FREQ", isDelete ? "" : obligation.getReportFreq());
                currentValues.put("REPORT_FREQ_DETAIL", isDelete ? "" : toStringOrEmpty(obligation.getReportFreqDetail()));
                currentValues.put("REPORT_FREQ_MONTHS", isDelete ? "" : obligation.getReportFreqMonths());
                currentValues.put("RESPONSIBLE_ROLE", isDelete ? "" : obligation.getResponsibleRole());
                currentValues.put("RESPONSIBLE_ROLE_SUF", isDelete ? "" : obligation.getResponsibleRoleSuf());
                currentValues.put("TERMINATE", isDelete ? "" : obligation.getTerminate());
                currentValues.put("TITLE", isDelete ? "" : obligation.getOblTitle());
                currentValues.put("VALID_SINCE", isDelete ? "" : toStringOrEmpty(obligation.getValidSince()));
                currentValues.put("VALID_TO", isDelete ? "" : toStringOrEmpty(obligation.getValidTo()));
            }

            List<UndoDTO> undoCountries = undoService.getUndoList(ts, "T_RASPATIAL_LNK", op);
            HashMap<Integer, String> fkSpatialIds = new HashMap<>();
            HashMap<Integer, String> voluntaries = new HashMap<>();
            StringBuilder undoCountriesString = new StringBuilder();
            StringBuilder undoCountriesVolString = new StringBuilder();
            if (undoCountries != null && !"D".equals(op)) {
                for (UndoDTO undoCountry : undoCountries) {
                    if ("FK_SPATIAL_ID".equals(undoCountry.getCol())) {
                        fkSpatialIds.put(undoCountry.getSubTransNr(), undoCountry.getValue());
                    }
                    if ("VOLUNTARY".equals(undoCountry.getCol())) {
                        voluntaries.put(undoCountry.getSubTransNr(), undoCountry.getValue());
                    }
                }

                Spatial country = null;

                for (int i = 0; i < fkSpatialIds.size(); i++) {
                    country = spatialService.findOne(Integer.parseInt(fkSpatialIds.get(i)));
                    if ("Y".equals(voluntaries.get(i))) {
                        undoCountriesVolString.append(country.getName()).append(", ");
                    } else {
                        undoCountriesString.append(country.getName()).append(", ");
                    }
                }
                if (undoCountriesString.length() > 1) {
                    undoCountriesString.replace(undoCountriesString.length() - 2, undoCountriesString.length(), "");
                }
                if (undoCountriesVolString.length() > 1) {
                    undoCountriesVolString.replace(undoCountriesVolString.length() - 2, undoCountriesVolString.length(), "");
                }
                model.addAttribute("undoCountries", undoCountriesString.toString());
                model.addAttribute("undoCountriesVol", undoCountriesVolString.toString());
            } else {
                model.addAttribute("undoCountries", "");
                model.addAttribute("undoCountriesVol", "");
            }

            List<Spatial> currentCountries = obligationsService.findAllCountriesByObligation(id, "N");
            StringBuilder currentCountriesString = new StringBuilder();
            if (currentCountries != null) {
                for (int i = 0; i < currentCountries.size(); i++) {
                    currentCountriesString.append(currentCountries.get(i).getName());
                    if (i + 1 < currentCountries.size()) {
                        currentCountriesString.append(", ");
                    }
                }
                model.addAttribute("currentCountries", currentCountriesString.toString());
            } else {
                model.addAttribute("currentCountries", "");
            }

            List<Spatial> currentCountriesVol = obligationsService.findAllCountriesByObligation(id, "Y");
            StringBuilder currentCountriesVolString = new StringBuilder();
            if (currentCountriesVol != null) {
                for (int i = 0; i < currentCountriesVol.size(); i++) {
                    currentCountriesVolString.append(currentCountriesVol.get(i).getName());
                    if (i + 1 < currentCountriesVol.size()) {
                        currentCountriesVolString.append(", ");
                    }
                }
                model.addAttribute("currentCountriesVol", currentCountriesVolString.toString());
            } else {
                model.addAttribute("currentCountriesVol", "");
            }

            StringBuilder addedCountries = new StringBuilder();
            StringBuilder removedCountries = new StringBuilder();
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
                ArrayList<String> current = new ArrayList<>();
                String[] undoCountriesStringList = undoCountriesString.toString().split(", ");

                current.addAll(Arrays.asList(currentCountriesStringList));

                ArrayList<String> undo = new ArrayList<>(Arrays.asList(undoCountriesStringList));

                for (String s1 : undo) {
                    if (!current.contains(s1)) {
                        removedCountries.append(s1).append(", ");
                    }
                }
                String removedCountriesString = "";
                if (removedCountries.length() > 1) {
                    removedCountriesString = removedCountries.substring(0, removedCountries.length() - 2);
                }

                for (String s : current) {
                    if (!undo.contains(s)) {
                        addedCountries.append(s).append(", ");
                    }
                }
                String addedCountriesString = "";
                if (addedCountries.length() > 1) {
                    addedCountriesString = addedCountries.substring(0, addedCountries.length() - 2);
                }

                model.addAttribute("addedCountries", addedCountriesString);
                model.addAttribute("removedCountries", removedCountriesString);

            }

            StringBuilder addedCountriesVol = new StringBuilder();
            StringBuilder removedCountriesVol = new StringBuilder();
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
                String[] undoCountriesVolStringList = undoCountriesVolString.toString().split(", ");

                ArrayList<String> current = new ArrayList<>(Arrays.asList(currentCountriesVolStringList));

                ArrayList<String> undo = new ArrayList<>(Arrays.asList(undoCountriesVolStringList));

                for (String s1 : undo) {
                    if (!current.contains(s1)) {
                        removedCountriesVol.append(s1).append(", ");
                    }
                }
                String removedCountriesVolString = "";
                if (removedCountriesVol.length() > 1) {
                    removedCountriesVolString = removedCountriesVol.substring(0, removedCountriesVol.length() - 2);
                }

                for (String s : current) {
                    if (!undo.contains(s)) {
                        addedCountriesVol.append(s).append(", ");
                    }
                }
                String addedCountriesVolString = "";
                if (addedCountriesVol.length() > 1) {
                    addedCountriesVolString = addedCountriesVol.substring(0, addedCountriesVol.length() - 2);
                }

                model.addAttribute("addedCountriesVol", addedCountriesVolString);
                model.addAttribute("removedCountriesVol", removedCountriesVolString);

            }

            List<UndoDTO> undoIssues = undoService.getUndoList(ts, "T_RAISSUE_LNK", op);
            //ArrayList<Issue> undoIssues = new ArrayList<Issue>();
            StringBuilder undoIssuesString = new StringBuilder();
            if (undoIssues != null && !"D".equals(op)) {
                for (UndoDTO undoIssue : undoIssues) {
                    if ("FK_ISSUE_ID".equals(undoIssue.getCol())) {
                        Issue issue = issueService.findById(Integer.parseInt(undoIssue.getValue()));
                        //undoIssues.add(issue);
                        undoIssuesString.append(issue.getIssueName());
                        undoIssuesString.append(", ");
                    }

                }
                if (undoIssuesString.length() > 1) {
                    undoIssuesString.replace(undoIssuesString.length() - 2, undoIssuesString.length(), "");
                }
                model.addAttribute("undoIssues", undoIssuesString.toString());
            } else {
                model.addAttribute("undoIssues", "");
            }

            List<Issue> currentIssues = obligationsService.findAllIssuesbyObligation(id);
            StringBuilder currentIssuesString = new StringBuilder();
            if (currentIssues != null) {
                for (int i = 0; i < currentIssues.size(); i++) {
                    currentIssuesString.append(currentIssues.get(i).getIssueName());
                    if (i + 1 < currentIssues.size()) {
                        currentIssuesString.append(", ");
                    }
                }
                model.addAttribute("currentIssues", currentIssuesString.toString());
            } else {
                model.addAttribute("currentIssues", "");
            }

            StringBuilder addedIssues = new StringBuilder();
            StringBuilder removedIssues = new StringBuilder();
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
                String[] undoIssuesStringList = undoIssuesString.toString().split(", ");

                ArrayList<String> current = new ArrayList<>(Arrays.asList(currentIssuesStringList));

                ArrayList<String> undo = new ArrayList<>(Arrays.asList(undoIssuesStringList));

                for (String s1 : undo) {
                    if (!current.contains(s1)) {
                        removedIssues.append(s1).append(", ");
                    }
                }
                String removedIssuesString = "";
                if (removedIssues.length() > 1) {
                    removedIssuesString = removedIssues.substring(0, removedIssues.length() - 2);
                }

                for (String s : current) {
                    if (!undo.contains(s)) {
                        addedIssues.append(s).append(", ");
                    }
                }
                String addedIssuesString = "";
                if (addedIssues.length() > 1) {
                    addedIssuesString = addedIssues.substring(0, addedIssues.length() - 2);
                }

                model.addAttribute("addedIssues", addedIssuesString);
                model.addAttribute("removedIssues", removedIssuesString);
            }

            List<UndoDTO> undoClients = undoService.getUndoList(ts, "T_CLIENT_OBLIGATION_LNK", op);
            StringBuilder undoClientsString = new StringBuilder();
            if (undoClients != null && !"D".equals(op)) {
                for (UndoDTO undoClient : undoClients) {
                    if ("FK_CLIENT_ID".equals(undoClient.getCol())) {
                        ClientDTO client = clientService.getById((Integer.parseInt(undoClient.getValue())));
                        undoClientsString.append(client.getName());
                        undoClientsString.append(", ");
                    }

                }
                if (undoClientsString.length() > 1) {
                    undoClientsString.replace(undoClientsString.length() - 2, undoClientsString.length(), "");
                }
                model.addAttribute("undoClients", undoClientsString.toString());
            } else {
                model.addAttribute("undoClients", "");
            }

            List<ClientDTO> currentClients = obligationsService.findAllClientsByObligation(id);
            StringBuilder currentClientsString = new StringBuilder();
            if (currentClients != null) {
                for (int i = 0; i < currentClients.size(); i++) {
                    currentClientsString.append(currentClients.get(i).getName());
                    if (i + 1 < currentClients.size()) {
                        currentClientsString.append(", ");
                    }
                }
                model.addAttribute("currentClients", currentClientsString.toString());
            } else {
                model.addAttribute("currentClients", "");
            }

            StringBuilder addedClients = new StringBuilder();
            StringBuilder removedClients = new StringBuilder();
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
                String[] undoClientsStringList = undoClientsString.toString().split(", ");

                ArrayList<String> current = new ArrayList<>(Arrays.asList(currentClientsStringList));

                ArrayList<String> undo = new ArrayList<>(Arrays.asList(undoClientsStringList));

                for (String s1 : current) {
                    if (!undo.contains(s1)) {
                        addedClients.append(s1).append(", ");
                    }
                }
                String addedClientsString = "";
                if (addedClients.length() > 1) {
                    addedClientsString = addedClients.substring(0, addedClients.length() - 2);
                }

                for (String s : undo) {
                    if (!current.contains(s)) {
                        removedClients.append(s).append(", ");
                    }
                }
                String removedClientsString = "";
                if (removedClients.length() > 1) {
                    removedClientsString = removedClients.substring(0, removedClients.length() - 2);
                }

                model.addAttribute("addedClients", addedClientsString);
                model.addAttribute("removedClients", removedClientsString);

            }

            List<UndoDTO> undoObligations = undoService.getUndoList(ts, "T_OBLIGATION_RELATION", op);
            StringBuilder undoObligationsString = new StringBuilder();
            String relation = "";
            Obligations obligationRelation;
            if (undoObligations != null && !"D".equals(op)) {
                for (UndoDTO undoObligation : undoObligations) {
                    if ("FK_RA_ID2".equals(undoObligation.getCol())) {
                        obligationRelation = obligationsService.findOblId((Integer.parseInt(undoObligation.getValue())));
                        undoObligationsString.append(obligationRelation.getOblTitle());
                    } else if ("RELATION".equals(undoObligation.getCol())) {
                        relation = undoObligation.getValue();
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
                    default:
                        break;
                }
                model.addAttribute("undoObligations", undoObligationsString.toString());
            } else {
                model.addAttribute("undoObligations", "");
            }

            obligationRelation = obligationsService.findObligationRelation(id);
            StringBuilder currentObligationsString = new StringBuilder();
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
                    default:
                        break;
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
            if (!"D".equals(op)) {
                boolean isDelete = undoService.isDelete(tab, "PK_SOURCE_ID", id);
                InstrumentFactsheetDTO instrument = sourceService.getById(id);

                currentValues.put("ABSTRACT", isDelete ? "" : instrument.getSourceAbstract());
                currentValues.put("ALIAS", isDelete ? "" : instrument.getSourceAlias());
                currentValues.put("CELEX_REF", isDelete ? "" : instrument.getSourceCelexRef());
                currentValues.put("COMMENT", isDelete ? "" : instrument.getSourceComment());
                currentValues.put("EC_ACCESSION", isDelete ? "" : toStringOrEmpty(instrument.getSourceEcAccession()));
                currentValues.put("EC_ENTRY_INTO_FORCE", isDelete ? "" : toStringOrEmpty(instrument.getSourceEcEntryIntoForce()));
                currentValues.put("FK_CLIENT_ID", isDelete ? "" : instrument.getClientId().toString());
                currentValues.put("FK_TYPE_ID", isDelete ? "" : toStringOrEmpty(instrument.getSourceFkTypeId()));
                currentValues.put("ISSUED_BY", isDelete ? "" : toStringOrEmpty(instrument.getSourceIssuedBy()));
                currentValues.put("LAST_MODIFIED", isDelete ? "" : toStringOrEmpty(instrument.getSourceLastModified()));
                // currentValues.put("ISSUED_BY_URL", isDelete ? "" : instrument.getSourceIssuedByUrl());
                currentValues.put("LAST_UPDATE", isDelete ? "" : toStringOrEmpty(instrument.getSourceLastUpdate()));
                currentValues.put("LEGAL_NAME", isDelete ? "" : instrument.getSourceLegalName());
                currentValues.put("PK_SOURCE_ID", isDelete ? "" : instrument.getSourceId().toString());
                currentValues.put("SECRETARIAT", isDelete ? "" : instrument.getSourceSecretariat());
                currentValues.put("SECRETARIAT_URL", isDelete ? "" : instrument.getSourceSecretariatUrl());
                currentValues.put("SOURCE_CODE", isDelete ? "" : instrument.getSourceCode());
                currentValues.put("TERMINATE", isDelete ? "" : instrument.getSourceTerminate());
                currentValues.put("TITLE", isDelete ? "" : instrument.getSourceTitle());
                currentValues.put("URL", isDelete ? "" : instrument.getSourceUrl());
                currentValues.put("VALID_FROM", isDelete ? "" : toStringOrEmpty(instrument.getSourceValidFrom()));
            }

            model.addAttribute("activeTab", "instruments");
        }

        model.addAttribute("currentValues", currentValues);

        BreadCrumbs.set(model, "Previous Actions");
        model.addAttribute("title", "Previous Actions");

        return "undoinfo";
    }

    /**
     * Returns the toString() of the object or empty string if the object is null
     *
     * @param o The object
     * @return toString() or ""
     */
    private final String toStringOrEmpty(Object o) {
        return null == o ? "" : o.toString();
    }

}
