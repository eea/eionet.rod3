package eionet.rod.service;

import eionet.rod.model.ClientDTO;
import eionet.rod.model.InstrumentDTO;
import eionet.rod.model.Obligations;

import java.util.List;

public interface ClientService {

    void insert(ClientDTO clientRec);

    ClientDTO getById(Integer clientId);

    List<ClientDTO> getAllClients();

    void deleteById(Integer clientId);

    void deleteAll();

    void update(ClientDTO clientRec);

    boolean clientExists(Integer clientId);

    List<ClientDTO> findOblClients(Integer raID, String status);

    List<Obligations> getDirectObligations(Integer clientId);

    List<Obligations> getIndirectObligations(Integer clientId);

    List<InstrumentDTO> getDirectInstruments(Integer clientId);

    List<InstrumentDTO> getIndirectInstruments(Integer clientId);

    String getOrganisationNameByID(String clientId);

    boolean isClientInUse(Integer clientId);
}
