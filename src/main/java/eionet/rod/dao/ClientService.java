package eionet.rod.dao;

import java.util.List;

import eionet.rod.model.ClientDTO;
import eionet.rod.model.InstrumentDTO;
import eionet.rod.model.Obligations;

/**
 * Service to store metadata for T_CLIENT using JDBC.
 */
public interface ClientService {

    void insert(ClientDTO clientRec);

    ClientDTO getById(Integer clientId);

     /**
     * Get all clients, and only the attributes that are relevant.
     */
    List<ClientDTO> getAllClients();

    void deleteById(Integer clientId);

    void deleteByIds(String clientIds);
    
    void deleteAll();

    void update(ClientDTO clientRec);

    boolean clientExists(Integer clientId);
    
    /**
     * Get all clients, by obligation id with status = C.
     */
    
    List<ClientDTO> findOblClients(Integer raID, String c);
    
    List<Obligations> getDirectObligations(Integer clientId);
    
    List<Obligations> getIndirectObligations(Integer clientId);
    
    List<InstrumentDTO> getDirectInstruments(Integer clientId);
    
    List<InstrumentDTO> getIndirectInstruments(Integer clientId);
    
    String getOrganisationNameByID(String clientId);
    
}
