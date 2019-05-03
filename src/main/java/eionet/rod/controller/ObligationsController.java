package eionet.rod.controller;

import eionet.rod.Attrs;
import eionet.rod.IAuthenticationFacade;
import eionet.rod.UNSEventSender;
import eionet.rod.dao.ClientService;
import eionet.rod.dao.IssueDao;
import eionet.rod.dao.UndoService;
import eionet.rod.model.*;
import eionet.rod.service.DeliveryService;
import eionet.rod.service.FileServiceIF;
import eionet.rod.service.ObligationService;
import eionet.rod.service.SpatialService;
import eionet.rod.util.BreadCrumbs;
import eionet.rod.util.RODServices;
import eionet.rod.util.RODUtil;
import eionet.rod.util.exception.ResourceNotFoundException;
import eionet.rod.util.exception.ServiceException;
import eionet.sparqlClient.helpers.QueryExecutor;
import eionet.sparqlClient.helpers.QueryResult;
import eionet.sparqlClient.helpers.ResultValue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.*;

//import org.springframework.validation.FieldError;


/**
 * Obligation managing controller.
 */
@Controller
@RequestMapping("/obligations")
public class ObligationsController {

    private static BreadCrumb obligationCrumb = new BreadCrumb("/obligations", "Obligations");

    private static final Log logger = LogFactory.getLog(ObligationsController.class);

    /**
     * Page of error
     *
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

    private QueryResult result;

    FileServiceIF fileSrv;

    /**
     * View for all obligations.
     *
     * @param model   - contains attributes for the view
     * @param message
     * @return view name
     */
    @RequestMapping({"", "/", "/view"})
    public String viewObligations(Model model, @RequestParam(required = false) String message, @RequestParam(required = false) String anmode) {
        BreadCrumbs.set(model, "Reporting obligations");

        Obligations obligation = new Obligations();
        String issueID = "0";
        boolean deliveries = false;
        String deliveryColumn = "0";
        if (!RODUtil.isNullOrEmpty(anmode)) {
            model.addAttribute("anmode", anmode);
            switch (anmode) {
                case "NI":
                    issueID = anmode;
                    obligation.setIssueId(issueID);
                    model.addAttribute("activeTab", "obligations");
                    model.addAttribute("titleObl", "Reporting obligations");
                    break;
                case "P":
                    model.addAttribute("activeTab", "CoreData");
                    model.addAttribute("titleObl", "Reporting obligations : Eionet core data flows");
                    deliveryColumn = "1";
                    break;
                case "F":
                    model.addAttribute("activeTab", "EEAData");
                    model.addAttribute("titleObl", "Reporting obligations : Delivery process is managed by EEA");
                    break;
                case "C":
                    model.addAttribute("activeTab", "obligations");
                    model.addAttribute("titleObl", "Reporting obligations : EEA Core set of indicators");
                    break;
                default:
                    model.addAttribute("activeTab", "obligations");
                    model.addAttribute("titleObl", "Reporting obligations");
                    break;
            }
        } else {
            model.addAttribute("activeTab", "obligations");
            model.addAttribute("titleObl", "Reporting obligations");
        }

        model.addAttribute("allObligations", obligationsService.findObligationList("0", issueID, "0", "N", "0", anmode, null, null, deliveries));

        model.addAttribute("title", "Reporting obligations");

        model.addAttribute("deliveryColumn", deliveryColumn);

        //Environmental issues
        List<Issue> issues = issueDao.findAllIssuesList();
        model.addAttribute("allIssues", issues);

        //Countries/territories
        List<Spatial> countries = spatialService.findAll();
        model.addAttribute("allCountries", countries);

        //Countries/territories
        List<ClientDTO> clients = clientService.getAllClients();
        model.addAttribute("allClients", clients);


        model.addAttribute("obligation", obligation);

        if (message != null) model.addAttribute("message", message);
        return "obligations";
    }

