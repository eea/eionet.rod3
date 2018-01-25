package eionet.rod.model;
/**
 * 
 * @author ycarrasco
 *
 */
public class SiblingObligation {

	private String siblingOblId;
    private String fkSourceId;
    private String siblingTitle;
    private String authority;
    private String terminate;
	
            
    public String getSiblingOblId() {
		return siblingOblId;
	}
	public void setSiblingOblId(String siblingOblId) {
		this.siblingOblId = siblingOblId;
	}
	public String getFkSourceId() {
		return fkSourceId;
	}
	public void setFkSourceId(String fkSourceId) {
		this.fkSourceId = fkSourceId;
	}
	
	public String getSiblingTitle() {
		return siblingTitle;
	}
	public void setSiblingTitle(String siblingTitle) {
		this.siblingTitle = siblingTitle;
	}
	public String getAuthority() {
		return authority;
	}
	public void setAuthority(String authority) {
		this.authority = authority;
	}
	public String getTerminate() {
		return terminate;
	}
	public void setTerminate(String terminate) {
		this.terminate = terminate;
	}
	
}
