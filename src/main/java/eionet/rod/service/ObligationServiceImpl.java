package eionet.rod.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eionet.rod.dao.ObligationsDao;
import eionet.rod.model.ClientDTO;
import eionet.rod.model.Issue;
import eionet.rod.model.Obligations;
import eionet.rod.model.SiblingObligation;
import eionet.rod.model.Spatial;

@Service(value = "obligationService")
@Transactional
public class ObligationServiceImpl implements ObligationService {
	
	@Autowired
	private ObligationsDao obligationsDao;
	
	@Override
	public List<Obligations> findAll() {
		return obligationsDao.findAll();
	}
	
	@Override
	public Obligations findOblId(Integer obligationId) {
		return obligationsDao.findOblId(obligationId);
	}
	@Override
	public List<SiblingObligation> findSiblingObligations(Integer obligationId){
		return obligationsDao.findSiblingObligations(obligationId);
	}
	
	@Override
	public Integer insertObligation(Obligations obligation, List<ClientDTO> allObligationClients, List<Spatial> allObligationCountries, List<Spatial> allObligationVoluntaryCountries, List<Issue> allSelectedIssues) {
		return obligationsDao.insertObligation(obligation, allObligationClients, allObligationCountries, allObligationVoluntaryCountries, allSelectedIssues);
	}
	@Override
	public void updateObligations(Obligations obligations, List<ClientDTO> allObligationClients, List<Spatial> allObligationCountries, List<Spatial> allObligationVoluntaryCountries, List<Issue> allSelectedIssues) {
		obligationsDao.updateObligations(obligations, allObligationClients, allObligationCountries, allObligationVoluntaryCountries, allSelectedIssues);
	}
	
	@Override
	public List<Spatial> findAllCountriesByObligation(Integer ObligationID, String voluntary) {
		return obligationsDao.findAllCountriesByObligation(ObligationID, voluntary);
	}

	@Override
	public List<Issue> findAllIssuesbyObligation(Integer ObligationID){
		return obligationsDao.findAllIssuesbyObligation(ObligationID);
	}
	
	@Override
	public List<Obligations> findObligationList(String clientId, String issueId, String spatialId, String terminate, String deadlineCase, String anmode){
		return obligationsDao.findObligationList(clientId, issueId, spatialId, terminate, deadlineCase, anmode);
	}
	@Override
	public void deleteObligations(String obligations) {
		obligationsDao.deleteObligations(obligations);
	}
	
	@Override
	public Obligations findObligationRelation(Integer obligationId) {
		return obligationsDao.findObligationRelation(obligationId);
	}
}