    /**
     * @param model
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String deleteObligations(Obligations obligations, Model model) {
        if (obligations.getDelObligations() != null) {
            String[] listObligations = obligations.getDelObligations().split(",");
            Authentication authentication = authenticationFacade.getAuthentication();
            long ts = System.currentTimeMillis();
            for (String listObligation : listObligations) {
                processEditDelete("D", authentication.getName(), Integer.parseInt(listObligation), ts);
            }
            obligationsService.deleteObligations(obligations.getDelObligations());
        }
        model.addAttribute("message", "Obligations selected deleted.");
        return "redirect:view";
    }

    /**
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
     * @param obligations
     * @param model
     * @return view obligations
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public String searchObligation(Obligations obligations, Model model) {
        BreadCrumbs.set(model, "Reporting obligations");
        String terminate = obligations.getTerminate();
        if (RODUtil.isNullOrEmpty(obligations.getTerminate())) {
            terminate = "N";
        }
        boolean deliveries = false;
        String deliveryColumn = "0";
        if (!RODUtil.isNullOrEmpty(obligations.getAnmode())) {
            model.addAttribute("anmode", obligations.getAnmode());
            switch (obligations.getAnmode()) {
                case "NI":
                    obligations.setIssueId(obligations.getAnmode());
                    model.addAttribute("activeTab", "obligations");
                    model.addAttribute("titleObl", "Reporting obligations");
                    break;
                case "P":
                    model.addAttribute("activeTab", "CoreData");
                    model.addAttribute("titleObl", "Reporting obligations : Eionet core data flows");
                    deliveryColumn = "1";
                    break;
                case "F":
                    model.addAttribute("activeTab", "EEAData");
                    model.addAttribute("titleObl", "Reporting obligations : Delivery process is managed by EEA");
                    break;
                case "C":
                    model.addAttribute("activeTab", "obligations");
                    model.addAttribute("titleObl", "Reporting obligations : EEA Core set of indicators");
                    break;
                default:
                    model.addAttribute("activeTab", "obligations");
                    model.addAttribute("titleObl", "Reporting obligations");
                    break;
            }
        } else {
            obligations.setAnmode(null);
            model.addAttribute("activeTab", "obligations");
            model.addAttribute("titleObl", "Reporting obligations");
        }

        model.addAttribute("allObligations", obligationsService.findObligationList(obligations.getClientId(), obligations.getIssueId(), obligations.getSpatialId(), terminate, "0", obligations.getAnmode(), null, null, deliveries));

        model.addAttribute("title", "Reporting obligations");

        model.addAttribute("deliveryColumn", deliveryColumn);

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

        model.addAttribute("obligation", obligations);

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
        model.addAttribute("title", RODUtil.replaceTags(obligation.getOblTitle()));
        if (obligation.getReportFreqMonths() == null)
            obligation.setReportFreqMonths("");

        obligation.setOblTitle(RODUtil.replaceTags(obligation.getOblTitle()));

        obligation.setDescription(RODUtil.replaceTags(obligation.getDescription()));

        obligation.setReportingFormat(RODUtil.replaceTags(obligation.getReportingFormat()));

        obligation.setCoordinator(RODUtil.replaceTags(obligation.getCoordinator()));
        obligation.setCoordinatorRole(RODUtil.replaceTags(obligation.getCoordinatorRole()));
        obligation.setCoordRoleId(RODUtil.replaceTags(obligation.getCoordRoleId()));
        obligation.setCoordRoleName(RODUtil.replaceTags(obligation.getCoordRoleName()));
        obligation.setCoordinatorUrl(RODUtil.replaceTags(obligation.getCoordinatorUrl(), true, true));

        obligation.setNationalContactUrl(RODUtil.replaceTags(obligation.getNationalContactUrl(), true, true));
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
        List<ClientDTO> clients = clientService.findOblClients(obligationId, status);

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
        model.addAttribute("title", RODUtil.replaceTags(obligation.getOblTitle()));

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
        List<ObligationCountry> obligationCountries = spatialService.findObligationCountriesList(obligationId);
        model.addAttribute("ObligationCountries", obligationCountries);

        model.addAttribute("activeTab", "obligations");

        return "obligation_legislation";
    }

    @RequestMapping(value = "/{obligationId}/products")
    public String obligation_products(@PathVariable("obligationId") Integer obligationId, final Model model) throws Exception {
        Obligations obligation = obligationsService.findOblId(obligationId);
        BreadCrumbs.set(model, obligationCrumb, new BreadCrumb(obligation.getOblTitle()));
        model.addAttribute("obligation", obligation);
        model.addAttribute("title", RODUtil.replaceTags(obligation.getOblTitle()));

        model.addAttribute("activeTab", "home");

        String query = "PREFIX data: <http://www.eea.europa.eu/portal_types/Data#> "
                + "PREFIX dct: <http://purl.org/dc/terms/> "
                + "SELECT DISTINCT ?product ?title xsd:date(?effective) as ?published WHERE { "
                + "?product data:reportingObligations \"" + obligationId + "\" ; "
                + "dct:title ?title ; "
                + "dct:issued ?effective "
                + "} ORDER BY DESC(?published) ";

        fileSrv = RODServices.getFileService();
        String endpointURL = fileSrv.getStringProperty(FileServiceIF.CR_SPARQL_ENDPOINT);
        //String CRSparqlEndpoint = "http://cr.eionet.europa.eu/sparql";
        QueryExecutor executor = new QueryExecutor();
        executor.executeQuery(endpointURL, query);
        result = executor.getResults();
        ArrayList<ArrayList<String>> lists = removeDuplicates();
        model.addAttribute("listItem", lists);
        //model.addAttribute("expression", expression);

        model.addAttribute("activeTab", "obligations");
        return "obligation_products";
    }

    private ArrayList<ArrayList<String>> removeDuplicates() {
        List<String> existingSubjects = new ArrayList<>();
        ArrayList<ArrayList<String>> lists = new ArrayList<>();
        if (result != null && result.getRows() != null) {
            ArrayList<HashMap<String, ResultValue>> rows = result.getRows();
            for (HashMap<String, ResultValue> row : rows) {
                String product;
                if (row.get("product") != null) {
                    product = row.get("product").toString();
                } else {
                    product = null;
                }
                String title;
                if (row.get("title") != null) {
                    title = row.get("title").toString();
                } else {
                    title = null;
                }
                String published;
                if (row.get("published") != null) {
                    published = row.get("published").toString();
                } else {
                    published = null;
                }
                ArrayList<String> list = new ArrayList<>();
                list.add(product);
                list.add(title);
                list.add(published);
                if (product != null && !existingSubjects.contains(product)) {
                    existingSubjects.add(product);
                    lists.add(list);
                }
            }
        }
        return lists;
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
        model.addAttribute("title", RODUtil.replaceTags(obligation.getOblTitle()));

        List<Delivery> deliveries = deliveryService.getAllDelivery(obligationId.toString(), null);
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
        model.addAttribute("title", RODUtil.replaceTags(obligation.getOblTitle()));

        List<UndoDTO> versions = undoService.getPreviousActionsReportSpecific(obligationId, "T_OBLIGATION", "PK_RA_ID", "U");
        if (versions != null) {
            for (UndoDTO version : versions) {
                version.setDate(RODUtil.miliseconds2Date(version.getUndoTime()));
            }
        }
        model.addAttribute("versions", versions);

        model.addAttribute("activeTab", "obligations");

        return "obligation_history";
    }

    /**
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

        //init var ListBox
        List<String> selectedClients = new ArrayList<>();
        obligations.setSelectedClients(selectedClients);
        List<String> selectedVoluntaryCountries = new ArrayList<>();
        obligations.setSelectedVoluntaryCountries(selectedVoluntaryCountries);
        List<String> selectedFormalCountries = new ArrayList<>();
        obligations.setSelectedFormalCountries(selectedFormalCountries);
        List<String> selectedIssues = new ArrayList<>();
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
        model.addAttribute("obligationCountries", allObligationCountriesEdit);

        //list of formal countries by obligationId with voluntary = N
        List<Spatial> allObligationVoluntaryCountriesEdit = obligationsService.findAllCountriesByObligation(obligationId, "Y");
        model.addAttribute("obligationVoluntaryCountries", allObligationVoluntaryCountriesEdit);

        //list of issues seleted by obligationID
        List<Issue> allIssuesObligationEdit = obligationsService.findAllIssuesbyObligation(obligationId);
        model.addAttribute("obligationIssues", allIssuesObligationEdit);

        //all obligations except the edited.
        List<Obligations> relObligations = obligationsService.findAll();
        model.addAttribute("relObligations", relObligations);

        model.addAttribute("title", "Edit Reporting Obligation for");

        model.addAttribute("id", "edit");

        return "eobligation";
    }


    /**
     * obligations details by ID (overview)
     */
    @RequestMapping(value = "/add/{sourceId}")
    public String obligation_add(final Model model, @PathVariable("sourceId") String sourceId) {
        BreadCrumbs.set(model, obligationCrumb, new BreadCrumb("Add obligation"));
        model.addAttribute("activeTab", "obligations");
        model.addAttribute("title", "Edit Reporting Obligation for");

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

        model.addAttribute("id", "add");

        return "eobligation";
    }

