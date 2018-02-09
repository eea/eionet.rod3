package eionet.rod.model;

import java.util.List;

import javax.validation.constraints.*;

import org.hibernate.validator.constraints.NotEmpty;

import eionet.rod.util.RODUtil;
/**
 * @author ycarrasco
 *
 */
public class Obligations {

	@NotNull @NotEmpty
	private String oblTitle;
	@NotNull @NotEmpty
	private String description;
	
	private Integer obligationId;
	private int eeaPrimary;
    private String overlapUrl;
    private int eeaCore;
    private int flagged;
    private String coordinator;
    private String coordinatorUrl;
    private String coordinatorRole;
    @NotNull @NotEmpty
    private String coordinatorRoleSuf;
    
    private String nationalContact;
    private String nationalContactUrl;
    private String responsibleRole;
    @NotNull @NotEmpty
    private String responsibleRoleSuf;
    
    @NotNull @NotEmpty
    private String terminate;
    
    private String reportFreqMonths;
    private String nextDeadline;
    private String nextDeadline2;
    private String nextReporting;
    private String firstReporting;
    private String continousReporting;
    private String dateComments;
    private String formatName;
    private String reportFormatUrl;
    private String reportingFormat;
    private String locationPtr;
    private String locationInfo;
    private String dataUsedFor;
    private String dataUsedForUrl;
    private String validSince;
    private String validTo;
    private String authority;
    private String comment;
    private String parameters;
    private String hasDelivery;
        
    //Fields from t_role table
    private String coordRoleId;
    private String coordRoleUrl;
    private String coordRoleName;
    
    private String respRoleId;
    private String respRoleName;
    
    //Fields from T_CLIENT_LNK table
    private String clientLnkFKClientId;
    private String clientLnkFKObjectId;
    private String clientLnkStatus;
    private String clientLnkType;

    //Fields from T_CLIENT table
    private String clientId;
    private String clientName;
    
    //Fields from T_SOURCE table
    private String sourceId;
    private String sourceTitle;
    private String sourceAlias;
    
    private List<String> selectedClients;
    private List<String> selectedFormalCountries;
    private List<String> selectedVoluntaryCountries;
    private List<String> selectedIssues;
   
    //Fields from T_ISSUE table to search
    private String issueId;
    
    //Fields from T_SPATIAL table to search
    private String spatialId;
    
    //field to deadline search
    private String deadlineId;
    
    private String delObligations;
    
    
	public String getOblTitle() {
		return oblTitle;
	}

