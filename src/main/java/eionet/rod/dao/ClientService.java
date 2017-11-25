package eionet.rod.dao;

import java.util.List;
import eionet.rod.model.ClientDTO;

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

    void deleteAll();

    void update(ClientDTO clientRec);

    boolean clientExists(Integer clientId);
}