    /**
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

        model.addAttribute("title", "Edit Reporting Obligation for");


        if (bindingResult.hasErrors()) {

            model.addAttribute("obligation", obligations);
            model.addAttribute("id", "add");

        } else {
            //insertamos y reenviamos a redirect:edit:id

            Integer obligationID = obligationsService.insertObligation(obligations, allObligationClients, allObligationCountries, allObligationVoluntaryCountries, allSelectedIssues);
            obligations.setObligationId(obligationID);
            model.addAttribute("obligationId", obligationID);

            model.addAttribute("obligation", obligations);

            model.addAttribute("id", "edit");

            model.addAttribute("message", "Obligation " + obligations.getOblTitle() + " added.");

            long ts = System.currentTimeMillis();

            try {
                sendEvent(false, obligations, obligationID, ts);
            } catch (ServiceException e) {

            }
        }

        return "eobligation";

    }

    /**
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
        model.addAttribute("title", "Edit Reporting Obligation for");

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

        model.addAttribute("obligation", obligations);

        model.addAttribute("id", "edit");
        model.addAttribute("message", "Obligation " + obligations.getOblTitle() + " updated.");

        try {
            sendEvent(true, obligations, obligations.getObligationId(), ts);
        } catch (ServiceException e) {
            logger.debug(e, e);
        }
        return "eobligation";
    }

    /**
     * @param selectedFormalCountries
     * @return List<Spatial> allObligationCountries
     */
    private List<Spatial> formalObligationCountriesSelected(List<String> selectedFormalCountries) {
        Spatial obligationCountry;
        List<Spatial> allObligationCountries = new ArrayList<>();
        if (selectedFormalCountries != null) {
            for (String selectedFormalCountry : selectedFormalCountries) {
                obligationCountry = spatialService.findOne(Integer.parseInt(selectedFormalCountry));
                allObligationCountries.add(obligationCountry);
            }
        }
        return allObligationCountries;
    }

