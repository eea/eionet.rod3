/**
 *
 */
package eionet.rod.dao;

import eionet.rod.model.ObligationCountry;
import eionet.rod.model.Spatial;

import java.util.List;

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
