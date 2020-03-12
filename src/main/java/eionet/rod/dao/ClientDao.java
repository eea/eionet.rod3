package eionet.rod.dao;

import eionet.rod.model.ClientDTO;
import eionet.rod.model.InstrumentDTO;
import eionet.rod.model.Obligations;

import java.util.List;

/**
 * Service to store metadata for T_CLIENT using JDBC.
 */
public interface ClientDao {

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

    List<ClientDTO> findOblClients(Integer raID, String status);

    List<Obligations> getDirectObligations(Integer clientId);

    List<Obligations> getIndirectObligations(Integer clientId);

    List<InstrumentDTO> getDirectInstruments(Integer clientId);

    List<InstrumentDTO> getIndirectInstruments(Integer clientId);

    String getOrganisationNameByID(String clientId);

    boolean isClientInUse(Integer clientId);
}
