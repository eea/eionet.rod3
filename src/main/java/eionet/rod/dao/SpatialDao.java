/**
 * 
 */
package eionet.rod.dao;

import java.util.List;

import eionet.rod.model.ObligationCountry;
import eionet.rod.model.Spatial;

/**
 * @author jrobles
 *
 */
public interface SpatialDao {

	List<Spatial> findAll();
	
	Spatial findId(Integer spatialId);

	List<Spatial> findAllMember(String member);
	
	List<ObligationCountry> findObligationCountriesList(Integer obligationId);
	
	
}
