package eionet.rod.dao;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.context.ApplicationContextException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import eionet.rod.model.ClientDTO;
import eionet.rod.model.Issue;
import eionet.rod.model.Obligations;
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
	
	public ObligationsDaoImpl() {
	}
	
	private JdbcTemplate jdbcTemplate;
	private SimpleJdbcInsert jdbcInsert;
	
	@Resource
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.jdbcInsert = new SimpleJdbcInsert(dataSource);
	}

	@Override
	public List<Obligations> findAll() {
		String query = "SELECT PK_RA_ID AS obligationId, TITLE AS oblTitle, DESCRIPTION AS description "
                + "FROM T_OBLIGATION "
                + "ORDER BY title";
		
		String queryCount = "SELECT Count(*) as obligationId "
                + "FROM T_OBLIGATION ";
		
		try {
		
			Integer countObligation = jdbcTemplate.queryForObject(queryCount, Integer.class);
		
			if (countObligation == 0) {
				throw new ResourceNotFoundException("No data in the database");
			}else {
			
				return jdbcTemplate.query(query, new BeanPropertyRowMapper<Obligations>(Obligations.class));

			}
			
		} catch (DataAccessException e) {
			throw new ResourceNotFoundException("No data in the database");
		}
	 
	}
		
	/**
	 * 
	 * @param dlCase
	 * @param date1
	 * @param date2
	 * @return
	 */
	private String handleDeadlines(String deadlineCase) {
        String ret = "";
        String date1 ="";
        String date2 = "";
        if ( deadlineCase != null ) { //selected in combo
            Calendar today = Calendar.getInstance();
            //next month
            if (deadlineCase.equals("1")) {
                date1=getDate(today);
                today.add(Calendar.MONTH, 1);
                date2=getDate(today);
            }
            //next 3 months
            else if (deadlineCase.equals("2")) {
                date1=getDate(today);
                today.add(Calendar.MONTH, 3);
                date2=getDate(today);
            }
            //next 6 months
            else if (deadlineCase.equals("3")) {
                date1=getDate(today);
                today.add(Calendar.MONTH, 6);
                date2=getDate(today);
            }
            //passed
            else if (deadlineCase.equals("4")) {
                date2=getDate(today);
                today.add(Calendar.MONTH, -3);
                date1=getDate(today);
            }
        }

        if (deadlineCase == null || !deadlineCase.equals("0")) {
            date1=cnvDate(date1);
            date2=cnvDate(date2);
            ret = " ((NEXT_DEADLINE >= '" + date1 + "' AND NEXT_DEADLINE <= '" + date2 + "') OR (NEXT_DEADLINE2 >= '" + date1 + "' AND NEXT_DEADLINE2 <= '" + date2 + "')) ";
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
	
	@Override
	public List<Obligations> findObligationList(String clientId, String issueId, String spatialId, String terminate, String deadlineCase) {
		String query = "SELECT distinct OB.PK_RA_ID AS obligationId, OB.TITLE AS oblTitle, OB.DESCRIPTION AS description, "
				+ "SO.PK_SOURCE_ID as sourceId, SO.TITLE AS sourceTitle, SO.ALIAS as sourceAlias, "
				+ "OB.NEXT_DEADLINE as nextDeadline, "
				//+ "CASE WHEN OB.NEXT_DEADLINE THEN (DATE_FORMAT(OB.NEXT_DEADLINE, '%d/%m/%Y')) ELSE '' END as nextDeadline, "
				//+ "IF(OB.NEXT_DEADLINE, DATE_FORMAT(OB.NEXT_DEADLINE, '%d/%m/%Y'), '') as nextDeadline, "
				+ "CL.PK_CLIENT_ID as clientId, CL.CLIENT_NAME as clientName, RRO.ROLE_ID AS respRoleId, RRO.ROLE_NAME AS respRoleName, OB.NEXT_REPORTING as nextReporting, OB.FK_DELIVERY_COUNTRY_IDS as hasDelivery "
				+ "FROM T_OBLIGATION OB "
                + "LEFT JOIN T_SOURCE SO ON SO.PK_SOURCE_ID = OB.FK_SOURCE_ID "
                + "LEFT JOIN T_CLIENT_OBLIGATION_LNK CLK ON CLK.STATUS='M' AND CLK.FK_RA_ID=OB.PK_RA_ID " 
                + "LEFT JOIN T_CLIENT CL ON CLK.FK_CLIENT_ID=CL.PK_CLIENT_ID "
				+ "LEFT JOIN T_RASPATIAL_LNK RAS ON RAS.FK_RA_ID=OB.PK_RA_ID "
				+ "LEFT JOIN T_ROLE RRO ON RRO.ROLE_ID=OB.RESPONSIBLE_ROLE "
				+ "LEFT JOIN T_RAISSUE_LNK RAI ON RAI.FK_RA_ID=OB.PK_RA_ID ";
				if ((!issueId.equals("0") && issueId != null) || (!clientId.equals("0")  && clientId != null) || (!spatialId.equals("0")  && spatialId != null) || (terminate != null && !terminate.equals("")) || (!deadlineCase.equals("0")  && deadlineCase != null)) {
					query += "WHERE ";
				}
				if (!clientId.equals("0")  && clientId != null) {
					query += "CLK.FK_CLIENT_ID = " + clientId;
				}
				if (!spatialId.equals("0")  && spatialId != null) {
					if (!clientId.equals("0")  && clientId != null) {
						query += " and ";	
					}
					query += "RAS.FK_SPATIAL_ID = " + spatialId;
				}
				if (!issueId.equals("0")  && issueId != null) {
					if (!clientId.equals("0")  && clientId != null || (!spatialId.equals("0")  && spatialId != null)) {
						query += " and ";	
					}
					query += "RAI.FK_ISSUE_ID = " + issueId;
				}
				if (terminate != null && !terminate.equals(""))  {
					if (!clientId.equals("0")  && clientId != null || (!spatialId.equals("0")  && spatialId != null) || (!issueId.equals("0")  && issueId != null)) {
						query += " and ";	
					}
					query += "OB.Terminate = '" + terminate + "'";
				}
				if (!deadlineCase.equals("0")  && deadlineCase != null) {
					if (!clientId.equals("0")  && clientId != null || (!spatialId.equals("0")  && spatialId != null) || (!issueId.equals("0")  && issueId != null) || (terminate != null && !terminate.equals(""))) {
						query += " and ";	
					}
					String queryDeadline = handleDeadlines(deadlineCase) ;
					query += queryDeadline;
				}
                query += " ORDER BY oblTitle";
		
//		String queryCount = "SELECT COUNT(DISTINCT OB.PK_RA_ID) as obligationId "
//				+ "FROM T_OBLIGATION OB "
//                + "LEFT JOIN T_SOURCE SO ON SO.PK_SOURCE_ID = OB.FK_SOURCE_ID "
//                + "LEFT JOIN T_CLIENT_OBLIGATION_LNK CLK ON CLK.STATUS='M' AND CLK.FK_RA_ID=OB.PK_RA_ID " 
//                + "LEFT JOIN T_CLIENT CL ON CLK.FK_CLIENT_ID=CL.PK_CLIENT_ID "
//                + "LEFT JOIN T_RASPATIAL_LNK RAS ON RAS.FK_RA_ID=OB.PK_RA_ID "
//                + "LEFT JOIN T_ROLE RRO ON RRO.ROLE_ID=OB.RESPONSIBLE_ROLE "
//                + "LEFT JOIN T_RAISSUE_LNK RAI ON RAI.FK_RA_ID=OB.PK_RA_ID ";
//				if ((!issueId.equals("0") && issueId != null) || (!clientId.equals("0")  && clientId != null) || (!spatialId.equals("0")  && spatialId != null) || (terminate != null && !terminate.equals("")) || (!deadlineCase.equals("0")  && deadlineCase != null)) {
//					queryCount += "WHERE ";
//				}
//				if (!clientId.equals("0")  && clientId != null) {
//					queryCount += "CLK.FK_CLIENT_ID = " + clientId;
//				}
//				if (!spatialId.equals("0")  && spatialId != null) {
//					if (!clientId.equals("0")  && clientId != null) {
//						queryCount += " and ";	
//					}
//					queryCount += "RAS.FK_SPATIAL_ID = " + spatialId;
//				}
//				if (!issueId.equals("0")  && issueId != null) {
//					if (!clientId.equals("0")  && clientId != null || (!spatialId.equals("0")  && spatialId != null)) {
//						queryCount += " and ";	
//					}
//					queryCount += "RAI.FK_ISSUE_ID = " + issueId;
//				}
//				if (terminate != null && !terminate.equals("")) {
//					if (!clientId.equals("0")  && clientId != null || (!spatialId.equals("0")  && spatialId != null) || (!issueId.equals("0")  && issueId != null)) {
//						queryCount += " and ";	
//					}
//					queryCount += "OB.Terminate = '" + terminate + "'";
//				}
//				if (!deadlineCase.equals("0")  && deadlineCase != null) {
//					if (!clientId.equals("0")  && clientId != null || (!spatialId.equals("0")  && spatialId != null) || (!issueId.equals("0")  && issueId != null) || (terminate != null && !terminate.equals(""))) {
//						queryCount += " and ";	
//					}
//					String queryDeadline = handleDeadlines(deadlineCase) ;
//					queryCount += queryDeadline;
//				}
//				
//		
//		try {
//		
//			Integer countObligations = jdbcTemplate.queryForObject(queryCount, Integer.class);
//		
//			if (countObligations == 0) {
//				return null;
//			}else {
//			
//				return jdbcTemplate.query(query, new BeanPropertyRowMapper<Obligations>(Obligations.class));
//
//			}
//			
//		} catch (DataAccessException e) {
//			throw new ResourceNotFoundException("No data in the database");
//		}
		
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<Obligations>(Obligations.class));
	}
	
	
	@Override
	public Obligations findOblId(Integer OblId) throws ApplicationContextException {
		String query = "SELECT OB.PK_RA_ID AS obligationId, OB.TITLE AS oblTitle, OB.DESCRIPTION AS description, "
				+ "OB.EEA_PRIMARY as eeaPrimary, OB.EEA_CORE as eeaCore, OB.FLAGGED as flagged, OB.COORDINATOR as coordinator, OB.COORDINATOR_URL as coordinatorUrl, "
				+ "OB.COORDINATOR_ROLE as coordinatorRole, OB.COORDINATOR_ROLE_SUF as coordinatorRoleSuf, OB.NATIONAL_CONTACT as nationalContact, OB.NATIONAL_CONTACT_URL as nationalContactUrl, OB.TERMINATE as terminate, " 
				+ "OB.NEXT_REPORTING as nextReporting, "
				+ "OB.NEXT_DEADLINE as nextDeadline, "
				//+ "CASE WHEN OB.NEXT_DEADLINE THEN (DATE_FORMAT(OB.NEXT_DEADLINE, '%d/%m/%Y')) ELSE '' END as nextDeadline, "
				//+ "IF(OB.NEXT_DEADLINE, DATE_FORMAT(OB.NEXT_DEADLINE, '%d/%m/%Y'), '') as nextDeadline, "
				+ "OB.REPORT_FREQ_MONTHS as reportFreqMonths, OB.DATE_COMMENTS as dateComments, OB.FORMAT_NAME as formatName, OB.REPORT_FORMAT_URL as reportFormatUrl, "
				+ "OB.REPORTING_FORMAT as reportingFormat, OB.LOCATION_PTR as locationPtr, OB.LOCATION_INFO as locationInfo, OB.DATA_USED_FOR as dataUsedFor, OB.DATA_USED_FOR_URL as dataUsedForUrl, "
				//+ "DATE_FORMAT(OB.VALID_SINCE, '%d/%m/%Y') as validSince, "
				+ "OB.VALID_SINCE as validSince, "
				+ "OB.AUTHORITY as authority, OB.COMMENT as comment, "
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
		
		String queryCount = "SELECT Count(*) as obligationId "
				 + "FROM T_OBLIGATION OB "
	             + "LEFT JOIN T_SOURCE SO ON SO.PK_SOURCE_ID = OB.FK_SOURCE_ID "
	             + "LEFT JOIN T_ROLE CRO ON CRO.ROLE_ID=OB.COORDINATOR_ROLE "
	             + "LEFT JOIN T_ROLE RRO ON RRO.ROLE_ID=OB.RESPONSIBLE_ROLE "
	             + "LEFT JOIN T_CLIENT_OBLIGATION_LNK CLK ON CLK.STATUS='M' AND CLK.FK_RA_ID=OB.PK_RA_ID " 
	             + "LEFT JOIN T_CLIENT CL ON CLK.FK_CLIENT_ID=CL.PK_CLIENT_ID " 
				 + "WHERE PK_RA_ID = ? ";
		
		try {
		
			Integer countObligation = jdbcTemplate.queryForObject(queryCount, Integer.class, OblId);
		
			if (countObligation == 0) {
				throw new ResourceNotFoundException("The obligation you requested with id " + OblId + " was not found in the database");
			}else {
		
				return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<Obligations>(Obligations.class), OblId);

			}
			
		} catch (DataAccessException e) {
			throw new ResourceNotFoundException("The obligation you requested with id " + OblId + " was not found in the database");
		}
		
	}
	/**
	 * 
	 */
	@Override
	 public List<SiblingObligation> findSiblingObligations(Integer siblingoblId) {

	        String query = "SELECT o2.PK_RA_ID as siblingoblId, o2.FK_SOURCE_ID as fkSourceId , o2.TITLE as siblingTitle, o2.AUTHORITY as authority, o2.TERMINATE as terminate "
	        + "FROM T_OBLIGATION o1, T_OBLIGATION o2, T_SOURCE "
	        + "WHERE T_SOURCE.PK_SOURCE_ID=o1.FK_SOURCE_ID AND o1.PK_RA_ID = " + siblingoblId + " AND o2.PK_RA_ID != " + siblingoblId + " AND o2.FK_SOURCE_ID = T_SOURCE.PK_SOURCE_ID "
	        + "ORDER BY o2.TITLE";

	       
	        String queryCount = "SELECT Count(*) as siblingoblId "
	    	        + "FROM T_OBLIGATION o1, T_OBLIGATION o2, T_SOURCE "
	    	        + "WHERE T_SOURCE.PK_SOURCE_ID=o1.FK_SOURCE_ID AND o1.PK_RA_ID = " + siblingoblId + " AND o2.PK_RA_ID != " + siblingoblId + " AND o2.FK_SOURCE_ID = T_SOURCE.PK_SOURCE_ID "
	    	        + "ORDER BY o2.TITLE";

	        
	        try {
	    		
				Integer countObligation = jdbcTemplate.queryForObject(queryCount, Integer.class);
			
				if (countObligation == 0) {
					
					List<SiblingObligation> SiblingObligations = null;
					return SiblingObligations;
					
					//throw new ResourceNotFoundException("The obligation you requested with id " + siblingoblId + " was not found in the database");
				}else {
			
					return jdbcTemplate.query(query, new BeanPropertyRowMapper<SiblingObligation>(SiblingObligation.class));

				}
				
			} catch (DataAccessException e) {
				throw new ResourceNotFoundException("The obligation you requested with id " + siblingoblId + " was not found in the database");
			}
	      
	    }
	
	@Override
	public List<Spatial> findAllCountriesByObligation(Integer ObligationID, String voluntary){
		String query = "SELECT OBSP.FK_SPATIAL_ID AS spatialId, SP.SPATIAL_NAME AS name "
                + "FROM T_SPATIAL SP, T_RASPATIAL_LNK OBSP "
				+ "WHERE SP.PK_SPATIAL_ID = OBSP.FK_SPATIAL_ID "
             	+ "and VOLUNTARY = ? and OBSP.FK_RA_ID = ? "
                + "ORDER BY name";
       
        
        String queryCount = "SELECT Count(*) as spatialId "
                + "FROM T_SPATIAL SP, T_RASPATIAL_LNK OBSP "
				+ "WHERE SP.PK_SPATIAL_ID = OBSP.FK_SPATIAL_ID "
             	+ "and VOLUNTARY = ? and OBSP.FK_RA_ID = ?";
		
		try {
		
			Integer countSpatial = jdbcTemplate.queryForObject(queryCount, Integer.class, voluntary, ObligationID);
		
			if (countSpatial == 0) {
				return null;
			}else {
			
				 return jdbcTemplate.query(query, new BeanPropertyRowMapper<Spatial>(Spatial.class), voluntary, ObligationID);

			}
			
		} catch (DataAccessException e) {
			throw new ResourceNotFoundException("No data in the database");
		}
        
	}
	
	
	@Override
	public List<Issue> findAllIssuesbyObligation(Integer ObligationID){
		String query = "SELECT OBIS.FK_ISSUE_ID AS issueId, TIS.ISSUE_NAME AS issueName "
                + "FROM T_ISSUE TIS, T_RAISSUE_LNK OBIS "
				+ "WHERE TIS.PK_ISSUE_ID = OBIS.FK_ISSUE_ID "
				+ "and OBIS.FK_RA_ID = ? "
                + "ORDER BY issueName";
        
		String queryCount = "SELECT Count(*) as issueId "
				+ "FROM T_ISSUE TIS, T_RAISSUE_LNK OBIS "
				+ "WHERE TIS.PK_ISSUE_ID = OBIS.FK_ISSUE_ID "
				+ "and OBIS.FK_RA_ID = ?";
		
        try {
    		
			Integer countSpatial = jdbcTemplate.queryForObject(queryCount, Integer.class, ObligationID);
		
			if (countSpatial == 0) {
				return null;
			}else {
			
				return jdbcTemplate.query(query, new BeanPropertyRowMapper<Issue>(Issue.class), ObligationID);

			}
			
		} catch (DataAccessException e) {
			throw new ResourceNotFoundException("No data in the database");
		}
        
	}
	
	/**
	 * 
	 */
	public Integer insertObligation(Obligations obligation, List<ClientDTO> allObligationClients, List<Spatial> allObligationCountries,List<Spatial> allObligationVoluntaryCountries, List<Issue> allObligationsIssues) {

    	jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("T_OBLIGATION").usingGeneratedKeyColumns(
                "PK_RA_ID");
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("TITLE", obligation.getOblTitle()); //obl
        parameters.put("FK_SOURCE_ID", Integer.parseInt(obligation.getSourceId())); //obl
        parameters.put("DESCRIPTION", obligation.getDescription()); //obl
        if (obligation.getFirstReporting() != null && obligation.getFirstReporting() != "") {
        	parameters.put("FIRST_REPORTING", RODUtil.str2Date(obligation.getFirstReporting()));
        }else {
        	parameters.put("FIRST_REPORTING", null);
        }
        if (obligation.getValidTo() != null && obligation.getValidTo() != "") {
        	parameters.put("VALID_TO", RODUtil.str2Date(obligation.getValidTo()));
        }else {
        	parameters.put("VALID_TO", null);
        }
        if (obligation.getReportFreqMonths() == "" || obligation.getReportFreqMonths() == null) {
        	parameters.put("REPORT_FREQ_MONTHS", null);
        }else {
        	parameters.put("REPORT_FREQ_MONTHS", Integer.parseInt(obligation.getReportFreqMonths()));
        }
        if (obligation.getNextDeadline() != null && obligation.getNextDeadline() != "") {
        	parameters.put("NEXT_DEADLINE",RODUtil.str2Date(obligation.getNextDeadline()));
        }else {
        	parameters.put("NEXT_DEADLINE",null);
        }
        if (obligation.getNextDeadline2() != null && obligation.getNextDeadline2() != "") {
        	parameters.put("NEXT_DEADLINE2",RODUtil.str2Date(obligation.getNextDeadline2()));
        }else {
        	parameters.put("NEXT_DEADLINE2",null);
        }
        parameters.put("TERMINATE",obligation.getTerminate()); //obl
        if (obligation.getNextReporting() != null && obligation.getNextReporting() != "") {
        	parameters.put("NEXT_REPORTING",RODUtil.str2Date(obligation.getNextReporting()));
        }else {
        	parameters.put("NEXT_REPORTING",null);
        }
        parameters.put("DATE_COMMENTS",obligation.getDateComments());
        parameters.put("FORMAT_NAME",obligation.getFormatName());
        parameters.put("REPORT_FORMAT_URL",obligation.getReportFormatUrl());
        if (obligation.getValidSince() != null && obligation.getValidSince() != "") {
        	parameters.put("VALID_SINCE",RODUtil.str2Date(obligation.getValidSince()));
        }else {
        	parameters.put("VALID_SINCE",null);
        }
        
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
        Integer obligationID = ((Number) key).intValue();
        
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
         insertAllIssues(allObligationsIssues, obligationID);
         
        return obligationID;
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
    public void updateObligations(Obligations obligations, List<ClientDTO> allObligationClients, List<Spatial> allObligationCountries,List<Spatial> allObligationVoluntaryCountries, List<Issue> allObligationsIssues) {
    	
    	Calendar calendar = Calendar.getInstance();
        java.sql.Date ourJavaDateObject = new java.sql.Date(calendar.getTime().getTime());
    	
        if (obligations.getFirstReporting() != null && obligations.getFirstReporting() != "") {
        	obligations.setFirstReporting(RODUtil.str2Date(obligations.getFirstReporting()));
        }else {
        	obligations.setFirstReporting(null);
        }
        if (obligations.getValidTo() != null && obligations.getValidTo() != "") {
        	obligations.setValidTo(RODUtil.str2Date(obligations.getValidTo()));
        }else {
        	obligations.setValidTo(null);
        }
        Integer setReportFreqMonths = null;
        if (obligations.getReportFreqMonths() != "") {
        	setReportFreqMonths = Integer.parseInt(obligations.getReportFreqMonths());
        }
        if (obligations.getNextDeadline() != null && obligations.getNextDeadline() != "") {
        	obligations.setNextDeadline(RODUtil.str2Date(obligations.getNextDeadline()));
        }else {
        	obligations.setNextDeadline(null);
        }
        if (obligations.getNextDeadline2() != null && obligations.getNextDeadline2() != "") {
        	obligations.setNextDeadline2(RODUtil.str2Date(obligations.getNextDeadline2()));
        }else {
        	obligations.setNextDeadline2(null);
        }
        if (obligations.getNextReporting() != null && obligations.getNextReporting() != "") {
        	obligations.setNextReporting(RODUtil.str2Date(obligations.getNextReporting()));
        }else {
        	obligations.setNextReporting(null);
        }
        if (obligations.getValidSince() != null && obligations.getValidSince() != "") {
        	obligations.setValidSince(RODUtil.str2Date(obligations.getValidSince()));
        }else {
        	obligations.setValidSince(null);
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
        insertAllIssues(allObligationsIssues, obligations.getObligationId());
    	
    }
	
    /**
     * Delete all selected countries by obligation
     * @param obligationID
     * @param voluntary
     */
    private void deleteCountries(Integer obligationID, String voluntary) {
    	jdbcTemplate.update("DELETE FROM T_RASPATIAL_LNK where FK_RA_ID = " + obligationID + " and VOLUNTARY = '" + voluntary + "'");
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
	        for (int i= 0; i< allObligationCountries.size() ; i++) {
	       	 jdbcTemplate.update(queryContryN,
	       			 allObligationCountries.get(i).getSpatialId(),
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
    	jdbcTemplate.update("DELETE FROM T_CLIENT_OBLIGATION_LNK where FK_RA_ID = " + obligationID + " and status = '" + status + "'");
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
	        for (int i= 0; i< allObligationClients.size() ; i++) {
	       	 jdbcTemplate.update(queryClientC,
	       			allObligationClients.get(i).getClientId(),
	                    obligationID,
	                    strStatus); 
	        }
    	}
    }
    /**
     * Delete obligation issues
     * @param obligationID
     */
    private void deleteIssues(Integer obligationsId) {
    	jdbcTemplate.update("DELETE FROM T_RAISSUE_LNK where FK_RA_ID = " + obligationsId);
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
	        for (int i= 0; i< allObligationsIssues.size() ; i++) {
	       	 jdbcTemplate.update(queryIssues,
	       			allObligationsIssues.get(i).getIssueId(),
	       			obligationIssueID); 
	        }
    	}
    }
    
}
