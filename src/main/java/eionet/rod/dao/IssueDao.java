package eionet.rod.dao;

import eionet.rod.model.Issue;

import java.util.List;

/**
 * @author ycarrasco
 */

public interface IssueDao {

    List<Issue> findObligationIssuesList(Integer issueOblId);

    List<Issue> findAllIssuesList();

    Issue findById(Integer issueId);

}
