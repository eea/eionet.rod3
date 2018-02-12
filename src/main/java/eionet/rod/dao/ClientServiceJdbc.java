package eionet.rod.dao;


import java.util.List;
import javax.sql.DataSource;

import eionet.rod.model.ClientDTO;
import eionet.rod.model.InstrumentDTO;
import eionet.rod.model.Obligations;
import eionet.rod.util.exception.ResourceNotFoundException;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationContextException;
import org.springframework.dao.DataAccessException;

/**
 * Service to store metadata for T_CLIENT using JDBC.
 */
@Service
public class ClientServiceJdbc implements ClientService {

    //@Autowired
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void insert(ClientDTO clientRec) {
        String query = "INSERT INTO T_CLIENT (CLIENT_NAME, CLIENT_ACRONYM, "
                + "CLIENT_ADDRESS, CLIENT_URL, POSTAL_CODE, CLIENT_EMAIL, CITY, "
                + "COUNTRY, DESCRIPTION) VALUES (?,?,?,?,?,?,?,?,?)";

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(query,
                clientRec.getName(),
                clientRec.getAcronym(),
                clientRec.getAddress(),
                clientRec.getUrl(),
                clientRec.getPostalCode(),
                clientRec.getEmail(),
                clientRec.getCity(),
                clientRec.getCountry(),
                clientRec.getDescription()
                );
    }

