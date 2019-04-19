package eionet.rod.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eionet.rod.model.ObligationCountry;
import eionet.rod.model.Spatial;
import eionet.rod.util.exception.ResourceNotFoundException;

/**
 * @author jrobles
 *
 */
@Repository
@Transactional
public class SpatialDaoImpl implements SpatialDao {

		private static final Log logger = LogFactory.getLog(SpatialDaoImpl.class);

	
	private JdbcTemplate jdbcTemplate;
	
	@Resource
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<Spatial> findAll() {
		String query = "SELECT PK_SPATIAL_ID AS spatialId, SPATIAL_NAME AS name, SPATIAL_TYPE AS type,"
                + "SPATIAL_TWOLETTER AS twoLetter, CAST(SPATIAL_ISMEMBERCOUNTRY as char) AS memberCountry "
                + "FROM T_SPATIAL where SPATIAL_TYPE = 'C' "
                + "ORDER BY name";
		
		String queryCount = "SELECT Count(*) as spatialId "
                + "FROM T_SPATIAL ";
		
		try {
		
			Integer countSpatial = jdbcTemplate.queryForObject(queryCount, Integer.class);
		
			if (countSpatial == 0) {
				throw new ResourceNotFoundException("No data in the database");
			}else {
			
				return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Spatial.class));

			}
			
		} catch (DataAccessException e) {
			logger.debug(e, e);
			throw new ResourceNotFoundException("No data in the database", e);
		}
	 
	}
	
	@Override
	public List<Spatial> findAllMember(String member) throws ResourceNotFoundException{
		String query = "SELECT PK_SPATIAL_ID AS spatialId, SPATIAL_NAME AS name "
                + "FROM T_SPATIAL "
				+ "WHERE CAST(SPATIAL_ISMEMBERCOUNTRY as char) = ?"
                + "ORDER BY name";
		
		String queryCount = "SELECT Count(*) as spatialId "
				+ "FROM T_SPATIAL "
				+ "WHERE CAST(SPATIAL_ISMEMBERCOUNTRY as char) = ?";
		
		try {
			Integer countMember = jdbcTemplate.queryForObject(queryCount, Integer.class, member);
			
			if (countMember == 0) {

				return null;
			}else {
				
				return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Spatial.class), member);
			}
		} catch (DataAccessException e) {
			logger.debug(e, e);
			throw new ResourceNotFoundException("You requested was not found in the database", e);
		}
	}

	@Override
	public Spatial findId(Integer spatialId) throws ResourceNotFoundException{
		String query = "SELECT PK_SPATIAL_ID AS spatialId, SPATIAL_NAME AS name, SPATIAL_TYPE AS type,"
                + "SPATIAL_TWOLETTER AS twoLetter, CAST(SPATIAL_ISMEMBERCOUNTRY as char) AS memberCountry "
                + "FROM T_SPATIAL "
                + "WHERE PK_SPATIAL_ID = ? "
                + "ORDER BY name";

		String queryCount = "SELECT Count(*) as spatialId "
                + "FROM T_SPATIAL "
                + "WHERE PK_SPATIAL_ID = ? ";
		
		try {
		
			Integer countSpatial = jdbcTemplate.queryForObject(queryCount, Integer.class, spatialId);
		
			if (countSpatial == 0) {

				throw new ResourceNotFoundException("The country you requested with id " + spatialId + " was not found in the database");
			
			}else {
		
				return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(Spatial.class), spatialId);

			}
			
		} catch (DataAccessException e) {
			logger.debug(e, e);
			throw new ResourceNotFoundException("The country you requested with id " + spatialId + " was not found in the database", e);
		}
		
	}
	
	@Override
	public List<ObligationCountry> findObligationCountriesList(Integer obligationId){
		String query =
	            "SELECT T_SPATIAL.PK_SPATIAL_ID as countryId, T_SPATIAL.SPATIAL_NAME as countryName, T_RASPATIAL_LNK.VOLUNTARY as voluntary, T_SPATIAL.SPATIAL_ISMEMBERCOUNTRY as isMemberCountry "
	                + "FROM T_SPATIAL, T_RASPATIAL_LNK "
	                + "WHERE T_RASPATIAL_LNK.FK_RA_ID= ? " 
	                + "AND T_RASPATIAL_LNK.FK_SPATIAL_ID=T_SPATIAL.PK_SPATIAL_ID "
	                + "ORDER BY T_SPATIAL.SPATIAL_NAME ";
	              
        String queryCount = "SELECT Count(*) as countryId "
        		+ "FROM T_SPATIAL, T_RASPATIAL_LNK "
                + "WHERE T_RASPATIAL_LNK.FK_RA_ID= ? " 
                + "AND T_RASPATIAL_LNK.FK_SPATIAL_ID=T_SPATIAL.PK_SPATIAL_ID ";
		
		try {
		
			Integer countSpatial = jdbcTemplate.queryForObject(queryCount, Integer.class, obligationId);
		
			if (countSpatial == 0) {

				return null;
				
			}else {
		
				return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(ObligationCountry.class), obligationId);

			}
			
		} catch (DataAccessException e) {
			logger.debug(e, e);
			throw new ResourceNotFoundException("The obligation you requested with id " + obligationId + " was not found in the database", e);
		}
	}
			
}
