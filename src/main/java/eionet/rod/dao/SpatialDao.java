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
	
	Spatial findId(Integer id);

	List<Spatial> findAllMember(String Member);
	
	List<ObligationCountry> findObligationCountriesList(Integer obligationId);
	
	
}
