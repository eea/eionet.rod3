package eionet.rod.model;

public class AnalysisDTO {
	
	private Integer totalRa;
    private String lastUpdateRa;
    private Integer totalLi;
    private String lastUpdateLi;
    private Integer eeaCore;
    private Integer eeaPriority;

    private Integer flaggedRa;
    private Integer instrumentsDue;
    private Integer noIssue;
    
    public AnalysisDTO() {    	
    }

	public Integer getTotalRa() {
		return totalRa;
	}

	public void setTotalRa(Integer totalRa) {
		this.totalRa = totalRa;
	}

	public String getLastUpdateRa() {
		return lastUpdateRa;
	}

	public void setLastUpdateRa(String lastUpdateRa) {
		this.lastUpdateRa = lastUpdateRa;
	}

	public Integer getTotalLi() {
		return totalLi;
	}

	public void setTotalLi(Integer totalLi) {
		this.totalLi = totalLi;
	}

	public String getLastUpdateLi() {
		return lastUpdateLi;
	}

	public void setLastUpdateLi(String lastUpdateLi) {
		this.lastUpdateLi = lastUpdateLi;
	}

	public Integer getEeaCore() {
		return eeaCore;
	}

	public void setEeaCore(Integer eeaCore) {
		this.eeaCore = eeaCore;
	}

	public Integer getEeaPriority() {
		return eeaPriority;
	}

	public void setEeaPriority(Integer eeaPriority) {
		this.eeaPriority = eeaPriority;
	}

	public Integer getFlaggedRa() {
		return flaggedRa;
	}

	public void setFlaggedRa(Integer flaggedRa) {
		this.flaggedRa = flaggedRa;
	}

	public Integer getInstrumentsDue() {
		return instrumentsDue;
	}

	public void setInstrumentsDue(Integer instrumentsDue) {
		this.instrumentsDue = instrumentsDue;
	}

	public Integer getNoIssue() {
		return noIssue;
	}

	public void setNoIssue(Integer noIssue) {
		this.noIssue = noIssue;
	}

}
