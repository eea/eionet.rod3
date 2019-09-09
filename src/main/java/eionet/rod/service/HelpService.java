package eionet.rod.service;

import eionet.rod.model.Documentation;
import eionet.rod.model.Help;

public interface HelpService {
    Help findId(String helpId);

    Documentation getDoc(String areaId);
}
