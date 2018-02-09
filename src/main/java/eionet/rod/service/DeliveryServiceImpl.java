package eionet.rod.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import eionet.rod.dao.DeliveryDao;
import eionet.rod.model.Delivery;

@Service(value = "deliveryService")
@Transactional
public class DeliveryServiceImpl implements DeliveryService {
	
	@Autowired
	DeliveryDao deliveriesDao;
	
	public List<Delivery> getAllDelivery(String actDetailsId, String spatialId){
		
		return deliveriesDao.getAllDelivery(actDetailsId, spatialId);
	}
	
	
}
