package eionet.rod.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eionet.rod.dao.SpatialDao;
import eionet.rod.model.ObligationCountry;
import eionet.rod.model.Spatial;

/**
 * @author jrobles
 *
 */
@Service(value = "spatialService")
@Transactional
public class SpatialServiceImpl implements SpatialService {
	
	@Autowired
	private SpatialDao spatialDao;
	

	@Override
	public List<Spatial> findAll() {
		return spatialDao.findAll();
	}
	
	@Override
	public List<Spatial> findAllMember(String member) {
		return spatialDao.findAllMember(member);
	}
	
	@Override
	public List<ObligationCountry> findObligationCountriesList(Integer obligationId){
		return spatialDao.findObligationCountriesList(obligationId);
	}
	
	@Override
	public Spatial findOne(Integer id) {
		
		return spatialDao.findId(id);
	}
	
	
	@Override
	public Long create(Spatial resource) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(Spatial resource) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteById(Integer id) {
		// TODO Auto-generated method stub
		
	}

	

	@Override
	public Spatial getById(Integer spatialId) {
		// TODO Auto-generated method stub
		return null;
	}

}
