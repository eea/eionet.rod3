/**
 * 
 */
package eionet.rod.dao;

import java.util.List;

import org.springframework.context.ApplicationContextException;

import eionet.rod.model.ObligationCountry;
import eionet.rod.model.Spatial;

/**
 * @author jrobles
 *
 */
public interface SpatialDao {

	public List<Spatial> findAll();
	
	public Spatial findId(Integer id) throws ApplicationContextException;

	public List<Spatial> findAllMember(String Member);
	
	public List<ObligationCountry> findObligationCountriesList(Integer obligationId);
	
}
