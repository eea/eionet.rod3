package eionet.rod.service;

import eionet.rod.model.Delivery;
import org.openrdf.query.TupleQueryResult;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * @author ycarrasco
 */
public interface DeliveryService {

    List<Delivery> getAllDelivery(String actDetailsId, String spatialId);

    void rollBackDeliveries();

    void commitDeliveries(HashMap<String, HashSet<Integer>> deliveredCountriesByObligations);

    void backUpDeliveries();

    int saveDeliveries(TupleQueryResult bindings, HashMap<String, HashSet<Integer>> savedCountriesByObligationId);

}
