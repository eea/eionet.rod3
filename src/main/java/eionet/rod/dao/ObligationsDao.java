package eionet.rod.dao;

import eionet.rod.model.*;
import org.springframework.context.ApplicationContextException;

import java.util.Date;
import java.util.List;

/**
 * @author ycarrasco
 *
 */
public interface ObligationsDao {

    List<Obligations> findAll();

    Obligations findOblId(Integer obligationId) throws ApplicationContextException;

    List<SiblingObligation> findSiblingObligations(Integer siblingoblId);

    Integer insertObligation(Obligations obligation, List<ClientDTO> allObligationClients, List<Spatial> allObligationCountries, List<Spatial> allObligationVoluntaryCountries, List<Issue> allSelectedIssues);

    void updateObligations(Obligations obligations, List<ClientDTO> allObligationClients, List<Spatial> allObligationCountries, List<Spatial> allObligationVoluntaryCountries, List<Issue> allSelectedIssues);

    List<Spatial> findAllCountriesByObligation(Integer obligationId, String voluntary);

    List<Issue> findAllIssuesbyObligation(Integer obligationId);

    void updateDeadlines(Integer obligationId, Date nextDeadline, Date nextDeadline2, Date current);

    List<Obligations> findObligationList(String clientId, String issueId, String spatialId, String terminate, String deadlineCase, String anmode, String date1, String date2, boolean deadlinePage);

    void deleteObligations(String obligations);

    Obligations findObligationRelation(Integer obligationId);

    List<Roles> getRespRoles();

    List<ClientDTO> findAllClientsByObligation(Integer obligationID);

    List<Obligations> findActivities();

    List<Obligations> getDeadlines();

    void updateTermination(Integer obligationId, String terminated);

    List<Obligations> getUpcomingDeadlines(int days);
}
