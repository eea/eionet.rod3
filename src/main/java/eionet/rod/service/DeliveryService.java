package eionet.rod.service;

import java.util.List;

import eionet.rod.model.Delivery;

/**
 * 
 * @author ycarrasco
 *
 */
public interface DeliveryService {

	List<Delivery> getAllDelivery(String actDetailsId, String spatialId);
}
