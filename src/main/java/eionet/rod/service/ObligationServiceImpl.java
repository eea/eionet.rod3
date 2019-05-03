package eionet.rod.service;

import eionet.rod.dao.ObligationsDao;
import eionet.rod.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


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
    public List<SiblingObligation> findSiblingObligations(Integer obligationId) {
        return obligationsDao.findSiblingObligations(obligationId);
    }

    @Override
    public Integer insertObligation(Obligations obligation, List<ClientDTO> allObligationClients, List<Spatial> allObligationCountries, List<Spatial> allObligationVoluntaryCountries, List<Issue> allSelectedIssues) {
        return obligationsDao.insertObligation(obligation, allObligationClients, allObligationCountries, allObligationVoluntaryCountries, allSelectedIssues);
    }

    @Override
    public void updateObligations(Obligations obligation, List<ClientDTO> allObligationClients, List<Spatial> allObligationCountries, List<Spatial> allObligationVoluntaryCountries, List<Issue> allSelectedIssues) {
        obligationsDao.updateObligations(obligation, allObligationClients, allObligationCountries, allObligationVoluntaryCountries, allSelectedIssues);
    }

    @Override
    public List<Spatial> findAllCountriesByObligation(Integer obligationId, String voluntary) {
        return obligationsDao.findAllCountriesByObligation(obligationId, voluntary);
    }

    @Override
    public List<Issue> findAllIssuesbyObligation(Integer obligationId) {
        return obligationsDao.findAllIssuesbyObligation(obligationId);
    }

    @Override
    public List<Obligations> findObligationList(String clientId, String issueId, String spatialId, String terminate, String deadlineCase, String anmode, String date1, String date2, boolean deadlinePage) {
        return obligationsDao.findObligationList(clientId, issueId, spatialId, terminate, deadlineCase, anmode, date1, date2, deadlinePage);
    }

    @Override
    public void deleteObligations(String obligations) {
        obligationsDao.deleteObligations(obligations);
    }

    @Override
    public Obligations findObligationRelation(Integer obligationId) {
        return obligationsDao.findObligationRelation(obligationId);
    }

    @Override
    public List<Roles> getRespRoles() {
        return obligationsDao.getRespRoles();
    }

    @Override
    public List<ClientDTO> findAllClientsByObligation(Integer obligationID) {
        return obligationsDao.findAllClientsByObligation(obligationID);
    }

}
