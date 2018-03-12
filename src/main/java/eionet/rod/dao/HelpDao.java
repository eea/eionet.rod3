package eionet.rod.dao;

import eionet.rod.model.Documentation;
import eionet.rod.model.Help;

public interface HelpDao {

	Help findId(String id);
	
	Documentation getDoc(String area_id);
		
}


