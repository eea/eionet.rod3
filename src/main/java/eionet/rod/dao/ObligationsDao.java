/**
 * 
 */
package eionet.rod.dao;

import java.util.List;

import org.springframework.context.ApplicationContextException;

import eionet.rod.model.ClientDTO;
import eionet.rod.model.Issue;
import eionet.rod.model.Obligations;
import eionet.rod.model.Roles;
import eionet.rod.model.SiblingObligation;
import eionet.rod.model.Spatial;

/**
 * @author ycarrasco
 *
 */
public interface ObligationsDao {

	List<Obligations> findAll();
	
	Obligations findOblId(Integer obligationId) throws ApplicationContextException;
	
	List<SiblingObligation> findSiblingObligations(Integer siblingoblId);
	
	Integer insertObligation(Obligations obligation, List<ClientDTO> allObligationClients, List<Spatial> allObligationCountries,List<Spatial> allObligationVoluntaryCountries, List<Issue> allSelectedIssues);
	
	void updateObligations(Obligations obligations, List<ClientDTO> allObligationClients, List<Spatial> allObligationCountries,List<Spatial> allObligationVoluntaryCountries, List<Issue> allSelectedIssues);
	
	List<Spatial> findAllCountriesByObligation(Integer obligationId, String voluntary);
	
	List<Issue> findAllIssuesbyObligation(Integer obligationId);
	
	List<Obligations> findObligationList(String clientId, String issueId, String spatialId, String terminate, String deadlineCase, String anmode, String date1, String date2, boolean deadlinePage);
	
	void deleteObligations(String obligations);
	
	Obligations findObligationRelation(Integer obligationId);
	
	List<Roles> getRespRoles();

	List<ClientDTO> findAllClientsByObligation(Integer obligationID);
		
}
