package eionet.rod.service;

import eionet.rod.dao.ClientDao;
import eionet.rod.model.ClientDTO;
import eionet.rod.model.InstrumentDTO;
import eionet.rod.model.Obligations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service(value = "clientService")
@Transactional
public class ClientServiceImpl implements ClientService {

    @Autowired
    ClientDao clientDao;

    @Override
    public void insert(ClientDTO clientRec) {
        clientDao.insert(clientRec);
    }

    @Override
    public ClientDTO getById(Integer clientId) {
        return clientDao.getById(clientId);
    }

    @Override
    public List<ClientDTO> getAllClients() {
        return clientDao.getAllClients();
    }

    @Override
    public void deleteById(Integer clientId) {
        clientDao.deleteById(clientId);
    }

    @Override
    public void deleteAll() {
        clientDao.deleteAll();
    }

    @Override
    public void update(ClientDTO clientRec) {
        clientDao.update(clientRec);
    }

    @Override
    public boolean clientExists(Integer clientId) {
        return clientDao.clientExists(clientId);
    }

    @Override
    public List<ClientDTO> findOblClients(Integer raID, String status) {
        return clientDao.findOblClients(raID, status);
    }

    @Override
    public List<Obligations> getDirectObligations(Integer clientId) {
        return clientDao.getDirectObligations(clientId);
    }

    @Override
    public List<Obligations> getIndirectObligations(Integer clientId) {
        return clientDao.getIndirectObligations(clientId);
    }

    @Override
    public List<InstrumentDTO> getDirectInstruments(Integer clientId) {
        return clientDao.getDirectInstruments(clientId);
    }

    @Override
    public List<InstrumentDTO> getIndirectInstruments(Integer clientId) {
        return clientDao.getIndirectInstruments(clientId);
    }

    @Override
    public String getOrganisationNameByID(String clientId) {
        return clientDao.getOrganisationNameByID(clientId);
    }

    @Override
    public boolean isClientInUse(Integer clientId) {
        return clientDao.isClientInUse(clientId);
    }
}
