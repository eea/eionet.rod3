package eionet.rod.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResult;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
//import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
import org.apache.commons.lang.StringUtils;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eionet.rod.model.Delivery;

import eionet.rod.util.RODUtil;
import eionet.rod.util.exception.ResourceNotFoundException;
import eionet.rod.util.exception.ServiceException;


@Repository
@Transactional
public class DeliveryDaoImpl implements DeliveryDao{
	
	private Log logger = LogFactory.getLog(DeliveryDaoImpl.class);
	
	private static String obligationsPrefix = "/obligations/";
    private static String spatialsPrefix = "/spatial/";
	
	public DeliveryDaoImpl() {
	}
	
	private JdbcTemplate jdbcTemplate;
	
	@Resource
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public List<Delivery> getAllDelivery(String actDetailsId, String spatialId) throws ResourceNotFoundException {
		
		String query =  "SELECT T_DELIVERY.FK_RA_ID as deliveryFKObligationId, T_DELIVERY.FK_SPATIAL_ID as deliveryFKSpatialId, T_DELIVERY.TITLE as deliveryTitle, "
                +"T_DELIVERY.DELIVERY_URL as deliveryUrl, T_DELIVERY.UPLOAD_DATE as deliveryUploadDate, T_DELIVERY.TYPE AS deliveryType, T_DELIVERY.FORMAT as deliveryFormat, T_DELIVERY.COVERAGE as deliveryCoverage, T_DELIVERY.COVERAGE_NOTE as deliveryCoverageNote, "
                + "T_OBLIGATION.PK_RA_ID, T_OBLIGATION.FK_SOURCE_ID, T_OBLIGATION.TITLE AS OBLIGATION_TITLE, T_OBLIGATION.REPORT_FREQ_MONTHS, "
                + "T_OBLIGATION.TERMINATE, T_OBLIGATION.NEXT_DEADLINE, T_OBLIGATION.REPORT_FORMAT_URL, T_OBLIGATION.RESPONSIBLE_ROLE, "
                + "T_OBLIGATION.FORMAT_NAME, T_OBLIGATION.FK_DELIVERY_COUNTRY_IDS, "
                + "T_SPATIAL.PK_SPATIAL_ID as spatialId, T_SPATIAL.SPATIAL_NAME as spatialName, T_SPATIAL.SPATIAL_TWOLETTER, T_SPATIAL.SPATIAL_ISMEMBERCOUNTRY, "
                + "T_ROLE.ROLE_NAME, T_ROLE.ROLE_URL, T_ROLE.ROLE_MEMBERS_URL, "
                + "T_CLIENT_OBLIGATION_LNK.FK_CLIENT_ID, T_CLIENT_OBLIGATION_LNK.STATUS, "
                + "T_CLIENT.PK_CLIENT_ID, T_CLIENT.CLIENT_NAME "
                + "FROM T_DELIVERY JOIN T_OBLIGATION ON T_DELIVERY.FK_RA_ID=T_OBLIGATION.PK_RA_ID "
                + "JOIN T_SPATIAL ON T_SPATIAL.PK_SPATIAL_ID=T_DELIVERY.FK_SPATIAL_ID "
                + "LEFT JOIN T_ROLE ON T_OBLIGATION.RESPONSIBLE_ROLE=T_ROLE.ROLE_ID "
                + "LEFT JOIN T_CLIENT_OBLIGATION_LNK ON T_CLIENT_OBLIGATION_LNK.STATUS='M' AND T_CLIENT_OBLIGATION_LNK.FK_RA_ID=T_OBLIGATION.PK_RA_ID "
                + "LEFT JOIN T_CLIENT ON T_CLIENT_OBLIGATION_LNK.FK_CLIENT_ID=T_CLIENT.PK_CLIENT_ID "
                + "WHERE T_DELIVERY.FK_RA_ID="
                + actDetailsId;

        if (!RODUtil.isNullOrEmpty(spatialId)) {
            query += " AND T_DELIVERY.FK_SPATIAL_ID = " + spatialId;
        }

        query += " ORDER BY T_DELIVERY.UPLOAD_DATE DESC";

		try {
			return jdbcTemplate.query(query, new BeanPropertyRowMapper<Delivery>(Delivery.class));
		}catch (DataAccessException ex) {
	    	logger.error(ex.getMessage(), ex);
			throw new ResourceNotFoundException("DataAccessException error: " + ex.getMessage());
		} 
	}
	
	
	@Override
    public void rollBackDeliveries() {

        try {
        	jdbcTemplate.update("DELETE from T_DELIVERY WHERE STATUS=1");
            
            jdbcTemplate.update("UPDATE T_DELIVERY SET STATUS=1 WHERE STATUS=0");
            
        } catch (DataAccessException sqle) {
            logger.error(sqle.getMessage(), sqle);
            throw new ResourceNotFoundException(sqle.getMessage());
        } 
    }
	
	private static final String qCommitDeliveries = "DELETE " + "FROM T_DELIVERY " + "WHERE STATUS=0";

	 /** */
    private static final String qMarkCountries = "" + "UPDATE T_OBLIGATION "
            + "SET LAST_HARVESTED = {fn now()}, FK_DELIVERY_COUNTRY_IDS = ? " + "WHERE PK_RA_ID = ?;";
    	
