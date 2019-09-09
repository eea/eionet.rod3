package eionet.rod.service;

import eionet.rod.dao.IssueDao;
import eionet.rod.model.Issue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service(value = "issueService")
@Transactional
public class IssueServiceImpl implements IssueService {

    @Autowired
    private IssueDao issueDao;

    @Override
    public List<Issue> findObligationIssuesList(Integer issueOblId) {
        return issueDao.findObligationIssuesList(issueOblId);
    }

    @Override
    public List<Issue> findAllIssuesList() {
        return issueDao.findAllIssuesList();
    }

    @Override
    public Issue findById(Integer issueId) {
        return issueDao.findById(issueId);
    }
}
