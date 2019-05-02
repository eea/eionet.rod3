package eionet.rod.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eionet.rod.model.ClientDTO;
import eionet.rod.model.HierarchyInstrumentDTO;
import eionet.rod.model.InstrumentClassificationDTO;
import eionet.rod.model.InstrumentDTO;
import eionet.rod.model.InstrumentFactsheetDTO;
import eionet.rod.model.InstrumentObligationDTO;
import eionet.rod.model.InstrumentsListDTO;
import eionet.rod.util.RODUtil;
import eionet.rod.util.exception.ResourceNotFoundException;

/**
 * Service to store metadata for T_SPURCE using JDBC.
 */
@Service(value = "sourceService")
@Transactional
public class SourceServiceJdbc implements SourceService {


	private static final Log logger = LogFactory.getLog(SourceServiceJdbc.class);

	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

	@Override
	public InstrumentFactsheetDTO getById(Integer sourceId)  throws ResourceNotFoundException {
		String query = "SELECT T_SOURCE.PK_SOURCE_ID AS sourceId,"
                + "TITLE AS sourceTitle, URL AS sourceUrl,"
                + "ALIAS AS sourceAlias, CELEX_REF AS sourceCelexRef, SOURCE_CODE AS sourceCode,"
                + "VALID_FROM AS sourceValidFrom, ABSTRACT AS sourceAbstract, COMMENT AS sourceComment, ISSUED_BY_URL AS sourceIssuedByUrl,"
                + "EC_ENTRY_INTO_FORCE AS sourceEcEntryIntoForce, EC_ACCESSION AS sourceEcAccession, SECRETARIAT AS sourceSecretariat,"
                + "SECRETARIAT_URL AS sourceSecretariatUrl, TERMINATE AS sourceTerminate, "
                + "FK_TYPE_ID AS sourceFkTypeId, LEGAL_NAME AS sourceLegalName, LAST_MODIFIED AS sourceLastModified, ISSUED_BY AS sourceIssuedBy, LAST_UPDATE AS sourceLastUpdate "
                + "FROM T_SOURCE "
                + "WHERE T_SOURCE.PK_SOURCE_ID = ?";

		InstrumentFactsheetDTO instrumentFactsheetRec = null;
		
		try {
		
			instrumentFactsheetRec = jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(InstrumentFactsheetDTO.class), sourceId);
			
			List<InstrumentObligationDTO> obligations = getObligationsById(sourceId);
	
			instrumentFactsheetRec.setObligations(obligations);
			
			ClientDTO clientRec = getClient(sourceId);
			if (clientRec != null) {
				instrumentFactsheetRec.setClientId(clientRec.getClientId());
				instrumentFactsheetRec.setClientName(clientRec.getName());
				instrumentFactsheetRec.setClientUrl(clientRec.getUrl());
			}
			instrumentFactsheetRec.setParent(getParent(sourceId));
			
			instrumentFactsheetRec.setRelatedInstruments(getRelatedInstruments(sourceId));
			
			instrumentFactsheetRec.setClassifications(getInstrumentClassifications(sourceId));

		} catch(EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Not found");
		} catch (DataAccessException e) {
		    logger.debug(e, e);
			throw new ResourceNotFoundException("DataAccessException error: " + e, e);
		}
		
