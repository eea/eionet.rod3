package eionet.rod.service;

import eionet.rod.dao.AnalysisDao;
import eionet.rod.model.AnalysisDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "analysisService")
@Transactional
public class AnalysisServiceImpl implements AnalysisService{

    @Autowired
    AnalysisDao analysisDao;

    @Override
    public AnalysisDTO getStatistics() {
        return analysisDao.getStatistics();
    }
}
