package eionet.rod.dao;
import java.util.List;

import eionet.rod.model.Issue;
/**
 * 
 * @author ycarrasco
 *
 */

public interface IssueDao {

	 public List<Issue> findObligationIssuesList(Integer obligationId);
	 
	 public List<Issue> findAllIssuesList();
	 
	 public Issue findById(Integer IssueId);
	
}
