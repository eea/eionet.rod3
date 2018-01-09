package eionet.rod.service;

import java.util.List;

import eionet.rod.model.Spatial;

/**
 * @author jrobles
 *
 */
public interface SpatialService {

	List<Spatial> findAll();

	List<Spatial> findAllMember(String Member);
	
	Long create(Spatial resource);

	void update(Spatial resource);

	void deleteById(Integer id);

	Spatial findOne(Integer id);

	Spatial getById(Integer spatialId);

}
