package eionet.rod.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;


import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eionet.rod.model.Delivery;

import eionet.rod.util.RODUtil;
import eionet.rod.util.exception.ResourceNotFoundException;


@Repository
@Transactional
public class DeliveryDaoImpl implements DeliveryDao{
	
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

		
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<Delivery>(Delivery.class));
	}
}
