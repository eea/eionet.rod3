package eionet.rod.dao;

import java.util.List;

import eionet.rod.model.Delivery;

public interface DeliveryDao {

	List<Delivery> getAllDelivery(String actDetailsId, String spatialId);
		
}


