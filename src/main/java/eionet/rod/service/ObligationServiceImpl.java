package eionet.rod.service;

import eionet.rod.dao.ObligationsDao;
import eionet.rod.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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

    @Override
    public List<Obligations> findActivities() {
        return obligationsDao.findActivities();
    }

    @Override
    public List<Obligations> getDeadlines() {
        return obligationsDao.getDeadlines();
    }

    @Override
    public void updateTermination(Integer obligationId, String terminated) {
        obligationsDao.updateTermination(obligationId, terminated);
    }

    @Override
    public void updateDeadlines(Integer obligationId, Date nextDeadline, Date nextDeadline2, Date current) {
        obligationsDao.updateDeadlines(obligationId, nextDeadline, nextDeadline2, current);
    }

    @Override
    public List<Obligations> getUpcomingDeadlines(int days) {
        return obligationsDao.getUpcomingDeadlines(days);
    }


}