    /**
     * @param selectedclients
     * @return List<ClientDTO> allObligationClients
     */
    private List<ClientDTO> formalObligationClientsSelected(List<String> selectedclients) {
        ClientDTO obligationClient;
        List<ClientDTO> allObligationClients = new ArrayList<>();
        if (selectedclients != null) {
            for (String selectedclient : selectedclients) {
                obligationClient = clientService.getById(Integer.parseInt(selectedclient));
                allObligationClients.add(obligationClient);

            }
        }

        return allObligationClients;
    }

    /**
     * @param selectedVoluntaryCountries
     * @return List<Spatial> allObligationVoluntaryCountries
     */
    private List<Spatial> voluntaryObligationCountriesSelected(List<String> selectedVoluntaryCountries) {
        Spatial obligationCountry;
        List<Spatial> allObligationVoluntaryCountries = new ArrayList<>();
        if (selectedVoluntaryCountries != null) {
            for (String selectedVoluntaryCountry : selectedVoluntaryCountries) {
                obligationCountry = spatialService.findOne(Integer.parseInt(selectedVoluntaryCountry));
                allObligationVoluntaryCountries.add(obligationCountry);
            }
        }
        return allObligationVoluntaryCountries;
    }

    /**
     * @param selectedIssues
     * @return List<Issue> allSelectedIssues
     */
    private List<Issue> obligationIssuesSelected(List<String> selectedIssues) {
        Issue selectedIssue;
        List<Issue> allSelectedIssues = new ArrayList<>();
        if (selectedIssues != null) {
            for (String selectedIssue1 : selectedIssues) {
                selectedIssue = issueDao.findById(Integer.parseInt(selectedIssue1));
                allSelectedIssues.add(selectedIssue);
            }
        }
        return allSelectedIssues;
    }

