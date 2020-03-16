package eionet.rod.model;

import java.util.Date;

public class AnalysisDTO {

    private Integer totalReportingObligation;
    private Integer openedReportingObligation;
    private Integer terminatedReportingObligation;
    private Date lastUpdateReportingObligation;
    private Integer totalLegalInstrument;
    private Date lastUpdateLegalInstrument;
    private Integer eeaCore;
    private Integer eeaPriority;

    private Integer flaggedRa;
    private Integer instrumentsDue;
    private Integer noIssue;

    public AnalysisDTO() {
    }

    public Integer getTotalReportingObligation() {
        return totalReportingObligation;
    }

    public void setTotalReportingObligation(Integer totalReportingObligation) {
        this.totalReportingObligation = totalReportingObligation;
    }

    public Integer getOpenedReportingObligation() {
        return openedReportingObligation;
    }

    public void setOpenedReportingObligation(Integer openedReportingObligation) {
        this.openedReportingObligation = openedReportingObligation;
    }

    public Integer getTerminatedReportingObligation() {
        return terminatedReportingObligation;
    }

    public void setTerminatedReportingObligation(Integer terminatedReportingObligation) {
        this.terminatedReportingObligation = terminatedReportingObligation;
    }

    public Date getLastUpdateReportingObligation() {
        return lastUpdateReportingObligation;
    }

    public void setLastUpdateReportingObligation(Date lastUpdateReportingObligation) {
        this.lastUpdateReportingObligation = lastUpdateReportingObligation;
    }

    public Integer getTotalLegalInstrument() {
        return totalLegalInstrument;
    }

    public void setTotalLegalInstrument(Integer totalLegalInstrument) {
        this.totalLegalInstrument = totalLegalInstrument;
    }

    public Date getLastUpdateLegalInstrument() {
        return lastUpdateLegalInstrument;
    }

    public void setLastUpdateLegalInstrument(Date lastUpdateLegalInstrument) {
        this.lastUpdateLegalInstrument = lastUpdateLegalInstrument;
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
