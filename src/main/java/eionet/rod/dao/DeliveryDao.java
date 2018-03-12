package eionet.rod.dao;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.openrdf.query.TupleQueryResult;

import eionet.rod.model.Delivery;
import eionet.rod.util.exception.ServiceException;

public interface DeliveryDao {

	List<Delivery> getAllDelivery(String actDetailsId, String spatialId);
	
	void rollBackDeliveries();
	
	void commitDeliveries(HashMap<String, HashSet<Integer>> deliveredCountriesByObligations);
	
	void backUpDeliveries();
	
	int saveDeliveries(TupleQueryResult bindings, HashMap<String, HashSet<Integer>> savedCountriesByObligationId) throws ServiceException;
}


