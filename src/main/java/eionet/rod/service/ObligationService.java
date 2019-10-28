package eionet.rod.service;

import eionet.rod.model.*;

import java.util.Date;
import java.util.List;

/**
 * @author ycarrasco
 */
public interface ObligationService {

    List<Obligations> findAll();

    Obligations findOblId(Integer obligationId);

    List<SiblingObligation> findSiblingObligations(Integer obligationId);

    Integer insertObligation(Obligations obligation, List<ClientDTO> allObligationClients, List<Spatial> allObligationCountries, List<Spatial> allObligationVoluntaryCountries, List<Issue> allSelectedIssues);

    void updateObligations(Obligations obligation, List<ClientDTO> allObligationClients, List<Spatial> allObligationCountries, List<Spatial> allObligationVoluntaryCountries, List<Issue> allSelectedIssues);

    List<Spatial> findAllCountriesByObligation(Integer obligationId, String voluntary);

    List<Issue> findAllIssuesbyObligation(Integer obligationId);

    List<Obligations> findObligationList(String clientId, String issueId, String spatialId, String terminate, String deadlineCase, String anmode, String date1, String date2, boolean deadlinePage);

    void deleteObligations(String obligations);

    Obligations findObligationRelation(Integer obligationId);

    List<ClientDTO> findAllClientsByObligation(Integer obligationID);

    List<Roles> getRespRoles();

    List<Obligations> findActivities();

    List<Obligations> getDeadlines();

    void updateTermination(Integer obligationId, String terminated);

    void updateDeadlines(Integer obligationId, Date nextDeadline, Date nextDeadline2, Date current);
}