	public void setOblTitle(String oblTitle) {
		this.oblTitle = oblTitle;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getObligationId() {
		return obligationId;
	}

	public void setObligationId(Integer obligationId) {
		this.obligationId = obligationId;
	}

	public int getEeaPrimary() {
		return eeaPrimary;
	}

	public void setEeaPrimary(int eeaPrimary) {
		this.eeaPrimary = eeaPrimary;
	}

	public String getOverlapUrl() {
		return overlapUrl;
	}

	public void setOverlapUrl(String overlapUrl) {
		this.overlapUrl = overlapUrl;
	}

	public int getEeaCore() {
		return eeaCore;
	}

	public void setEeaCore(int eeaCore) {
		this.eeaCore = eeaCore;
	}

	public int getFlagged() {
		return flagged;
	}

	public void setFlagged(int flagged) {
		this.flagged = flagged;
	}

	public String getCoordinator() {
		return coordinator;
	}

	public void setCoordinator(String coordinator) {
		this.coordinator = coordinator;
	}

	public String getCoordinatorUrl() {
		return coordinatorUrl;
	}

	public void setCoordinatorUrl(String coordinatorUrl) {
		this.coordinatorUrl = coordinatorUrl;
	}

	public String getCoordinatorRole() {
		return coordinatorRole;
	}

	public void setCoordinatorRole(String coordinatorRole) {
		this.coordinatorRole = coordinatorRole;
	}

	public String getCoordinatorRoleSuf() {
		return coordinatorRoleSuf;
	}

	public void setCoordinatorRoleSuf(String coordinatorRoleSuf) {
		this.coordinatorRoleSuf = coordinatorRoleSuf;
	}

	public String getNationalContact() {
		return nationalContact;
	}

	public void setNationalContact(String nationalContact) {
		this.nationalContact = nationalContact;
	}

	public String getNationalContactUrl() {
		return nationalContactUrl;
	}

	public void setNationalContactUrl(String nationalContactUrl) {
		this.nationalContactUrl = nationalContactUrl;
	}

	public String getResponsibleRole() {
		return responsibleRole;
	}

	public void setResponsibleRole(String responsibleRole) {
		this.responsibleRole = responsibleRole;
	}

	public String getResponsibleRoleSuf() {
		return responsibleRoleSuf;
	}

	public void setResponsibleRoleSuf(String responsibleRoleSuf) {
		this.responsibleRoleSuf = responsibleRoleSuf;
	}

	public String getTerminate() {
		return terminate;
	}

	public void setTerminate(String terminate) {
		this.terminate = terminate;
	}

	public String getReportFreqMonths() {
		return reportFreqMonths;
	}

	public void setReportFreqMonths(String reportFreqMonths) {
		this.reportFreqMonths = reportFreqMonths;
	}

	public String getNextDeadline() {
		return nextDeadline;
	}

	public void setNextDeadline(String nextDeadline) {
		this.nextDeadline = nextDeadline;
	}

	public String getNextDeadline2() {
		return nextDeadline2;
	}

	public void setNextDeadline2(String nextDeadline2) {
		this.nextDeadline2 = nextDeadline2;
	}

	public String getNextReporting() {
		return nextReporting;
	}

	public void setNextReporting(String nextReporting) {
		this.nextReporting = nextReporting;
	}

	public String getFirstReporting() {
		return firstReporting;
	}

	public void setFirstReporting(String firstReporting) {
		this.firstReporting = firstReporting;
	}

	public String getContinousReporting() {
		return continousReporting;
	}

	public void setContinousReporting(String continousReporting) {
		this.continousReporting = continousReporting;
	}

	public String getDateComments() {
		return dateComments;
	}

	public void setDateComments(String dateComments) {
		this.dateComments = dateComments;
	}

	public String getFormatName() {
		return formatName;
	}

	public void setFormatName(String formatName) {
		this.formatName = formatName;
	}

	public String getReportFormatUrl() {
		return reportFormatUrl;
	}

	public void setReportFormatUrl(String reportFormatUrl) {
		this.reportFormatUrl = reportFormatUrl;
	}

	public String getReportingFormat() {
		return reportingFormat;
	}

	public void setReportingFormat(String reportingFormat) {
		this.reportingFormat = reportingFormat;
	}

	public String getLocationInfo() {
		return locationInfo;
	}

	public void setLocationInfo(String locationInfo) {
		this.locationInfo = locationInfo;
	}

	public String getLocationPtr() {
		return locationPtr;
	}

	public void setLocationPtr(String locationPtr) {
		this.locationPtr = locationPtr;
	}

	public String getDataUsedFor() {
		return dataUsedFor;
	}

	public void setDataUsedFor(String dataUsedFor) {
		this.dataUsedFor = dataUsedFor;
	}

	public String getDataUsedForUrl() {
		return dataUsedForUrl;
	}

	public void setDataUsedForUrl(String dataUsedForUrl) {
		this.dataUsedForUrl = dataUsedForUrl;
	}

	public String getValidSince() {
		return validSince;
	}

	public void setValidSince(String validSince) {
		this.validSince = validSince;
	}

	public String getValidTo() {
		return validTo;
	}

	public void setValidTo(String validTo) {
		if (validTo =="") {
			validTo = null;
		}
		this.validTo = validTo;
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getCoordRoleId() {
		return coordRoleId;
	}

	public void setCoordRoleId(String coordRoleId) {
		this.coordRoleId = coordRoleId;
	}

	public String getCoordRoleUrl() {
		return coordRoleUrl;
	}

	public void setCoordRoleUrl(String coordRoleUrl) {
		this.coordRoleUrl = coordRoleUrl;
	}

	public String getCoordRoleName() {
		return coordRoleName;
	}

	public void setCoordRoleName(String coordRoleName) {
		this.coordRoleName = coordRoleName;
	}

	public String getRespRoleId() {
		return respRoleId;
	}

	public void setRespRoleId(String respRoleId) {
		this.respRoleId = respRoleId;
	}

	public String getRespRoleName() {
		return respRoleName;
	}

	public void setRespRoleName(String respRoleName) {
		this.respRoleName = respRoleName;
	}

	public String getClientLnkFKClientId() {
		return clientLnkFKClientId;
	}

	public void setClientLnkFKClientId(String clientLnkFKClientId) {
		this.clientLnkFKClientId = clientLnkFKClientId;
	}

	public String getClientLnkFKObjectId() {
		return clientLnkFKObjectId;
	}

	public void setClientLnkFKObjectId(String clientLnkFKObjectId) {
		this.clientLnkFKObjectId = clientLnkFKObjectId;
	}

	public String getClientLnkStatus() {
		return clientLnkStatus;
	}

	public void setClientLnkStatus(String clientLnkStatus) {
		this.clientLnkStatus = clientLnkStatus;
	}

	public String getClientLnkType() {
		return clientLnkType;
	}

	public void setClientLnkType(String clientLnkType) {
		this.clientLnkType = clientLnkType;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getSourceTitle() {
		return sourceTitle;
	}

	public void setSourceTitle(String sourceTitle) {
		this.sourceTitle = sourceTitle;
	}

	public String getSourceAlias() {
		return sourceAlias;
	}

	public void setSourceAlias(String sourceAlias) {
		this.sourceAlias = sourceAlias;
	}
	
	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public List<String> getSelectedClients() {
		return selectedClients;
	}

	public void setSelectedClients(List<String> selectedClients) {
		this.selectedClients = selectedClients;
	}

	public List<String> getSelectedFormalCountries() {
		return selectedFormalCountries;
	}

	public void setSelectedFormalCountries(List<String> selectedFormalCountries) {
		this.selectedFormalCountries = selectedFormalCountries;
	}

	public List<String> getSelectedVoluntaryCountries() {
		return selectedVoluntaryCountries;
	}

	public void setSelectedVoluntaryCountries(List<String> selectedVoluntaryCountries) {
		this.selectedVoluntaryCountries = selectedVoluntaryCountries;
	}

	public List<String> getSelectedIssues() {
		return selectedIssues;
	}

	public void setSelectedIssues(List<String> selectedIssues) {
		this.selectedIssues = selectedIssues;
	}

	public String getIssueId() {
		return issueId;
	}

	public void setIssueId(String issueId) {
		this.issueId = issueId;
	}

	public String getSpatialId() {
		return spatialId;
	}

	public void setSpatialId(String spatialId) {
		this.spatialId = spatialId;
	}

	public String getDeadlineId() {
		return deadlineId;
	}

	public void setDeadlineId(String deadlineId) {
		this.deadlineId = deadlineId;
	}

	public String getHasDelivery() {
		return hasDelivery;
	}

	public void setHasDelivery(String hasDelivery) {
		this.hasDelivery = hasDelivery;
	}

	public String getDelObligations() {
		return delObligations;
	}

	public void setDelObligations(String delObligations) {
		this.delObligations = delObligations;
	}

	public String truncateText(String truncateText) {
	       return RODUtil.truncateText(truncateText);
	}

	
}
