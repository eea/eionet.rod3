package eionet.rod.service;

import eionet.rod.dao.HelpDao;
import eionet.rod.model.Documentation;
import eionet.rod.model.Help;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "helpService")
@Transactional
public class HelpServiceImpl implements HelpService {

    @Autowired
    HelpDao helpDao;

    @Override
    public Help findId(String helpId) {
        return helpDao.findId(helpId);
    }

    @Override
    public Documentation getDoc(String areaId) {
        return helpDao.getDoc(areaId);
    }
}
