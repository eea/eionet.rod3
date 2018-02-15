/**
 * 
 */
package eionet.rod.dao;

import java.util.List;

import org.springframework.context.ApplicationContextException;

import eionet.rod.model.ClientDTO;
import eionet.rod.model.Issue;
import eionet.rod.model.Obligations;
import eionet.rod.model.SiblingObligation;
import eionet.rod.model.Spatial;

/**
 * @author ycarrasco
 *
 */
public interface ObligationsDao {

	public List<Obligations> findAll();
	
	public Obligations findOblId(Integer obligationId) throws ApplicationContextException;
	
	public List<SiblingObligation> findSiblingObligations(Integer obligationId);
	
	public Integer insertObligation(Obligations obligation, List<ClientDTO> allObligationClients, List<Spatial> allObligationCountries,List<Spatial> allObligationVoluntaryCountries, List<Issue> allSelectedIssues);
	
	public void updateObligations(Obligations obligations, List<ClientDTO> allObligationClients, List<Spatial> allObligationCountries,List<Spatial> allObligationVoluntaryCountries, List<Issue> allSelectedIssues);
	
	public List<Spatial> findAllCountriesByObligation(Integer ObligationID, String voluntary);
	
	public List<Issue> findAllIssuesbyObligation(Integer ObligationID);
	
	public List<Obligations> findObligationList(String clientId, String issueId, String spatialId, String terminate, String deadlineCase);
	
	public void deleteObligations(String obligations);
	
	public Obligations findObligationRelation(Integer obligationId);
		
}
