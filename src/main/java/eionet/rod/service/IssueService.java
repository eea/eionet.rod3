package eionet.rod.service;

import eionet.rod.model.Issue;

import java.util.List;

public interface IssueService {

    List<Issue> findObligationIssuesList(Integer issueOblId);

    List<Issue> findAllIssuesList();

    Issue findById(Integer issueId);
}
