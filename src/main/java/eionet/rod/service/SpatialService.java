package eionet.rod.service;

import eionet.rod.model.ObligationCountry;
import eionet.rod.model.Spatial;

import java.util.List;

/**
 * @author jrobles
 */
public interface SpatialService {

    List<Spatial> findAll();

    List<Spatial> findAllMember(String member);

    Long create(Spatial resource);

    void update(Spatial resource);

    void deleteById(Integer id);

    Spatial findOne(Integer id);

    Spatial getById(Integer spatialId);

    List<ObligationCountry> findObligationCountriesList(Integer obligationId);

}
