package eionet.rod.dao;



import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.annotation.Resource;

import javax.sql.DataSource;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.context.ApplicationContextException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import eionet.rod.model.ClientDTO;
import eionet.rod.model.Issue;
import eionet.rod.model.Obligations;
import eionet.rod.model.Roles;
import eionet.rod.model.SiblingObligation;
import eionet.rod.model.Spatial;
import eionet.rod.util.RODUtil;
import eionet.rod.util.exception.ResourceNotFoundException;


/**
 * @author ycarrasco
 *
 */
@Repository
@Transactional
public class ObligationsDaoImpl implements ObligationsDao {
	
    private static final Log logger = LogFactory.getLog(ObligationsDaoImpl.class);

	public ObligationsDaoImpl() {
	}
	
	private JdbcTemplate jdbcTemplate;
	private SimpleJdbcInsert jdbcInsert;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	
	
	@Resource
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.jdbcInsert = new SimpleJdbcInsert(dataSource);
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * find all obligations
	 */
	@Override
	public List<Obligations> findAll() {
		String query = "SELECT PK_RA_ID AS obligationId, TITLE AS oblTitle, DESCRIPTION AS description "
                + "FROM T_OBLIGATION "
                + "ORDER BY title";

		try {
		    List<Obligations> result = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Obligations.class));

		
			if (result.isEmpty()) {
				throw new ResourceNotFoundException("No data in the database");
			} else {
			    return result;
			}
			
		} catch (DataAccessException e) {
            logger.debug(e, e);
			throw new ResourceNotFoundException("DataAccessException error: " + e, e);
		}
	 
	}
	
	
	/**
	 * cases of deadlines
	 * @param deadlineCase
	 * @param date1
	 * @param date2
	 * @return
	 */
	private String handleDeadlines(String deadlineCase, String date1, String date2) {
        String ret = "";
       
        if ( deadlineCase != null ) { //selected in combo
            Calendar today = Calendar.getInstance();
            //next month
			switch (deadlineCase) {
				case "1":
					date1 = getDate(today);
					today.add(Calendar.MONTH, 1);
					date2 = getDate(today);
					break;
				//next 3 months
				case "2":
					date1 = getDate(today);
					today.add(Calendar.MONTH, 3);
					date2 = getDate(today);
					break;
				//next 6 months
				case "3":
					date1 = getDate(today);
					today.add(Calendar.MONTH, 6);
					date2 = getDate(today);
					break;
				//passed
				case "4":
					date2 = getDate(today);
					today.add(Calendar.MONTH, -3);
					date1 = getDate(today);
					break;
			}
        }

        if (!RODUtil.isNullOrEmpty(deadlineCase) || !"0".equals(deadlineCase)) {
           
        	if (!RODUtil.isNullOrEmpty(date1))
        			date1=cnvDate(date1);
        	if (!RODUtil.isNullOrEmpty(date2))
        		date2=cnvDate(date2);
            
        	if (!RODUtil.isNullOrEmpty(date1) || !RODUtil.isNullOrEmpty(date2)) {
            	
            	ret ="((";
            	String ret2 ="";
            	String ret1 ="";
            	String ret3 = " ) OR ( ";
            	if (!RODUtil.isNullOrEmpty(date1) ) {
            		ret1 += "NEXT_DEADLINE >= :date1 ";
            		ret2 += "NEXT_DEADLINE2 >= :date1 ";
            	}
            	if (!RODUtil.isNullOrEmpty(date2) ) {
            		if(!RODUtil.isNullOrEmpty(ret1))
            			ret1 += " AND ";
            		ret1 += "NEXT_DEADLINE <= :date2 ";
            		if(!RODUtil.isNullOrEmpty(ret2))
            			ret2 += " AND ";
            		ret2 += "NEXT_DEADLINE2 <= :date2 ";
            	}
            	
            	ret += ret1 + ret3 + ret2;
            	
            	ret +="))";
            }
        }

        return ret;
    }
    
    // dd/mm/yyyy -> yyyy-mm-dd
    private String cnvDate(String date ) {
        date = date.substring(6) + "-" + date.substring(3,5) + "-" + date.substring(0,2);
        return date;
    }
    
    //formats Calendar object date to dd/mm/yyyy
    private String getDate(Calendar cal) {
        String day = Integer.toString( cal.get( Calendar.DATE) );
        if (day.length() == 1)
            day  ="0" + day;
        String month = Integer.toString( cal.get( Calendar.MONTH) +1 );
        if (month.length() == 1)
            month  ="0" + month;

        String year = Integer.toString( cal.get( Calendar.YEAR) );

        return day + "/" + month + "/" + year;
    }
	
    
      
    /***
     * find all obligations by issue, country, deadline case and terminated yes or not(non-Javadoc)
     */
	@Override
	public List<Obligations> findObligationList(String clientId, String issueId, String spatialId, String terminate, String deadlineCase, String anmode, String date1, String date2, boolean deadlinePage) throws ResourceNotFoundException {
		
		MapSqlParameterSource params = new MapSqlParameterSource();
		
		String query = "SELECT distinct OB.PK_RA_ID AS obligationId, OB.TITLE AS oblTitle, OB.DESCRIPTION AS description, "
				+ "SO.PK_SOURCE_ID as sourceId, SO.TITLE AS sourceTitle, SO.ALIAS as sourceAlias, SO.SOURCE_CODE as sourceCode, "
				+ "OB.NEXT_DEADLINE as nextDeadline, "
				+ "CL.PK_CLIENT_ID as clientId, CL.CLIENT_NAME as clientName, RRO.ROLE_ID AS respRoleId, RRO.ROLE_NAME AS respRoleName, OB.NEXT_REPORTING as nextReporting ";
				if (deadlinePage) {
					query += ", OB.FK_DELIVERY_COUNTRY_IDS REGEXP CONCAT('.',RAS.FK_SPATIAL_ID,'.') AS hasdelivery "
							+ ", RAS.FK_SPATIAL_ID as deliveryCountryId, "
							+ "(SELECT SPATIAL_NAME FROM T_SPATIAL WHERE T_SPATIAL.PK_SPATIAL_ID = RAS.FK_SPATIAL_ID ) as deliveryCountryName ";
				}
				query+= "FROM "
						+ " T_OBLIGATION OB "
		                + "LEFT JOIN T_SOURCE SO ON SO.PK_SOURCE_ID = OB.FK_SOURCE_ID "
		                + "LEFT JOIN T_CLIENT_OBLIGATION_LNK CLK ON CLK.STATUS='M' AND CLK.FK_RA_ID=OB.PK_RA_ID " 
		                + "LEFT JOIN T_CLIENT CL ON CLK.FK_CLIENT_ID=CL.PK_CLIENT_ID "
						+ "LEFT JOIN T_RASPATIAL_LNK RAS ON RAS.FK_RA_ID=OB.PK_RA_ID "
						+ "LEFT JOIN T_ROLE RRO ON RRO.ROLE_ID=OB.RESPONSIBLE_ROLE "
						+ "LEFT JOIN T_RAISSUE_LNK RAI ON RAI.FK_RA_ID=OB.PK_RA_ID WHERE 1=1 ";

				if (!RODUtil.isNullOrEmpty(clientId) && !"0".equals(clientId)) {
					query += " AND CLK.FK_CLIENT_ID = :client_id ";
					params.addValue("client_id", clientId);
				}
				if (!RODUtil.isNullOrEmpty(spatialId) && !"0".equals(spatialId)) {
					query += " AND RAS.FK_SPATIAL_ID = :spatial_id ";
					params.addValue("spatial_id", spatialId);
				}
				if (!RODUtil.isNullOrEmpty(issueId) && !"0".equals(issueId) && !"NI".equals(issueId)) {
					query += " AND RAI.FK_ISSUE_ID = :rai_issue_id ";
					params.addValue("rai_issue_id", issueId);
				} else if ("NI".equals(issueId)) {
					// todo check if valid
					query += " AND OB.PK_RA_ID NOT IN (SELECT DISTINCT RAI2.FK_RA_ID FROM T_RAISSUE_LNK RAI2) ";
				}
				if (!RODUtil.isNullOrEmpty(terminate) && "N".equals(terminate))  {
					query += " AND OB.Terminate = :ob_terminate ";
					params.addValue("ob_terminate", terminate);
				}
				if (!RODUtil.isNullOrEmpty(deadlineCase) && !"0".equals(deadlineCase) || !RODUtil.isNullOrEmpty(date1) || !RODUtil.isNullOrEmpty(date2)) {
					if (!RODUtil.isNullOrEmpty(date1)) {
						if (RODUtil.readDate(date1) == null) {
							throw new ResourceNotFoundException("Date error: " + date1);
						}
					}
					if (!RODUtil.isNullOrEmpty(date2)) {
						if (RODUtil.readDate(date2) == null) {
							throw new ResourceNotFoundException("Date error: " + date2);
						}
					}
					String queryDeadline = handleDeadlines(deadlineCase, date1, date2) ;
					if (!queryDeadline.isEmpty()) {
						query += "AND " + queryDeadline;
						params.addValue("date1", date1);
						params.addValue("date2", date2);
						// todo solve with all the date issues
					}
					
				}
				if (!RODUtil.isNullOrEmpty(anmode) &&  !"NI".equals(anmode)) {
					if ("C".equals(anmode))
					{
						query += " AND EEA_CORE=1 ";
					}
					if ("P".equals(anmode))
					{
						query += " AND EEA_PRIMARY=1 ";
					}
					if ("F".equals(anmode))
					{
						query += " AND FLAGGED=1 ";
					}
				}

                query += " ORDER BY oblTitle";
		
        try {
        	return namedParameterJdbcTemplate.query(query, params, new BeanPropertyRowMapper<>(Obligations.class));
        } catch (DataAccessException e) {
            logger.debug(e, e);
        	throw new ResourceNotFoundException("DataAccessException error: " + e, e);
		}
	}
	
	
	
	
	/***
	 * Find obligation by id
	 */
	@Override
	public Obligations findOblId(Integer oblId) throws ApplicationContextException {

		String query = "SELECT OB.PK_RA_ID AS obligationId, OB.TITLE AS oblTitle, OB.DESCRIPTION AS description, "
				+ "OB.EEA_PRIMARY as eeaPrimary, OB.EEA_CORE as eeaCore, OB.FLAGGED as flagged, OB.COORDINATOR as coordinator, OB.COORDINATOR_URL as coordinatorUrl, "
				+ "OB.COORDINATOR_ROLE as coordinatorRole, OB.COORDINATOR_ROLE_SUF as coordinatorRoleSuf, OB.NATIONAL_CONTACT as nationalContact, OB.NATIONAL_CONTACT_URL as nationalContactUrl, OB.TERMINATE as terminate, " 
				+ "OB.NEXT_REPORTING as nextReporting, "
				+ "OB.NEXT_DEADLINE as nextDeadline, "
				+ "OB.FIRST_REPORTING as firstReporting, OB.VALID_TO as validTo, OB.NEXT_DEADLINE2 as nextDeadline2, "
				//+ "CASE WHEN OB.NEXT_DEADLINE THEN (DATE_FORMAT(OB.NEXT_DEADLINE, '%d/%m/%Y')) ELSE '' END as nextDeadline, "
				//+ "IF(OB.NEXT_DEADLINE, DATE_FORMAT(OB.NEXT_DEADLINE, '%d/%m/%Y'), '') as nextDeadline, "
				+ "OB.REPORT_FREQ_MONTHS as reportFreqMonths, OB.DATE_COMMENTS as dateComments, OB.FORMAT_NAME as formatName, OB.REPORT_FORMAT_URL as reportFormatUrl, "
				+ "OB.REPORTING_FORMAT as reportingFormat, OB.LOCATION_PTR as locationPtr, OB.LOCATION_INFO as locationInfo, OB.DATA_USED_FOR as dataUsedFor, OB.DATA_USED_FOR_URL as dataUsedForUrl, "
				//+ "DATE_FORMAT(OB.VALID_SINCE, '%d/%m/%Y') as validSince, "
				+ "OB.VALID_SINCE as validSince, "
				+ "OB.AUTHORITY as authority, OB.COMMENT as comment, "
				+ "OB.REPORT_FREQ_DETAIL AS reportFreqDetail, OB.LAST_UPDATE AS lastUpdate, OB.REPORT_FREQ AS reportFreq, OB.LAST_HARVESTED AS lastHarvested, "
				+ "OB.FK_DELIVERY_COUNTRY_IDS AS deliveryCountryId, OB.CONTINOUS_REPORTING AS continousReporting, "
				+ "CRO.ROLE_ID AS coordRoleId, CRO.ROLE_NAME AS coordRoleName, CRO.ROLE_URL AS coordRoleUrl, "
				+ "RRO.ROLE_ID AS respRoleId, RRO.ROLE_NAME AS respRoleName, OB.RESPONSIBLE_ROLE as responsibleRole,OB.RESPONSIBLE_ROLE_SUF as responsibleRoleSuf, "
				+ "SO.PK_SOURCE_ID as sourceId, SO.TITLE AS sourceTitle, SO.ALIAS as sourceAlias, "
				//+ "CLK.FK_CLIENT_ID AS clientLnkFKClientId, CLK.FK_OBJECT_ID as clientLnkFKObjectId, CLK.TYPE as clientLnkType, CLK.STATUS as clientLnkStatus, "
		        + "CL.PK_CLIENT_ID as clientId, CL.CLIENT_NAME as clientName "
                + "FROM T_OBLIGATION OB "
                + "LEFT JOIN T_SOURCE SO ON SO.PK_SOURCE_ID = OB.FK_SOURCE_ID "
                + "LEFT JOIN T_ROLE CRO ON CRO.ROLE_ID=OB.COORDINATOR_ROLE "
                + "LEFT JOIN T_ROLE RRO ON RRO.ROLE_ID=OB.RESPONSIBLE_ROLE "
                + "LEFT JOIN T_CLIENT_OBLIGATION_LNK CLK ON CLK.STATUS='M' AND CLK.FK_RA_ID=OB.PK_RA_ID " 
                + "LEFT JOIN T_CLIENT CL ON CLK.FK_CLIENT_ID=CL.PK_CLIENT_ID " 
				+ "WHERE PK_RA_ID = ? ";
		
		try {
		
			return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(Obligations.class), oblId);

		} catch(EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("The obligation you requested with id " + oblId + " was not found in the database");
		} catch (DataAccessException e) {
            logger.debug(e, e);
			throw new ResourceNotFoundException("DataAccessException error: " + e, e);
		}
		
	}
	/**
	 * find the sibling obligations 
	 */
	@Override
	 public List<SiblingObligation> findSiblingObligations(Integer siblingOblId) {

	        String query = "SELECT o2.PK_RA_ID as siblingoblId, o2.FK_SOURCE_ID as fkSourceId , o2.TITLE as siblingTitle, o2.AUTHORITY as authority, o2.TERMINATE as terminate "
		        + "FROM T_OBLIGATION o1, T_OBLIGATION o2, T_SOURCE "
		        + "WHERE T_SOURCE.PK_SOURCE_ID=o1.FK_SOURCE_ID AND o1.PK_RA_ID = ? AND o2.PK_RA_ID != ? AND o2.FK_SOURCE_ID = T_SOURCE.PK_SOURCE_ID "
		        + "ORDER BY o2.TITLE";

	        try {
	    		
				return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(SiblingObligation.class), siblingOblId, siblingOblId);

			} catch(EmptyResultDataAccessException e) {
	        	return null;
			} catch (DataAccessException e) {
                logger.debug(e, e);
				throw new ResourceNotFoundException("DataAccessException error: " + e, e);
			}
	      
	    }
	
	/**
	 * Find all countries relationed with the obligation
	 */
	@Override
	public List<Spatial> findAllCountriesByObligation(Integer obligationId, String voluntary){
		String query = "SELECT OBSP.FK_SPATIAL_ID AS spatialId, SP.SPATIAL_NAME AS name "
                + "FROM T_SPATIAL SP, T_RASPATIAL_LNK OBSP "
				+ "WHERE SP.PK_SPATIAL_ID = OBSP.FK_SPATIAL_ID "
             	+ "and VOLUNTARY = ? and OBSP.FK_RA_ID = ? "
                + "ORDER BY name";
		
		try {
		
			 return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Spatial.class), voluntary, obligationId);

		} catch(EmptyResultDataAccessException e) {
			return null;
		} catch (DataAccessException e) {
		    logger.debug(e, e);
			throw new ResourceNotFoundException("DataAccessException error: " + e, e);
		}
        
	}
	
	/**
	 * Find issues relationed with the obligation
	 */
	@Override
	public List<Issue> findAllIssuesbyObligation(Integer obligationId){
		String query = "SELECT OBIS.FK_ISSUE_ID AS issueId, TIS.ISSUE_NAME AS issueName "
                + "FROM T_ISSUE TIS, T_RAISSUE_LNK OBIS "
				+ "WHERE TIS.PK_ISSUE_ID = OBIS.FK_ISSUE_ID "
				+ "and OBIS.FK_RA_ID = ? "
                + "ORDER BY issueName";
        
        try {
			return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Issue.class), obligationId);
		} catch(EmptyResultDataAccessException e) {
			return null;
		}catch (DataAccessException e) {
            logger.debug(e, e);
			throw new ResourceNotFoundException("DataAccessException error: " + e, e);
		}
        
	}
	
	/**
	 * Insert obligations
	 * Relation with clients, countries, issues and Other obligations 
	 */
	@Override
	public Integer insertObligation(Obligations obligation, List<ClientDTO> allObligationClients, List<Spatial> allObligationCountries,List<Spatial> allObligationVoluntaryCountries, List<Issue> allSelectedIssues) {
		try {
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			
	    	jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
	        jdbcInsert.withTableName("T_OBLIGATION").usingGeneratedKeyColumns(
	                "PK_RA_ID");
	        Map<String, Object> parameters = new HashMap<>();
	        parameters.put("TITLE", obligation.getOblTitle()); //obl
	        parameters.put("FK_SOURCE_ID", Integer.parseInt(obligation.getSourceId())); //obl
	        parameters.put("DESCRIPTION", obligation.getDescription()); //obl
			parameters.put("FIRST_REPORTING", obligation.getFirstReporting());

			parameters.put("VALID_TO", obligation.getValidTo());

	        if (obligation.getReportFreqMonths().isEmpty() || obligation.getReportFreqMonths() == null) {
	        	parameters.put("REPORT_FREQ_MONTHS", null);
	        }else {
	        	parameters.put("REPORT_FREQ_MONTHS", Integer.parseInt(obligation.getReportFreqMonths()));
	        }

	        parameters.put("NEXT_DEADLINE", obligation.getNextDeadline());
	        parameters.put("NEXT_DEADLINE2", obligation.getNextDeadline2());
	        parameters.put("TERMINATE",obligation.getTerminate()); //obl
	        parameters.put("NEXT_REPORTING",obligation.getNextReporting());
	        parameters.put("DATE_COMMENTS",obligation.getDateComments());
	        parameters.put("FORMAT_NAME",obligation.getFormatName());
	        parameters.put("REPORT_FORMAT_URL",obligation.getReportFormatUrl());
	        parameters.put("VALID_SINCE", obligation.getValidSince());
	        parameters.put("REPORTING_FORMAT",obligation.getReportingFormat());
	        parameters.put("LOCATION_INFO",obligation.getLocationInfo());
	        parameters.put("LOCATION_PTR",obligation.getLocationPtr());
	        parameters.put("DATA_USED_FOR",obligation.getDataUsedFor());
	        parameters.put("DATA_USED_FOR_URL",obligation.getDataUsedForUrl());
	        parameters.put("COORDINATOR_ROLE",obligation.getCoordinatorRole());
	        if (obligation.getCoordinatorRoleSuf() == null) {
	        	parameters.put("COORDINATOR_ROLE_SUF",0); //obl
	        }else {
	        	parameters.put("COORDINATOR_ROLE_SUF",Integer.parseInt(obligation.getCoordinatorRoleSuf())); //obl
	        }
	        parameters.put("COORDINATOR",obligation.getCoordinator());
	        parameters.put("COORDINATOR_URL",obligation.getCoordinatorUrl());
	        parameters.put("RESPONSIBLE_ROLE",obligation.getResponsibleRole());
	        if (obligation.getResponsibleRoleSuf() == null) {
	        	parameters.put("RESPONSIBLE_ROLE_SUF",0); //obl
	        }else {
	        	parameters.put("RESPONSIBLE_ROLE_SUF",Integer.parseInt(obligation.getResponsibleRoleSuf())); //obl
	        }
	        parameters.put("NATIONAL_CONTACT",obligation.getNationalContact());
	        parameters.put("NATIONAL_CONTACT_URL",obligation.getNationalContactUrl());
	        parameters.put("PARAMETERS",obligation.getParameters());
	        parameters.put("EEA_PRIMARY",obligation.getEeaPrimary());
	        parameters.put("EEA_CORE",obligation.getEeaCore());
	        parameters.put("FLAGGED",obligation.getFlagged());
	        parameters.put("OVERLAP_URL",obligation.getOverlapUrl());
	        parameters.put("COMMENT",obligation.getComment());
	        parameters.put("AUTHORITY",obligation.getAuthority());
	        
	     // java.sql.Date
	        Calendar calendar = Calendar.getInstance();
	        java.sql.Date ourJavaDateObject = new java.sql.Date(calendar.getTime().getTime());
	        
	        parameters.put("LAST_UPDATE",ourJavaDateObject);
	        if (obligation.getContinousReporting() == null) {
	        	parameters.put("CONTINOUS_REPORTING","no");
	        }else {
	        	parameters.put("CONTINOUS_REPORTING",obligation.getContinousReporting());
	        }
	        
	        // execute insert
	        Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(
	                parameters));
	     // convert Number to Int using ((Number) key).intValue()
	        Integer obligationID = key.intValue();
	        
	        //Insert client with status = M
	        String queryClientM = "INSERT INTO T_CLIENT_OBLIGATION_LNK (FK_CLIENT_ID, FK_RA_ID, STATUS) "
	                + " VALUES (?,?,?)";
	
	         jdbcTemplate.update(queryClientM,
	                obligation.getClientId(),
	                obligationID,
	                "M");
	              
	         //Insert clients with status = C
	         insertAllClients(allObligationClients,"C",obligationID);
	                          
	        //Insert formal countries with voluntary = N  
	         insertAllObligationsCountries(allObligationCountries, "N", obligationID);
	         
	        //Insert voluntary countries with voluntary = Y
	         insertAllObligationsCountries(allObligationVoluntaryCountries, "Y", obligationID);
	                 
	         //Insert Issues
	         insertAllIssues(allSelectedIssues, obligationID);
	       
	         //insert relations with other obligations
	         insertObligationRelations(obligation.getRelObligationId(),obligation.getOblRelationId(), obligationID);
	       
	         return obligationID;
	         
		}catch (DataAccessException e){
		    logger.debug(e, e);
			throw new ResourceNotFoundException("DataAccessException error: " + e, e);
		}
         
   
    }
	
	/** */
    private static final String updateObligationSQL = "UPDATE T_OBLIGATION SET "
	    + "VALID_SINCE = ?, TITLE = ?, FORMAT_NAME = ?, REPORT_FORMAT_URL = ?, REPORTING_FORMAT = ?,  DATE_COMMENTS = ?, TERMINATE = ?, COMMENT = ?,"
		+ "RESPONSIBLE_ROLE = ?, NEXT_DEADLINE = ?, FIRST_REPORTING = ?, REPORT_FREQ_MONTHS = ?, NEXT_REPORTING = ?, VALID_TO = ?, "
	    + "NEXT_DEADLINE2 = ?, LOCATION_PTR = ?, LOCATION_INFO = ?, DESCRIPTION = ?, RESPONSIBLE_ROLE_SUF = ?, NATIONAL_CONTACT = ?, "
	    + "NATIONAL_CONTACT_URL = ?, COORDINATOR_ROLE = ?, COORDINATOR_ROLE_SUF = ?, COORDINATOR = ?, COORDINATOR_URL = ?, "
	    + "EEA_PRIMARY = ?, EEA_CORE = ?, FLAGGED = ?, AUTHORITY = ?, DATA_USED_FOR = ?, DATA_USED_FOR_URL = ?,"
	    + "CONTINOUS_REPORTING = ?, LAST_UPDATE = ?, "
	    + "FK_SOURCE_ID  = ? "
	    + "WHERE PK_RA_ID = ?";
	/**
	 * 
	 */
    @Override
    public void updateObligations(Obligations obligations, List<ClientDTO> allObligationClients, List<Spatial> allObligationCountries,List<Spatial> allObligationVoluntaryCountries, List<Issue> allSelectedIssues) {
    	
    	Calendar calendar = Calendar.getInstance();
        java.sql.Date ourJavaDateObject = new java.sql.Date(calendar.getTime().getTime());
        Integer setReportFreqMonths = null;
        if (!obligations.getReportFreqMonths().isEmpty()) {
        	setReportFreqMonths = Integer.parseInt(obligations.getReportFreqMonths());
        }
        Integer setCoordinatorRoleSuf = 0;
        if (obligations.getCoordinatorRoleSuf() != null) {
        	setCoordinatorRoleSuf =  Integer.parseInt(obligations.getCoordinatorRoleSuf()); //obl
        }
        Integer setResponsibleRoleSuf = 0;
        if (obligations.getResponsibleRoleSuf() != null) {
        	setResponsibleRoleSuf = Integer.parseInt(obligations.getResponsibleRoleSuf()); //obl
        }
        if (obligations.getContinousReporting() == null) {
        	obligations.setContinousReporting("no");
        }else {
        	obligations.setContinousReporting(obligations.getContinousReporting());
        }
		jdbcTemplate.update(updateObligationSQL,
    		obligations.getValidSince(),
            obligations.getOblTitle(),
            obligations.getFormatName(),
            obligations.getReportFormatUrl(),
            obligations.getReportingFormat(),
            obligations.getDateComments(),
            obligations.getTerminate(),
            obligations.getComment(),
            obligations.getResponsibleRole(),
            obligations.getNextDeadline(),
            obligations.getFirstReporting(),
            setReportFreqMonths,
            obligations.getNextReporting(),
            obligations.getValidTo(),
            obligations.getNextDeadline2(),
            obligations.getLocationPtr(),
            obligations.getLocationInfo(),
            obligations.getDescription(),
            setResponsibleRoleSuf,
            obligations.getNationalContact(),
            obligations.getNationalContactUrl(),
            obligations.getCoordinatorRole(),
            setCoordinatorRoleSuf,
            obligations.getCoordinator(),
            obligations.getCoordinatorUrl(),
            obligations.getEeaPrimary(),
            obligations.getEeaCore(),
            obligations.getFlagged(),
            obligations.getAuthority(),
            obligations.getDataUsedFor(),
            obligations.getDataUsedForUrl(),
            obligations.getContinousReporting(),
            ourJavaDateObject,
            obligations.getSourceId(),
            obligations.getObligationId());
		
		
		//Delete clients with status = M by this ObligationID
		deleteClients(obligations.getObligationId(), "M");
			
		//Delete clients with status = C by this ObligationID
       	deleteClients(obligations.getObligationId(), "C");
       	
       //Delete formal countries with voluntary = N by this ObligationID
        deleteCountries(obligations.getObligationId(), "N");
       	
       	//Delete voluntary countries with voluntary = Y by this ObligationID
        deleteCountries(obligations.getObligationId(), "Y");
       
        //Delete Issues by this obligationID
        deleteIssues(obligations.getObligationId());
        
        //Delete relations with other obligations
        deleteObligationRelations(obligations.getObligationId());
        
		//Insert clients with status = C
        insertAllClients(allObligationClients,"C",obligations.getObligationId());
        
        //Insert client with status = M
        String queryClientM = "INSERT INTO T_CLIENT_OBLIGATION_LNK (FK_CLIENT_ID, FK_RA_ID, STATUS) "
                + " VALUES (?,?,?)";

         jdbcTemplate.update(queryClientM,
                obligations.getClientId(),
                obligations.getObligationId(),
                "M");
        
        //Insert formal countries with voluntary = N  
        insertAllObligationsCountries(allObligationCountries, "N", obligations.getObligationId());
        
        //Insert voluntary countries with voluntary = Y
        insertAllObligationsCountries(allObligationVoluntaryCountries, "Y", obligations.getObligationId());
                
        //Insert Issues
        insertAllIssues(allSelectedIssues, obligations.getObligationId());
        
      	//insert relations with other obligations
        insertObligationRelations(obligations.getRelObligationId(),obligations.getOblRelationId(), obligations.getObligationId());
      
    }
    
    @Override
    public void deleteObligations(String obligations) {
    	String[] listObligations = obligations.split(",");

		for (String listObligation : listObligations) {
			deleteCountries(Integer.parseInt(listObligation), "N");
			deleteCountries(Integer.parseInt(listObligation), "Y");
			deleteClients(Integer.parseInt(listObligation), "M");
			deleteClients(Integer.parseInt(listObligation), "C");
			deleteIssues(Integer.parseInt(listObligation));
			deleteObligationRelations(Integer.parseInt(listObligation));
			jdbcTemplate.update("DELETE FROM T_OBLIGATION where PK_RA_ID = ? ", listObligation);
		}
    	
    	
    	
    }
	
    /**
     * Delete all selected countries by obligation
     * @param obligationID
     * @param voluntary
     */
    private void deleteCountries(Integer obligationID, String voluntary) {
    	String queryDelete ="DELETE FROM T_RASPATIAL_LNK where FK_RA_ID = ? and VOLUNTARY = ?";
    	jdbcTemplate.update(queryDelete, obligationID, voluntary);
    }
    
    
    /**
     * Insert selected countries
     * @param allObligationCountries
     * @param strVoluntary
     * @param obligationID
     */
    private void insertAllObligationsCountries(List<Spatial> allObligationCountries, String strVoluntary, Integer obligationID) {
    	if (allObligationCountries != null) {
	    	String queryContryN = "INSERT INTO T_RASPATIAL_LNK (FK_SPATIAL_ID, FK_RA_ID, VOLUNTARY) "
	                + " VALUES (?,?,?)";
			for (Spatial allObligationCountry : allObligationCountries) {
				jdbcTemplate.update(queryContryN,
						allObligationCountry.getSpatialId(),
						obligationID,
						strVoluntary);
			}
        }
    }
    
    /**
     * Delete obligation Clients by status
     * @param obligationID
     * @param status
     */
    private void deleteClients(Integer obligationID, String status) {
    	String queryDelete="DELETE FROM T_CLIENT_OBLIGATION_LNK where FK_RA_ID = ? and status = ?";
    	jdbcTemplate.update(queryDelete, obligationID,status);
    }
    
    /**
     * Insert all clients selected by obligation
     * @param allObligationClients
     * @param strStatus
     * @param obligationID
     */
    private void insertAllClients(List<ClientDTO> allObligationClients, String strStatus, Integer obligationID) {
    	if (allObligationClients != null){
	    	String queryClientC = "INSERT INTO T_CLIENT_OBLIGATION_LNK (FK_CLIENT_ID, FK_RA_ID, STATUS) "
	                + " VALUES (?,?,?)";
			for (ClientDTO allObligationClient : allObligationClients) {
				jdbcTemplate.update(queryClientC,
						allObligationClient.getClientId(),
						obligationID,
						strStatus);
			}
    	}
    }
    /**
     * Delete obligation issues
     * @param obligationsId
     */
    private void deleteIssues(Integer obligationsId) {
    	String queryDelete ="DELETE FROM T_RAISSUE_LNK where FK_RA_ID = ?";
    	jdbcTemplate.update(queryDelete,obligationsId);
    }
    
    /**
     * Insert all Issues selected by obligation
     * @param allObligationsIssues
     * @param obligationIssueID
     */
    private void insertAllIssues(List<Issue> allObligationsIssues, Integer obligationIssueID) {
    	if (allObligationsIssues != null) {
	    	String queryIssues = "INSERT INTO T_RAISSUE_LNK (FK_ISSUE_ID, FK_RA_ID) "
	                + " VALUES (?,?)";
			for (Issue allObligationsIssue : allObligationsIssues) {
				jdbcTemplate.update(queryIssues,
						allObligationsIssue.getIssueId(),
						obligationIssueID);
			}
    	}
    }
    
    /**
     * Insert Relation with other obligation
     * @param relObligationId
     * @param oblRelationId
     * @param obligationID
     */
    private void insertObligationRelations(Integer relObligationId, String oblRelationId, Integer obligationID) {
    	if (relObligationId != null && !relObligationId.equals(0)) {
    		String queryOblRelation = "INSERT INTO T_OBLIGATION_RELATION (FK_RA_ID,RELATION,FK_RA_ID2) "
	                + " VALUES (?,?,?)";
	       	 jdbcTemplate.update(queryOblRelation,
	       			obligationID,
	       			oblRelationId,
	       			relObligationId); 
    	}
    }
    
    /**
     * Delete Relation with other obligation
     * @param obligationID
     */
    private void deleteObligationRelations(Integer obligationID) {

    		String queryOblRelation = "DELETE FROM T_OBLIGATION_RELATION WHERE FK_RA_ID = ?";
	       	 jdbcTemplate.update(queryOblRelation,
	       			obligationID); 

    }
    /**
     * Find relations with other obligations
     * @param obligationId
     */
    @Override
    public Obligations findObligationRelation(Integer obligationId) {
    	String query = "SELECT OBR.FK_RA_ID2 AS relObligationId, OBR.RELATION AS oblRelationId, OB.TITLE AS oblRelationTitle "
                + "FROM T_OBLIGATION_RELATION OBR INNER JOIN T_OBLIGATION OB "
                + "ON OBR.FK_RA_ID2=OB.PK_RA_ID "
    			+ "WHERE OBR.FK_RA_ID = ? ";

		try {
			return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(Obligations.class), obligationId);
		} catch(EmptyResultDataAccessException e) {
			Obligations o = new Obligations();
			o.setRelObligationId(0);
			o.setOblRelationId("0");
			o.setOblRelationTitle("");
			return o;

			// todo try null here
		}catch (DataAccessException e) {
		    logger.debug(e, e);
			throw new ResourceNotFoundException("DataAccessException error: " + e, e);
		}
    }
    
    //ROLES
    private static final String qResponsibleRole =
            "SELECT DISTINCT RESPONSIBLE_ROLE as roleName "
            + "FROM T_OBLIGATION "
            + "WHERE RESPONSIBLE_ROLE IS NOT NULL AND RESPONSIBLE_ROLE <> '' ";

        private static final String qCountryResponsibleRole =
            "SELECT DISTINCT a.RESPONSIBLE_ROLE AS roleName "
            + "FROM T_OBLIGATION a, T_SPATIAL s,  T_RASPATIAL_LNK sl "
            + "WHERE  a.RESPONSIBLE_ROLE_SUF=1 "
            + "AND sl.FK_RA_ID=a.PK_RA_ID "
            + "AND sl.FK_SPATIAL_ID = s.PK_SPATIAL_ID "
            + "AND a.RESPONSIBLE_ROLE IS NOT NULL "
            + "AND a.RESPONSIBLE_ROLE <> '' "
            + "AND s.SPATIAL_TYPE = 'C' "
            + "AND s.SPATIAL_TWOLETTER IS NOT NULL "
            + "AND TRIM(s.SPATIAL_TWOLETTER) <> '' ";

        private static final String qCoordinatorRole =
            "SELECT DISTINCT COORDINATOR_ROLE as roleName "
            + "FROM T_OBLIGATION "
            + "WHERE COORDINATOR_ROLE IS NOT NULL "
            + "AND COORDINATOR_ROLE <> '' ";

        private static final String qCountryCoordinatorRole =
            "SELECT DISTINCT a.COORDINATOR_ROLE as roleName "
            + "FROM T_OBLIGATION a, T_SPATIAL s,  T_RASPATIAL_LNK sl "
            + "WHERE  a.COORDINATOR_ROLE_SUF=1 "
            + "AND sl.FK_RA_ID=a.PK_RA_ID "
            + "AND sl.FK_SPATIAL_ID = s.PK_SPATIAL_ID "
            + "AND a.COORDINATOR_ROLE IS NOT NULL "
            + "AND a.COORDINATOR_ROLE <> '' "
            + "AND s.SPATIAL_TYPE = 'C' "
            + "AND s.SPATIAL_TWOLETTER IS NOT NULL AND "
            + "TRIM(s.SPATIAL_TWOLETTER) <> '' ";

        private static String[] respRolesQueries = { qResponsibleRole, qCountryResponsibleRole, qCoordinatorRole, qCountryCoordinatorRole };

    
        public List<Roles> getRespRoles() {
        	List<Roles> rolesAdd = new ArrayList<>();
        	try {
				for (String respRolesQuery : respRolesQueries) {
					rolesAdd.addAll(jdbcTemplate.query(respRolesQuery, new BeanPropertyRowMapper<>(Roles.class)));
				}
        	}catch (Exception e) {
        	    logger.debug(e, e);
			}
        	return rolesAdd;
        }
                      
 
    /**
	 * Find all clients relationed with the obligation
	 */
    @Override
    public List<ClientDTO> findAllClientsByObligation(Integer obligationID) {
    	String query = "SELECT CL.PK_CLIENT_ID AS clientId, CL.CLIENT_NAME AS name "
    			+ "FROM T_CLIENT AS CL INNER JOIN T_CLIENT_OBLIGATION_LNK AS COL "
    			+ "ON CL.PK_CLIENT_ID = COL.FK_CLIENT_ID "
    			+ "WHERE COL.FK_RA_ID=?";

			return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(ClientDTO.class), obligationID);
    }	   
}
