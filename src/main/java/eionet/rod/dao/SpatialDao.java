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

	public List<Spatial> findAll();
	
	public Spatial findId(Integer id);

	public List<Spatial> findAllMember(String Member);
	
	public List<ObligationCountry> findObligationCountriesList(Integer obligationId);
	
	
}
