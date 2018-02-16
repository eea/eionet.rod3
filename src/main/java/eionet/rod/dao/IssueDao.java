package eionet.rod.dao;
import java.util.List;

import eionet.rod.model.Issue;
/**
 * 
 * @author ycarrasco
 *
 */

public interface IssueDao {

	 List<Issue> findObligationIssuesList(Integer obligationId);
	 
	 List<Issue> findAllIssuesList();
	 
	 Issue findById(Integer IssueId);
	
}