    @Override
    public ClientDTO getById(Integer clientId) throws ApplicationContextException {
        String query = "SELECT T_CLIENT.PK_CLIENT_ID AS clientId,"
                + "CLIENT_NAME AS name, CLIENT_ACRONYM AS acronym,"
                + "CLIENT_URL AS url, CLIENT_ADDRESS AS address, CLIENT_EMAIL AS email,"
                + "DESCRIPTION, POSTAL_CODE AS postalcode, CITY, COUNTRY, CLIENT_SHORT_NAME AS shortName "
                + "FROM T_CLIENT "
                + "WHERE T_CLIENT.PK_CLIENT_ID = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
	    try {
	        ClientDTO clientRec = jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<ClientDTO>(ClientDTO.class), clientId);
	        return clientRec;
	       
	    } catch (DataAccessException e) {
			throw new ResourceNotFoundException("DataAccessException error: " + e);
		}
    }

    @Override
    public List<ClientDTO> getAllClients() {
        String query = "SELECT PK_CLIENT_ID AS clientId, CLIENT_NAME AS name, CLIENT_ACRONYM AS acronym,"
                + "CLIENT_URL AS url, CLIENT_ADDRESS AS address, CLIENT_EMAIL AS email,"
                + "DESCRIPTION, POSTAL_CODE AS postalcode, CITY, COUNTRY, CLIENT_SHORT_NAME AS shortName "
                + "FROM T_CLIENT "
                + "ORDER BY name";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<ClientDTO>(ClientDTO.class));
    }

    @Override
    public void deleteById(Integer clientId) {
        String query = "DELETE FROM T_CLIENT WHERE PK_CLIENT_ID = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(query, clientId);
    }

    @Override
    public void deleteAll() {
        String query = "DELETE FROM T_CLIENT";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(query);
    }

    @Override
    public void update(ClientDTO clientRec) {
        String update = "UPDATE T_CLIENT SET CLIENT_NAME=?, CLIENT_SHORT_NAME=?, "
                + "CLIENT_ACRONYM=?, CLIENT_ADDRESS=?, CLIENT_URL=?, "
                + "POSTAL_CODE=?, CLIENT_EMAIL=?, CITY=?, COUNTRY=?, DESCRIPTION=? "
                + "WHERE PK_CLIENT_ID=?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(update,
                clientRec.getName(),
                clientRec.getShortName(),
                clientRec.getAcronym(),
                clientRec.getAddress(),
                clientRec.getUrl(),
                clientRec.getPostalCode(),
                clientRec.getEmail(),
                clientRec.getCity(),
                clientRec.getCountry(),
                clientRec.getDescription(),
                clientRec.getClientId());
    }

    //FIXME: Stub
    @Override
    public boolean clientExists(Integer clientId) {
        return true;
    }
    
    @Override
    public List<ClientDTO> findOblClients(Integer raID, String status){
    	
    	String query = "SELECT OBCL.FK_CLIENT_ID AS clientId, CL.CLIENT_NAME as name "  
    			+ "FROM T_CLIENT_OBLIGATION_LNK OBCL "
    			+ "LEFT JOIN T_CLIENT CL on CL.PK_CLIENT_ID = OBCL.FK_CLIENT_ID "  
    			+ "WHERE ";
    			if (status != null){
    				query = query + "STATUS='" + status + "' and ";
    			}
    			query = query +  "OBCL.FK_RA_ID = ? ";
    		    query = query +  "ORDER BY CL.CLIENT_NAME";
    	
    	String queryCount = "SELECT Count(*) AS clientId "
    			+ "FROM T_CLIENT_OBLIGATION_LNK OBCL "
    			+ "LEFT JOIN T_CLIENT CL on CL.PK_CLIENT_ID = OBCL.FK_CLIENT_ID "
    			+ "WHERE ";
 		    	if (status != null){
 		    		queryCount = queryCount + "STATUS='" + status + "' and ";
				}
 		    	queryCount = queryCount +  "OBCL.FK_RA_ID = ? ";

    	
    	JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    	
    	
        try {
    		
			Integer countObligationClients = jdbcTemplate.queryForObject(queryCount, Integer.class, raID);
		
			if (countObligationClients == 0) {
				List<ClientDTO> clients = null;
				return clients;
							
				//throw new ResourceNotFoundException("The obligation you requested with id " + raID + " have not client with status = C");
		
			}else {
		
				 return jdbcTemplate.query(query, new BeanPropertyRowMapper<ClientDTO>(ClientDTO.class), raID);

			}
			
		} catch (DataAccessException e) {
			throw new ResourceNotFoundException("The obligation you requested with id " + raID + " have not client with status = C");
		}
     }
    
    private static final String q_direct_obligations =
        "SELECT PK_RA_ID as obligationId, FK_SOURCE_ID as sourceId, TITLE as oblTitle, TERMINATE as terminate " +
        "FROM T_OBLIGATION, T_CLIENT_OBLIGATION_LNK " +
        "WHERE PK_RA_ID = FK_RA_ID and STATUS='M' and " +
        "FK_CLIENT_ID=? ORDER BY TITLE";
    
    public List<Obligations> getDirectObligations(Integer clientId){
    	 
    	JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query(q_direct_obligations, new BeanPropertyRowMapper<Obligations>(Obligations.class),clientId);
    }

    private static final String q_indirect_obligations =
    	"SELECT PK_RA_ID as obligationId, FK_SOURCE_ID as sourceId, TITLE as oblTitle, TERMINATE as terminate " +
        "FROM T_OBLIGATION, T_CLIENT_OBLIGATION_LNK " +
        "WHERE PK_RA_ID = FK_RA_ID and STATUS != 'M' and " +
        "FK_CLIENT_ID=? ORDER BY TITLE";
    
    public List<Obligations> getIndirectObligations(Integer clientId){
   	 
    	JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query(q_indirect_obligations, new BeanPropertyRowMapper<Obligations>(Obligations.class),clientId);
    }

    private static final String q_direct_instruments =
    	"SELECT S.PK_SOURCE_ID as sourceId, S.ALIAS as sourceAlias, S.LEGAL_NAME as sourceLegalName, S.TITLE as sourceTitle " +
    	"FROM T_SOURCE S, T_CLIENT_SOURCE_LNK CL " +
        "WHERE CL.FK_CLIENT_ID=? AND CL.FK_SOURCE_ID=S.PK_SOURCE_ID and CL.STATUS = 'M' " +
        "ORDER BY ALIAS";

    public List<InstrumentDTO> getDirectInstruments(Integer clientId){
    	JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query(q_direct_instruments, new BeanPropertyRowMapper<InstrumentDTO>(InstrumentDTO.class),clientId);	
    }
    
    
    private static final String q_indirect_instruments =
    		"SELECT S.PK_SOURCE_ID as sourceId, S.ALIAS as sourceAlias, S.LEGAL_NAME as sourceLegalName, S.TITLE as sourceTitle " +
    	"FROM T_SOURCE S, T_CLIENT_SOURCE_LNK CL " +
        "WHERE CL.FK_CLIENT_ID=? AND CL.FK_SOURCE_ID=S.PK_SOURCE_ID and CL.STATUS != 'M' " +
        "ORDER BY ALIAS";
    
    public List<InstrumentDTO> getIndirectInstruments(Integer clientId) {
    	JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query(q_indirect_instruments, new BeanPropertyRowMapper<InstrumentDTO>(InstrumentDTO.class),clientId);	
    }
    
}