    private void processEditDelete(String state, String userName, Integer obligationId, long ts) {

        if ("U".equals(state)) {
            undoService.insertIntoUndo(obligationId, "U", "T_OBLIGATION", "PK_RA_ID", ts, "", "y");
        }

        String url = "obligations/" + obligationId;
        undoService.insertIntoUndo(ts, "T_OBLIGATION", "REDIRECT_URL", "L", "y", "n", url, 0, "n");
        undoService.insertIntoUndo(ts, "T_OBLIGATION", "A_USER", "K", "y", "n", userName, 0, "n");
        undoService.insertIntoUndo(ts, "T_OBLIGATION", "TYPE", "T", "y", "n", "A", 0, "n");

        if ("D".equals(state)) {
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

        if ("D".equals(op)) {
            //FALTAN LOS ACLS
            undoService.insertIntoUndo(obligationId, "D", "T_OBLIGATION", "PK_RA_ID", ts, "", show);
        }

        undoService.insertIntoUndo(obligationId, op, "T_CLIENT_OBLIGATION_LNK", "FK_RA_ID", ts, "", show);

    }

    private String getUserName() {
        Authentication authentication = authenticationFacade.getAuthentication();

        return authentication.getName();
    }

    private Vector<String> getChanges(Integer obligationID, long ts) throws ServiceException {
        Vector<String> resVec = new Vector<>();
        List<UndoDTO> undoList = undoService.getUndoInformation(ts, "U", "T_OBLIGATION");
        Obligations obligation = obligationsService.findOblId(obligationID);
        String value = "";

        for (UndoDTO undo : undoList) {
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
                    value = (obligation.getFirstReporting() != null) ? obligation.getFirstReporting().toString() : null;
                    break;
                case "VALID_TO":
                    value = (obligation.getValidTo() != null) ? obligation.getValidTo().toString() : null;
                    break;
                case "REPORT_FREQ_MONTHS":
                    value = obligation.getReportFreqMonths();
                    break;
                case "NEXT_DEADLINE":
                    value = (obligation.getNextDeadline() != null) ? obligation.getNextDeadline().toString() : null;
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
                    value = (obligation.getValidSince() != null) ? obligation.getValidSince().toString() : null;
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
                    value = (obligation.getNextDeadline2() != null) ? obligation.getNextDeadline2().toString() : null;
                    break;
                case "LAST_HARVESTED":
                    value = (obligation.getLastHarvested() != null) ? obligation.getLastHarvested().toString() : null;
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
                    if ("".equals(value.trim())) {
                        value = null;
                    }
                }

                if (undoValue != null) {
                    if ("".equals(undoValue.trim())) {
                        undoValue = null;
                    }
                }
                boolean diff = (value != null && value.equals(undoValue)) || (value == null && undoValue == null);
                if (!diff) {
                    String label = getLabel(undo.getCol(), undoValue, value);
                    resVec.add(label);
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
        List<String> undoVoluntaryCountries = new ArrayList<>();
        List<String> undoFormallyCountries = new ArrayList<>();
        List<String> currentVoluntaryCountries = new ArrayList<>();
        List<String> currentFormallyCountries = new ArrayList<>();
        List<String> voluntary = new ArrayList<>();

        if (undoList != null) {
            for (UndoDTO undoDTO : undoList) {
                if ("VOLUNTARY".equals(undoDTO.getCol())) {
                    voluntary.add(undoDTO.getValue());
                }
            }
        }

        int countVoluntary = 0;
        if (undoList != null) {
            for (UndoDTO undoDTO : undoList) {
                if ("FK_SPATIAL_ID".equals(undoDTO.getCol())) {
                    country = spatialService.findOne(Integer.parseInt(undoDTO.getValue()));
                    if ("N".equals(voluntary.get(countVoluntary))) {
                        undoFormallyCountries.add(country.getName());
                    } else {
                        undoVoluntaryCountries.add(country.getName());
                    }
                    countVoluntary++;
                }

            }
        }

        if (formallyCountries != null) {
            for (Spatial formallyCountry : formallyCountries) {
                currentFormallyCountries.add(formallyCountry.getName());
            }
        }

        if (voluntaryCountries != null) {
            for (Spatial voluntaryCountry : voluntaryCountries) {
                currentVoluntaryCountries.add(voluntaryCountry.getName());
            }
        }

        if (!currentFormallyCountries.isEmpty()) {
            for (String currentFormallyCountry : currentFormallyCountries) {
                if (!undoFormallyCountries.contains(currentFormallyCountry)) {
                    addedFormallyCountries.append(currentFormallyCountry);
                    addedFormallyCountries.append(", ");
                }
            }
            if (addedFormallyCountries.length() > 0) {
                addedFormallyCountries = addedFormallyCountries.replace(addedFormallyCountries.length() - 2, addedFormallyCountries.length(), "");
            }

        }

        if (!undoFormallyCountries.isEmpty()) {
            for (String undoFormallyCountry : undoFormallyCountries) {
                if (!currentFormallyCountries.contains(undoFormallyCountry)) {
                    removedFormallyCountries.append(undoFormallyCountry);
                    removedFormallyCountries.append(", ");
                }
            }
            if (removedFormallyCountries.length() > 0) {
                removedFormallyCountries = removedFormallyCountries.replace(removedFormallyCountries.length() - 2, removedFormallyCountries.length(), "");
            }

        }

        if (!currentVoluntaryCountries.isEmpty()) {
            for (String currentVoluntaryCountry : currentVoluntaryCountries) {
                if (!undoVoluntaryCountries.contains(currentVoluntaryCountry)) {
                    addedVoluntaryCountries.append(currentVoluntaryCountry);
                    addedVoluntaryCountries.append(", ");
                }
            }
            if (addedVoluntaryCountries.length() > 0) {
                addedVoluntaryCountries = addedVoluntaryCountries.replace(addedVoluntaryCountries.length() - 2, addedVoluntaryCountries.length(), "");
            }

        }

        if (!undoVoluntaryCountries.isEmpty()) {
            for (String undoVoluntaryCountry : undoVoluntaryCountries) {
                if (!currentVoluntaryCountries.contains(undoVoluntaryCountry)) {
                    removedVoluntaryCountries.append(undoVoluntaryCountry);
                    removedVoluntaryCountries.append(", ");
                }
            }
            if (removedVoluntaryCountries.length() > 0) {
                removedVoluntaryCountries = removedVoluntaryCountries.replace(removedVoluntaryCountries.length() - 2, removedVoluntaryCountries.length(), "");
            }

        }

        if (addedFormallyCountries.length() > 0) {
            resVec.add("'Countries reporting formally' added: " + addedFormallyCountries);
        }
        if (removedFormallyCountries.length() > 0) {
            resVec.add("'Countries reporting formally' removed: " + removedFormallyCountries);
        }
        if (addedVoluntaryCountries.length() > 0) {
            resVec.add("'Countries reporting voluntarily' added: " + addedVoluntaryCountries);
        }
        if (removedVoluntaryCountries.length() > 0) {
            resVec.add("'Countries reporting voluntarily' removed: " + removedVoluntaryCountries);
        }


        undoList = undoService.getUndoInformation(ts, "U", "T_RAISSUE_LNK");
        List<Issue> obligationIssues = obligationsService.findAllIssuesbyObligation(obligationID);
        Issue issue;
        StringBuffer addedIssues = new StringBuffer();
        StringBuffer removedIssues = new StringBuffer();
        List<String> undoIssues = new ArrayList<>();
        List<String> currentIssues = new ArrayList<>();

        if (obligationIssues != null) {
            for (Issue obligationIssue : obligationIssues) {
                currentIssues.add(obligationIssue.getIssueName());
            }
        }

        if (undoList != null) {
            for (UndoDTO undo : undoList) {
                if ("FK_ISSUE_ID".equals(undo.getCol())) {
                    issue = issueDao.findById(Integer.parseInt(undo.getValue()));
                    undoIssues.add(issue.getIssueName());
                }
            }
        }

        if (!currentIssues.isEmpty()) {
            for (String currentIssue : currentIssues) {
                if (!undoIssues.contains(currentIssue)) {
                    addedIssues.append(currentIssue);
                    addedIssues.append(", ");
                }
            }
            if (addedIssues.length() > 0) {
                addedIssues = addedIssues.replace(addedIssues.length() - 2, addedIssues.length(), "");
            }

        }

        if (!undoIssues.isEmpty()) {
            for (String undoIssue : undoIssues) {
                if (!currentIssues.contains(undoIssue)) {
                    removedIssues.append(undoIssue);
                    removedIssues.append(", ");
                }
            }
            if (removedIssues.length() > 0) {
                removedIssues = removedIssues.replace(removedIssues.length() - 2, removedIssues.length(), "");
            }

        }

        if (addedIssues.length() > 0) {
            resVec.add("'Environmental issues' added: " + addedIssues);
        }
        if (removedIssues.length() > 0) {
            resVec.add("'Environmental issues' removed: " + removedIssues);
        }

        undoList = undoService.getUndoInformation(ts, "U", "T_CLIENT_OBLIGATION_LNK");
        List<ClientDTO> obligationClients = obligationsService.findAllClientsByObligation(obligationID);
        ClientDTO client;
        StringBuffer addedClients = new StringBuffer();
        StringBuffer removedClients = new StringBuffer();
        List<String> undoClients = new ArrayList<>();
        List<String> currentClients = new ArrayList<>();

        if (obligationClients != null) {
            for (ClientDTO obligationClient : obligationClients) {
                currentClients.add(obligationClient.getName());
            }
        }

        if (undoList != null) {
            for (UndoDTO undo : undoList) {
                if ("FK_CLIENT_ID".equals(undo.getCol())) {
                    client = clientService.getById(Integer.parseInt(undo.getValue()));
                    undoClients.add(client.getName());
                }
            }
        }

        if (!currentClients.isEmpty()) {
            for (String currentClient : currentClients) {
                if (!undoClients.contains(currentClient)) {
                    addedClients.append(currentClient);
                    addedClients.append(", ");
                }
            }
            if (addedClients.length() > 0) {
                addedClients = addedClients.replace(addedClients.length() - 2, addedClients.length(), "");
            }

        }

        if (!undoClients.isEmpty()) {
            for (String undoClient : undoClients) {
                if (!currentClients.contains(undoClient)) {
                    removedClients.append(undoClient);
                    removedClients.append(", ");
                }
            }
            if (removedClients.length() > 0) {
                removedClients = removedClients.replace(removedClients.length() - 2, removedClients.length(), "");
            }

        }

        if (addedClients.length() > 0) {
            resVec.add("'Other clients using this reporting' added: " + addedClients);
        }
        if (removedClients.length() > 0) {
            resVec.add("'Other clients using this reporting' removed: " + removedClients);
        }

        undoList = undoService.getUndoInformation(ts, "U", "T_OBLIGATION_RELATION");
        Obligations currentRelation = obligationsService.findObligationRelation(obligationID);
        String undoRelation = null;
        String addedRelation = "";
        String removedRelation = "";

        if (undoList != null) {
            for (UndoDTO undo : undoList) {
                if ("FK_RA_ID2".equals(undo.getCol())) {
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

        if (!addedRelation.isEmpty() || !removedRelation.isEmpty()) {
            resVec.add("'Relation with other obligations' changed from '" + removedRelation + "' to '" + addedRelation + "'");
        }

        return resVec;
    }

    private String getSuffixValue(String value) throws ServiceException {
        String ret = null;
        int b = Integer.parseInt(value); //new Integer(value).intValue();
        if (b == 0) {
            ret = "checked";
        } else if (b == 1) {
            ret = "unchecked";
        }

        return ret;
    }

    private String getChkValue(String value) throws ServiceException {

        String ret = null;
        int b = Integer.parseInt(value); //new Integer(value).intValue();
        if (b == 0) {
            ret = "unchecked";
        } else if (b == 1) {
            ret = "checked";
        }
        return ret;
    }

    private String getDpsirValue(String value) throws ServiceException {
        String ret = null;
        if ("null".equalsIgnoreCase(value) || "no".equalsIgnoreCase(value)) {
            ret = "unchecked";
        } else if ("yes".equalsIgnoreCase(value)) {
            ret = "checked";
        }
        return ret;
    }

    private String getLabel(String col, String value, String currentValue) throws ServiceException {

        String label = "";

        // todo replace with case

        if ("TITLE".equalsIgnoreCase(col)) {
            label = "'Title' changed ";
        } else if ("DESCRIPTION".equalsIgnoreCase(col)) {
            label = "'Description' changed";
        } else if ("COORDINATOR_ROLE".equalsIgnoreCase(col)) {
            label = "'National reporting coordinators role' changed";
        } else if ("COORDINATOR_ROLE_SUF".equalsIgnoreCase(col)) {
            label = "'National reporting coordinators suffix' changed";
            value = getSuffixValue(value);
            currentValue = getSuffixValue(currentValue);
        } else if ("COORDINATOR".equalsIgnoreCase(col)) {
            label = "'National reporting coordinators name' changed";
        } else if ("COORDINATOR_URL".equalsIgnoreCase(col)) {
            label = "'National reporting coordinators URL' changed";
        } else if ("RESPONSIBLE_ROLE".equalsIgnoreCase(col)) {
            label = "'National reporting contacts role' changed";
        } else if ("RESPONSIBLE_ROLE_SUF".equalsIgnoreCase(col)) {
            label = "'National reporting contacts suffix' changed";
            value = getSuffixValue(value);
            currentValue = getSuffixValue(currentValue);
        } else if ("NATIONAL_CONTACT".equalsIgnoreCase(col)) {
            label = "'National reporting contacts name' changed";
        } else if ("NATIONAL_CONTACT_URL".equalsIgnoreCase(col)) {
            label = "'National reporting contacts URL' changed";
        } else if ("REPORT_FREQ_MONTHS".equalsIgnoreCase(col)) {
            label = "'Reporting frequency in months' changed";
        } else if ("REPORT_FREQ".equalsIgnoreCase(col)) {
            label = "'Reporting frequency' changed";
        } else if ("REPORT_FREQ_DETAILS".equalsIgnoreCase(col)) {
            label = "'Reporting frequency details' changed";
        } else if ("FIRST_REPORTING".equalsIgnoreCase(col)) {
            label = "'Baseline reporting date' changed";
        } else if ("VALID_TO".equalsIgnoreCase(col)) {
            label = "'Valid to' changed";
        } else if ("NEXT_DEADLINE".equalsIgnoreCase(col)) {
            label = "'Next due date' changed";
        } else if ("NEXT_DEADLINE2".equalsIgnoreCase(col)) {
            label = "'Due date after next due (calculated automatically)' changed";
        } else if ("NEXT_REPORTING".equalsIgnoreCase(col)) {
            label = "'Reporting date' changed";
        } else if ("DATE_COMMENTS".equalsIgnoreCase(col)) {
            label = "'Date comments' changed";
        } else if ("FORMAT_NAME".equalsIgnoreCase(col)) {
            label = "'Name of reporting guidelines' changed";
        } else if ("REPORT_FORMAT_URL".equalsIgnoreCase(col)) {
            label = "'URL to reporting guidelines' changed";
        } else if ("VALID_SINCE".equalsIgnoreCase(col)) {
            label = "'Format valid since' changed";
        } else if ("REPORTING_FORMAT".equalsIgnoreCase(col)) {
            label = "'Reporting guidelines -Extra info' changed";
        } else if ("LOCATION_INFO".equalsIgnoreCase(col)) {
            label = "'Name of repository' changed";
        } else if ("LOCATION_PTR".equalsIgnoreCase(col)) {
            label = "'URL to repository' changed";
        } else if ("DATA_USED_FOR".equalsIgnoreCase(col)) {
            label = "'Data used for (URL)' changed";
        } else if ("LEGAL_MORAL".equalsIgnoreCase(col)) {
            label = "'Obligation type' changed";
        } else if ("PARAMETERS".equalsIgnoreCase(col)) {
            label = "'Parameters' changed";
        } else if ("EEA_PRIMARY".equalsIgnoreCase(col)) {
            label = "'This obligation is an Eionet core data flow' changed";
            value = getChkValue(value);
            currentValue = getChkValue(currentValue);
        } else if ("EEA_CORE".equalsIgnoreCase(col)) {
            label = "'This obligation is used for EEA Core set of indicators' changed";
            value = getChkValue(value);
            currentValue = getChkValue(currentValue);
        } else if ("FLAGGED".equalsIgnoreCase(col)) {
            label = "'This obligation is flagged' changed";
            value = getChkValue(value);
            currentValue = getChkValue(currentValue);
        } else if ("DPSIR_D".equalsIgnoreCase(col)) {
            label = "'DPSIR D' changed";
            value = getDpsirValue(value);
            currentValue = getDpsirValue(value);
        } else if ("DPSIR_P".equalsIgnoreCase(col)) {
            label = "'DPSIR P' changed";
            value = getDpsirValue(value);
            currentValue = getDpsirValue(currentValue);
        } else if ("OVERLAP_URL".equalsIgnoreCase(col)) {
            label = "'URL of overlapping obligation' changed";
        } else if ("COMMENT".equalsIgnoreCase(col)) {
            label = "'General comments' changed";
        } else if ("AUTHORITY".equalsIgnoreCase(col)) {
            label = "'Authority giving rise to the obligation' changed";
        } else if ("RM_VERIFIED".equalsIgnoreCase(col)) {
            label = "'Verified' changed";
        } else if ("RM_VERIFIED_BY".equalsIgnoreCase(col)) {
            label = "'Verified by' changed";
        } else if ("RM_NEXT_UPDATE".equalsIgnoreCase(col)) {
            label = "'Next update due' changed";
        } else if ("VALIDATED_BY".equalsIgnoreCase(col)) {
            label = "'Validated by' changed";
        } else if ("FK_CLIENT_ID".equalsIgnoreCase(col)) {
            label = "'Report to' changed";
            value = clientService.getOrganisationNameByID(value);
            currentValue = clientService.getOrganisationNameByID(currentValue);
        } else if ("CONTINOUS_REPORTING".equalsIgnoreCase(col)) {
            label = "'Continuous reporting' changed";
        } else if ("LAST_UPDATE".equalsIgnoreCase(col)) {
            label = "'Last update' changed";
        } else if ("LAST_HARVESTED".equalsIgnoreCase(col)) {
            label = "'Last harvested date' changed";
        } else if ("TERMINATE".equalsIgnoreCase(col)) {
            label = "'Terminate' changed";
        }

        label = label + " from '" + value + "' to '" + currentValue + "'";

        return label;

    }

    private void sendEvent(boolean isUpdate, Obligations pObligations, Integer obligationId, long ts) throws ServiceException {
        String userName = getUserName();
        FileServiceIF fileService = RODServices.getFileService();

        try {
            Vector<Vector<String>> lists = new Vector<>();
            Vector<String> list = new Vector<>();
            long timestamp = System.currentTimeMillis();
            String events = "http://rod.eionet.europa.eu/events/" + timestamp;

            //int obligation_id = Integer.valueOf(pObligationsID).intValue();

            if (isUpdate) {
                list.add(events);
                list.add("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
                list.add(Attrs.SCHEMA_RDF + "ObligationChange");
                lists.add(list);

                list = new Vector<>();
                list.add(events);
                String etSchema = fileService.getStringProperty(FileServiceIF.UNS_EVENTTYPE_PREDICATE);
                list.add(etSchema);
                list.add("Obligation change");
                lists.add(list);

                list = new Vector<>();
                list.add(events);
                list.add("http://purl.org/dc/elements/1.1/title");
                list.add("Obligation change");
                lists.add(list);

            } else {
                list.add(events);
                list.add("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
                list.add(Attrs.SCHEMA_RDF + "NewObligation");
                lists.add(list);

                list = new Vector<>();
                list.add(events);
                String etSchema = fileService.getStringProperty(FileServiceIF.UNS_EVENTTYPE_PREDICATE);
                list.add(etSchema);
                list.add("New Obligation");
                lists.add(list);

                list = new Vector<>();
                list.add(events);
                list.add("http://purl.org/dc/elements/1.1/title");
                list.add("New Obligation");
                lists.add(list);
            }

            list = new Vector<>();
            list.add(events);
            String oblSchema = fileService.getStringProperty(FileServiceIF.UNS_OBLIGATION_PREDICATE);
            list.add(oblSchema);
            list.add(pObligations.getOblTitle());
            lists.add(list);

            list = new Vector<>();
            list.add(events);
            list.add(Attrs.SCHEMA_RDF + "responsiblerole");
            list.add(pObligations.getResponsibleRole());
            lists.add(list);

            list = new Vector<>();
            list.add(events);
            list.add(Attrs.SCHEMA_RDF + "actor");
            list.add(userName);
            lists.add(list);

            if (isUpdate) {
                Vector<String> changes = getChanges(obligationId, ts);
                for (Enumeration<String> en = changes.elements(); en.hasMoreElements(); ) {
                    String label = en.nextElement();
                    list = new Vector<>();
                    list.add(events);
                    list.add(Attrs.SCHEMA_RDF + "change");
                    list.add(label);
                    lists.add(list);
                }
            }

            list = new Vector<>();
            list.add(events);
            list.add("http://purl.org/dc/elements/1.1/identifier");
            String url = "http://rod.eionet.europa.eu/obligations/" + obligationId;
            list.add(url);

            lists.add(list);

            if (!lists.isEmpty()) {
                UNSEventSender.makeCall(lists);
            }
        } catch (RuntimeException e) {
            throw new ResourceNotFoundException(e.getMessage(), e);
        }


    }

}