		return instrumentFactsheetRec;
	}

	@Override
	public List<InstrumentObligationDTO> getObligationsById(Integer sourceId) {
		String query = "SELECT PK_RA_ID AS obligationId, O.TITLE AS title, AUTHORITY AS authority, O.TERMINATE AS terminate "
				+ "FROM T_OBLIGATION AS O "
				+ "LEFT JOIN T_SOURCE AS S ON S.PK_SOURCE_ID = O.FK_SOURCE_ID "
				+ "WHERE S.PK_SOURCE_ID = ?";

		return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(InstrumentObligationDTO.class), sourceId);
		
		
	}

	@Override
	public void update(InstrumentFactsheetDTO instrumentFactsheetRec)  throws ResourceNotFoundException {
		instrumentFactsheetRec = validateDates(instrumentFactsheetRec);

		String update = "UPDATE T_SOURCE SET TITLE=?, ALIAS=?, "
                + "SOURCE_CODE=?, URL=?, CELEX_REF=?, ISSUED_BY_URL=?, "
                + "VALID_FROM=?, ABSTRACT=?, COMMENT=?, EC_ENTRY_INTO_FORCE=?, EC_ACCESSION=?, "
                + "SECRETARIAT=?, SECRETARIAT_URL=?, TERMINATE=?, FK_CLIENT_ID=? , LAST_UPDATE=? "
                + "WHERE PK_SOURCE_ID=?";

        Calendar calendar = Calendar.getInstance();
        java.sql.Date ourJavaDateObject = new java.sql.Date(calendar.getTime().getTime());
        
		jdbcTemplate.update(update,
				instrumentFactsheetRec.getSourceTitle(),
				instrumentFactsheetRec.getSourceAlias(),
				instrumentFactsheetRec.getSourceCode(),
				instrumentFactsheetRec.getSourceUrl(),
				instrumentFactsheetRec.getSourceCelexRef(),
				instrumentFactsheetRec.getSourceIssuedByUrl(),
				instrumentFactsheetRec.getSourceValidFrom(),
				instrumentFactsheetRec.getSourceAbstract(),
				instrumentFactsheetRec.getSourceComment(),
				instrumentFactsheetRec.getSourceEcEntryIntoForce(),
				instrumentFactsheetRec.getSourceEcAccession(),
				instrumentFactsheetRec.getSourceSecretariat(),
				instrumentFactsheetRec.getSourceSecretariatUrl(),
				instrumentFactsheetRec.getSourceTerminate(),
				instrumentFactsheetRec.getClientId(),
				ourJavaDateObject,
				instrumentFactsheetRec.getSourceId());	
		
		update = "UPDATE T_CLIENT_SOURCE_LNK "
				+ "SET FK_CLIENT_ID=? "
				+ "WHERE FK_SOURCE_ID=?";
		
		jdbcTemplate.update(update,
				instrumentFactsheetRec.getClientId(),
				instrumentFactsheetRec.getSourceId());
		
		String delete = "DELETE FROM T_SOURCE_LNK WHERE FK_SOURCE_CHILD_ID = ? "
				+ "AND CHILD_TYPE='S' AND PARENT_TYPE='S'";
		jdbcTemplate.update(delete, instrumentFactsheetRec.getSourceId());
		
		if (instrumentFactsheetRec.getSourceLnkFKSourceParentId() != -1) {
			
			String insert = "INSERT INTO T_SOURCE_LNK (FK_SOURCE_CHILD_ID, CHILD_TYPE, "
					+ "FK_SOURCE_PARENT_ID, PARENT_TYPE) "
					+ "VALUES (?,?,?,?)";
			
			jdbcTemplate.update(insert,
					instrumentFactsheetRec.getSourceId(),
					"S",
					instrumentFactsheetRec.getSourceLnkFKSourceParentId(),
					"S");
		}
		
		deleteClassifications(instrumentFactsheetRec.getSourceId());
		insertClassifications(instrumentFactsheetRec);
		
	}

	private ClientDTO getClient(Integer sourceId) {
		String query = "SELECT C.PK_CLIENT_ID AS clientId, C.CLIENT_NAME AS name, C.CLIENT_URL AS url "
				+ "FROM T_CLIENT AS C "
				+ "INNER JOIN T_CLIENT_SOURCE_LNK AS CSL "
				+ "ON C.PK_CLIENT_ID=CSL.FK_CLIENT_ID "
				+ "INNER JOIN T_SOURCE AS S "
				+ "ON S.PK_SOURCE_ID=CSL.FK_SOURCE_ID "
				+ "WHERE S.PK_SOURCE_ID=? AND CSL.STATUS='M'";


		return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(ClientDTO.class), sourceId);
	}

	@Override
	public Integer insert(InstrumentFactsheetDTO instrumentFactsheetRec) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		
		SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
		jdbcInsert.withTableName("T_SOURCE").usingGeneratedKeyColumns(
                "PK_SOURCE_ID");
		
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("TITLE", instrumentFactsheetRec.getSourceTitle());
		parameters.put("ALIAS", instrumentFactsheetRec.getSourceAlias());
		parameters.put("SOURCE_CODE", instrumentFactsheetRec.getSourceCode());
		parameters.put("TERMINATE", instrumentFactsheetRec.getSourceTerminate());
		parameters.put("URL", instrumentFactsheetRec.getSourceUrl());
		parameters.put("CELEX_REF", instrumentFactsheetRec.getSourceCelexRef());
		parameters.put("ISSUED_BY_URL", instrumentFactsheetRec.getSourceIssuedByUrl());
		parameters.put("VALID_FROM", instrumentFactsheetRec.getSourceValidFrom());
		parameters.put("ABSTRACT", instrumentFactsheetRec.getSourceAbstract());
		parameters.put("COMMENT", instrumentFactsheetRec.getSourceComment());
		parameters.put("EC_ENTRY_INTO_FORCE", instrumentFactsheetRec.getSourceEcEntryIntoForce());
		parameters.put("EC_ACCESSION", instrumentFactsheetRec.getSourceEcAccession());
		parameters.put("SECRETARIAT", instrumentFactsheetRec.getSourceSecretariat());
		parameters.put("SECRETARIAT_URL", instrumentFactsheetRec.getSourceSecretariatUrl());
		parameters.put("FK_CLIENT_ID", instrumentFactsheetRec.getClientId());
		parameters.put("FK_TYPE_ID", null); //todo WHY unused?
		parameters.put("LEGAL_NAME", "");
	   
		// java.sql.Date
        Calendar calendar = Calendar.getInstance();
        java.sql.Date ourJavaDateObject = new java.sql.Date(calendar.getTime().getTime());
        
        parameters.put("LAST_UPDATE",ourJavaDateObject);
	       
		Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(
                parameters));
		Integer sourceId = key.intValue();

		insertClient(sourceId, instrumentFactsheetRec.getClientId());
		insertParent(sourceId, instrumentFactsheetRec.getSourceLnkFKSourceParentId());
		instrumentFactsheetRec.setSourceId(sourceId);
		insertClassifications(instrumentFactsheetRec);
		
		return sourceId;
	}
	
	
	private void insertClient(Integer sourceId, Integer clientId) {

		String query = "INSERT INTO T_CLIENT_SOURCE_LNK (FK_CLIENT_ID, FK_SOURCE_ID, STATUS) "
				+ "VALUES (?,?,?)";
		
		jdbcTemplate.update(query,
				clientId,
				sourceId,
				"M"
				);
	}	
	

	private void insertParent(Integer sourceId, Integer sourceLnkFKSourceParentId) {
		if (sourceLnkFKSourceParentId != -1) {
			String query = "INSERT INTO T_SOURCE_LNK (FK_SOURCE_CHILD_ID, CHILD_TYPE, FK_SOURCE_PARENT_ID, PARENT_TYPE) "
					+ "VALUES (?,?,?,?)";
			
			jdbcTemplate.update(query,
					sourceId,
					"S",
					sourceLnkFKSourceParentId,
					"S"
					);
		}
		
	}

	@Override
	public List<InstrumentFactsheetDTO> getAllInstruments() {
		String query = "SELECT T_SOURCE.PK_SOURCE_ID AS sourceId, ALIAS AS sourceAlias "                
                + "FROM T_SOURCE ORDER BY sourceAlias";
		return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(InstrumentFactsheetDTO.class));
	}
	

	private InstrumentDTO getParent(Integer sourceId) {
		InstrumentDTO instrumentDTORec = null;
		List<InstrumentDTO> instruments = null;
		String query = "SELECT PK_SOURCE_ID AS sourceId, ALIAS AS sourceAlias "
				+ "FROM T_SOURCE WHERE PK_SOURCE_ID=("
				+ "SELECT FK_SOURCE_PARENT_ID FROM T_SOURCE_LNK WHERE FK_SOURCE_CHILD_ID=? "
				+ "AND CHILD_TYPE='S' AND PARENT_TYPE='S')";
		instruments = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(InstrumentDTO.class), sourceId);
		if (instruments != null && !instruments.isEmpty()) {
			instrumentDTORec = instruments.get(0);
		}

		return instrumentDTORec;
	}
	
	
	private List<InstrumentDTO> getRelatedInstruments(Integer sourceId) {
		List<InstrumentDTO> relatedInstruments = new ArrayList<>();
		InstrumentDTO relatedInstrument = null;
		List<InstrumentFactsheetDTO> instruments = null;
		
		String query = "SELECT FK_SOURCE_CHILD_ID AS sourceLnkFKSourceChildId "
				+ "FROM T_SOURCE_LNK WHERE FK_SOURCE_PARENT_ID=? "
				+ "AND CHILD_TYPE='S' AND PARENT_TYPE='S'";

		instruments = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(InstrumentFactsheetDTO.class), sourceId);
		if (instruments != null && !instruments.isEmpty()) {
			
			for (InstrumentFactsheetDTO instrument : instruments) {
				query = "SELECT PK_SOURCE_ID AS sourceId, ALIAS AS sourceAlias "
						+ "FROM T_SOURCE WHERE PK_SOURCE_ID=?";
				relatedInstrument = jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(InstrumentDTO.class), instrument.getSourceLnkFKSourceChildId());
				relatedInstruments.add(relatedInstrument);
			}
			
		}
		return relatedInstruments;
		
	}
	
	private List<InstrumentClassificationDTO> getInstrumentClassifications(Integer sourceId) {
		List<InstrumentClassificationDTO>  classifications = null;
		String query = "SELECT SC.PK_CLASS_ID AS classId, SC.CLASSIFICATOR AS classificator, SC.CLASS_NAME AS className "
				+ "FROM T_SOURCE_CLASS SC INNER JOIN T_SOURCE_LNK AS SL "
				+ "ON SC.PK_CLASS_ID = SL.FK_SOURCE_PARENT_ID "
				+ "INNER JOIN T_SOURCE AS S "
				+ "ON SL.FK_SOURCE_CHILD_ID = S.PK_SOURCE_ID "
				+ "WHERE S.PK_SOURCE_ID=? AND SL.CHILD_TYPE='S' AND SL.PARENT_TYPE='C'";
		classifications = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(InstrumentClassificationDTO.class), sourceId);
		return classifications;		
	}
	
	@Override
	public List<InstrumentClassificationDTO> getAllClassifications() {
		List<InstrumentClassificationDTO>  classifications = null;
		String query = "SELECT PK_CLASS_ID AS classId, CLASSIFICATOR AS classificator, CLASS_NAME AS className "
				+ "FROM T_SOURCE_CLASS WHERE CLASS_NAME != '' ORDER BY CLASSIFICATOR";
		classifications = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(InstrumentClassificationDTO.class));
		return classifications;
	}
	
	@Override
	public void insertClassifications(InstrumentFactsheetDTO instrumentFactsheetRec) {
		String query = "INSERT INTO T_SOURCE_LNK (FK_SOURCE_CHILD_ID, FK_SOURCE_PARENT_ID, CHILD_TYPE, PARENT_TYPE) "
				+ "VALUES (?,?,?,?)";

		for (String classificationId : instrumentFactsheetRec.getSelectedClassifications()) {
			jdbcTemplate.update(query,
					instrumentFactsheetRec.getSourceId(),
					Integer.parseInt(classificationId),
					"S",
					"C"
					);
		}
	}
	

	public void deleteClassifications(Integer sourceId) {
		String delete = "DELETE FROM T_SOURCE_LNK WHERE FK_SOURCE_CHILD_ID = ? " 
				+ "AND CHILD_TYPE='S' AND PARENT_TYPE='C'";
		jdbcTemplate.update(delete,
				sourceId
				);
	}
		
	@Override
	public String getHierarchy(Integer id, boolean hasParent, String mode) {
		String newLine = "\n";
		StringBuilder ret = new StringBuilder();
		String query = "SELECT SC.PK_CLASS_ID AS classId, SC.CLASSIFICATOR AS classificator, SC.CLASS_NAME AS className, SL.FK_SOURCE_PARENT_ID AS parentId "
				+ "FROM T_SOURCE_CLASS SC_PARENT, T_SOURCE_CLASS SC, T_SOURCE_LNK SL "
				+ "WHERE SC_PARENT.PK_CLASS_ID=? AND SC_PARENT.PK_CLASS_ID=SL.FK_SOURCE_PARENT_ID "  // todo: this looks like a bug - joining pk_class_id with fk_source_parent_id
				+ "AND SL.FK_SOURCE_CHILD_ID=SC.PK_CLASS_ID AND SL.CHILD_TYPE='C' AND SL.PARENT_TYPE='C' "
				+ "ORDER BY SC.CLASSIFICATOR";
		List<InstrumentsListDTO> intrumentsListDTOs = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(InstrumentsListDTO.class), id);
		String style = "category";
        if (!hasParent) {
            style = "topcategory";
        }
        
        if (intrumentsListDTOs != null && !intrumentsListDTOs.isEmpty()) {
        	 ret.append("<ul class='").append(style).append("'>").append(newLine);
        	 for (InstrumentsListDTO intrumentsListDTO : intrumentsListDTOs) {
        		 ret.append("<li>").append(newLine);
        		 if (hasParent) {
        			 if (intrumentsListDTO.getClassificator() != null && !intrumentsListDTO.getClassificator().isEmpty()) {
        				 ret.append(intrumentsListDTO.getClassificator()).append("&#160;").append(newLine);
        			 }
        		 }
    			 ret.append("<a href='instruments?id=").append(intrumentsListDTO.getClassId());
    			 if ("X".equals(mode)) {
    				 ret.append("&amp;mode=X");
    			 }
    			 ret.append("'>").append(intrumentsListDTO.getClassName()).append("</a>").append(newLine);
    			 ret.append(getHierarchy(intrumentsListDTO.getClassId(), true, mode));
    			 ret.append("</li>").append(newLine);
        	 }
        	 ret.append("</ul>").append(newLine);
        }
		
        return ret.toString();		
	}

	@Override
	public InstrumentsListDTO getHierarchyInstrument(Integer id) {
		String query = "SELECT SC.PK_CLASS_ID AS classId, SC.CLASSIFICATOR AS classificator, SC.CLASS_NAME AS className, "
				+ "SL.FK_SOURCE_PARENT_ID AS parentId FROM T_SOURCE_CLASS SC, T_SOURCE_LNK SL "
				+ "WHERE SC.PK_CLASS_ID=? AND SC.PK_CLASS_ID=SL.FK_SOURCE_CHILD_ID AND SL.CHILD_TYPE='C' AND SL.PARENT_TYPE='C' "
				+ "ORDER BY SC.CLASSIFICATOR";
		List<InstrumentsListDTO> hierarchyInstrument = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(InstrumentsListDTO.class), id);
		if (hierarchyInstrument != null && !hierarchyInstrument.isEmpty()) {
			return hierarchyInstrument.get(0);
		} else {
			return null;
		}
	}
	
	@Override
	public List<HierarchyInstrumentDTO> getHierarchyInstruments(Integer id) {
		String query = "SELECT S1.PK_SOURCE_ID AS sourceId, S1.ALIAS AS sourceAlias, S1.url AS sourceUrl, "
				+ "S2.PK_SOURCE_ID AS sourceParentId, S2.ALIAS AS sourceParentAlias "
				+ "FROM T_SOURCE_LNK AS SL1 "
				+ "INNER JOIN T_SOURCE as S1 ON SL1.FK_SOURCE_CHILD_ID = S1.PK_SOURCE_ID "
				+ "LEFT JOIN T_SOURCE_LNK AS SL2 ON S1.PK_SOURCE_ID = SL2.FK_SOURCE_CHILD_ID AND SL2.PARENT_TYPE = 'S' "
				+ "LEFT JOIN T_SOURCE AS S2 ON SL2.FK_SOURCE_PARENT_ID = S2.PK_SOURCE_ID "
				+ "WHERE SL1.PARENT_TYPE = 'C' AND SL1.FK_SOURCE_PARENT_ID=? "
				+ "ORDER BY S1.ALIAS";
		return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(HierarchyInstrumentDTO.class), id);
	}
	
	private InstrumentFactsheetDTO validateDates(InstrumentFactsheetDTO instrumentFactsheetRec) {
		if ("".equals(instrumentFactsheetRec.getSourceValidFrom())) {
			instrumentFactsheetRec.setSourceValidFrom(null);
		} 
		
		if ("".equals(instrumentFactsheetRec.getSourceEcEntryIntoForce())) {
			instrumentFactsheetRec.setSourceEcEntryIntoForce(null);
		} 
		
		if ("".equals(instrumentFactsheetRec.getSourceEcAccession())) {
			instrumentFactsheetRec.setSourceEcAccession(null);
		} 
		return instrumentFactsheetRec;
		
	}
	
	@Override
	public void delete(Integer sourceId) {

		String delete = "DELETE FROM T_SOURCE WHERE PK_SOURCE_ID=?";
		jdbcTemplate.update(delete, sourceId);
		
		delete = "DELETE FROM T_CLIENT_SOURCE_LNK WHERE FK_SOURCE_ID=?";
		jdbcTemplate.update(delete, sourceId);
		
		delete = "DELETE FROM T_SOURCE_LNK WHERE FK_SOURCE_CHILD_ID=?";
		jdbcTemplate.update(delete, sourceId);
	}

}