    /*
     * (non-Javadoc)
     *
     * @see eionet.rod.services.modules.db.dao.IDeliveryDao#commitDeliveriesNew(java.util.HashMap)
     */
    @Override
    public void commitDeliveries(HashMap<String, HashSet<Integer>> deliveredCountriesByObligations) {

        try {
        	jdbcTemplate.update(qCommitDeliveries);
            
            // now mark the countries

            if (deliveredCountriesByObligations != null && !deliveredCountriesByObligations.isEmpty()) {

                Iterator<Entry<String, HashSet<Integer>>> entries = deliveredCountriesByObligations.entrySet().iterator();
                while (entries.hasNext()) {

                    Entry<String, HashSet<Integer>> entry = entries.next();
                    String obligId = entry.getKey();
                    HashSet<Integer> countryIdsSet = entry.getValue();
                    if (countryIdsSet != null && !countryIdsSet.isEmpty()) {
                        String countryIds = "," + cnvHashSet(countryIdsSet, ",") + ",";
                       // markCountries(Integer.parseInt(obligId), countryIds);
                        jdbcTemplate.update(qMarkCountries, countryIds, Integer.parseInt(obligId));
                    }
                }
            }

        } catch (DataAccessException sqle) {
            logger.error(sqle.getMessage(), sqle);
            throw new ResourceNotFoundException(sqle.getMessage());
        } 
    }
       
    protected String cnvHashSet(HashSet<Integer> hash, String separator) {

        // quick fix
        if (hash == null) {
            return "";
        }

        StringBuffer s = new StringBuffer();
        for (Iterator<Integer> it = hash.iterator(); it.hasNext(); ) {
            Integer id = it.next();
            if (id != null)
                s.append(id);
            if (it.hasNext())
                s.append(separator);

        }

        return s.toString();
    }
    
   
   private static final String qBackUpDeliveries = "UPDATE T_DELIVERY SET STATUS=0";

   /*
    * (non-Javadoc)
    *
    * @see eionet.rod.services.modules.db.dao.IDeliveryDao#backUpDeliveries()
    */
   @Override
   public void backUpDeliveries() {

       try {
           jdbcTemplate.update(qBackUpDeliveries);

       } catch (DataAccessException sqle) {
           logger.error(sqle.getMessage(), sqle);
           throw new ResourceNotFoundException(sqle.getMessage());
       } 
   }
   
   private static final String qSaveDeliveries = "INSERT INTO T_DELIVERY (TITLE,RA_URL,TYPE,FORMAT,COVERAGE,"
           + "STATUS,UPLOAD_DATE,DELIVERY_URL,FK_SPATIAL_ID,FK_RA_ID,COVERAGE_NOTE) "
           + "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
   
   /*
    * (non-Javadoc)
    *
    * @see eionet.rod.services.modules.db.dao.IDeliveryDao#saveDeliveries(TupleQueryResult, HashMap<String,HashSet<Integer>>)
    */
   @Override
   public int saveDeliveries(TupleQueryResult bindings, HashMap<String, HashSet<Integer>> savedCountriesByObligationId) throws ServiceException {

       int batchCounter = 0;

       if (bindings == null) {
           return batchCounter;
       }

       try {
  
    	   while (bindings.hasNext()) {
               BindingSet pairs = bindings.next();
               //System.out.print(row);
               String link = pairs.getValue("link").stringValue();
               if (link == null || link.trim().length() == 0) {
                   link = "No URL";
               }
               String title = (pairs.getValue("title") != null) ? pairs.getValue("title").stringValue() : "";
               String locality = (pairs.getValue("locality") != null) ? pairs.getValue("locality").stringValue() : "";
               String obligation = (pairs.getValue("obligation") != null) ? pairs.getValue("obligation").stringValue() : "";
               String period = (pairs.getValue("period") != null) ? pairs.getValue("period").stringValue() : "";
               String sdate = (pairs.getValue("date") != null) ? pairs.getValue("date").stringValue() : "";
               String note = (pairs.getValue("note") != null) ? pairs.getValue("note").stringValue() : "";
               Date date;
               try {
            	   DateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                   date = isoDateFormat.parse(sdate);
               } catch (ParseException pe) {
            	   date = null;
               }

               String countryId = null;
               if (!StringUtils.isBlank(locality) && locality.contains(spatialsPrefix)) {
                   int index = locality.lastIndexOf("/");
                   if (index != -1 && locality.length() > (index + 1)) {
                       countryId = locality.substring(index + 1);
                   }
               }

               if (countryId == null) {
                   logger.info("!!! Delivery not saved, failed to find id for country: " + locality + ", " + "Identifier: " + link
                           + ", " + "Title: " + title + ", " + "Date: " + sdate + ", " + "Coverage: " + period);
               } else if (!StringUtils.isBlank(obligation) && obligation.contains(obligationsPrefix)) {

                   int index = obligation.lastIndexOf("/");
                   if (index != -1 && obligation.length() > (index + 1)) {
                       String obligationId = obligation.substring(index + 1);

                       if (!StringUtils.isBlank(obligationId)) {
                    	   
                    	   title = (title == null) ? "" : title;
                    	   period =((period == null) ? "" : period);
                    	   date=date != null ? new Timestamp(date.getTime()) : null;
                    	   
                    	   jdbcTemplate.update(qSaveDeliveries,title,obligation,"","",period,1, date,link,Integer.parseInt(countryId), Integer.parseInt(obligationId),note);
                    
                    	   batchCounter++;
                    	   
                           HashSet<Integer> savedCountries = savedCountriesByObligationId.get(obligationId);
                           if (savedCountries == null) {
                               savedCountries = new HashSet<Integer>();
                               savedCountriesByObligationId.put(obligationId, savedCountries);
                           }
                           savedCountries.add(Integer.parseInt(countryId));
                       }
                   }
               }
           }

       } catch (Exception e) {
           logger.error(e.getMessage(), e);
           throw new ResourceNotFoundException("Saving deliveries failed with reason " + e.toString());
       } 

       return batchCounter;
   }
   
}
