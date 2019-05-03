package eionet.rod.dao;

import eionet.rod.model.Documentation;
import eionet.rod.model.Help;

public interface HelpDao {

    Help findId(String helpId);

    Documentation getDoc(String areaId);

}


