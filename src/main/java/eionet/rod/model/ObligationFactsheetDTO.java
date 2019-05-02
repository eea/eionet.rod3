package eionet.rod.model;

import java.util.Date;

/**
 *
 * @author altnyris
 *
 */
public class ObligationFactsheetDTO {

    //Fields from T_OBLIGATION table
    private String obligationId;
    private String fkSourceId;
    private String fkClientId;
    private Date validSince;
    private Date validTo;
    private String title;
    private String formatName;
    private String reportFormatUrl;
    private String reportFreq;
    private String reportFreqDetail;
    private String reportingFormat;
    private String nextReporting;
    private String dateComments;
    private String terminate;
    private String lastUpdate;
    private String comment;
    private String responsibleRole;
    private Date nextDeadline;
    private Date nextDeadline2;
    private Date firstReporting;
    private String reportFreqMonths;
    private String continousReporting;
    private String fkDeliveryCountryIds;
    private String rmNextUpdate;
    private String rmVerified;
    private String rmVerifiedBy;
    private String locationPtr;
    private String locationInfo;
    private String dataUsedFor;
    private String dataUsedForUrl;
    private String description;
    private String responsibleRoleSuf;
    private String nationalContact;
    private String nationalContactUrl;
    private String coordinatorRole;
    private String coordinatorRoleSuf;
    private String coordinator;
    private String coordinatorUrl;
    private String authority;
    private int eeaPrimary;
    private String parameters;
    private String validatedBy;
    private String overlapUrl;
    private int eeaCore;
    private int flagged;
    private String dpsirD;
    private String dpsirP;
    private String dpsirS;
    private String dpsirI;
    private String dpsirR;

    private String legalMoral;

    //Fields from T_SOURCE table
    private String sourceId;
    private String sourceTitle;
    private String sourceAlias;
    private String sourceCelexRef;
    private String sourceCode;

    //Fields from RESP_ROLE table
    private String respRoleId;
    private String respRoleName;
    private String respRoleUrl;
    private String respRoleMembersUrl;

    //Fields from COORD_ROLE table
    private String coordRoleId;
    private String coordRoleName;
    private String coordRoleUrl;
    private String coordRoleMembersUrl;

    //Fields from T_LOOKUP table
    private String lookupCValue;
    private String lookupCTerm;

    //Fields from T_CLIENT_LNK table
    private String clientLnkFKClientId;
    private String clientLnkFKObjectId;
    private String clientLnkStatus;
    private String clientLnkType;

    //Fields from T_CLIENT table
    private String clientId;
    private String clientName;


    /**
     * Constructor.
     */
    public ObligationFactsheetDTO() {
    }


    public String getObligationId() {
        return obligationId;
    }


    public void setObligationId(String obligationId) {
        this.obligationId = obligationId;
    }


    public String getFkSourceId() {
        return fkSourceId;
    }


    public void setFkSourceId(String fkSourceId) {
        this.fkSourceId = fkSourceId;
    }


    public String getFkClientId() {
        return fkClientId;
    }


    public void setFkClientId(String fkClientId) {
        this.fkClientId = fkClientId;
    }


    public Date getValidSince() {
        return validSince;
    }


    public void setValidSince(Date validSince) {
        this.validSince = validSince;
    }


    public Date getValidTo() {
        return validTo;
    }


    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
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


    public String getReportFreq() {
        return reportFreq;
    }


    public void setReportFreq(String reportFreq) {
        this.reportFreq = reportFreq;
    }


    public String getReportFreqDetail() {
        return reportFreqDetail;
    }


    public void setReportFreqDetail(String reportFreqDetail) {
        this.reportFreqDetail = reportFreqDetail;
    }


    public String getReportingFormat() {
        return reportingFormat;
    }


    public void setReportingFormat(String reportingFormat) {
        this.reportingFormat = reportingFormat;
    }


    public String getNextReporting() {
        return nextReporting;
    }


    public void setNextReporting(String nextReporting) {
        this.nextReporting = nextReporting;
    }


    public String getDateComments() {
        return dateComments;
    }


    public void setDateComments(String dateComments) {
        this.dateComments = dateComments;
    }


    public String getTerminate() {
        return terminate;
    }


    public void setTerminate(String terminate) {
        this.terminate = terminate;
    }


    public String getLastUpdate() {
        return lastUpdate;
    }


    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }


    public String getComment() {
        return comment;
    }


    public void setComment(String comment) {
        this.comment = comment;
    }


    public String getResponsibleRole() {
        return responsibleRole;
    }


    public void setResponsibleRole(String responsibleRole) {
        this.responsibleRole = responsibleRole;
    }


    public Date getNextDeadline() {
        return nextDeadline;
    }


    public void setNextDeadline(Date nextDeadline) {
        this.nextDeadline = nextDeadline;
    }


    public Date getNextDeadline2() {
        return nextDeadline2;
    }


    public void setNextDeadline2(Date nextDeadline2) {
        this.nextDeadline2 = nextDeadline2;
    }


    public Date getFirstReporting() {
        return firstReporting;
    }


    public void setFirstReporting(Date firstReporting) {
        this.firstReporting = firstReporting;
    }


    public String getReportFreqMonths() {
        return reportFreqMonths;
    }


    public void setReportFreqMonths(String reportFreqMonths) {
        this.reportFreqMonths = reportFreqMonths;
    }


    public String getContinousReporting() {
        return continousReporting;
    }


    public void setContinousReporting(String continousReporting) {
        this.continousReporting = continousReporting;
    }


    public String getFkDeliveryCountryIds() {
        return fkDeliveryCountryIds;
    }


    public void setFkDeliveryCountryIds(String fkDeliveryCountryIds) {
        this.fkDeliveryCountryIds = fkDeliveryCountryIds;
    }


    public String getRmNextUpdate() {
        return rmNextUpdate;
    }


    public void setRmNextUpdate(String rmNextUpdate) {
        this.rmNextUpdate = rmNextUpdate;
    }


    public String getRmVerified() {
        return rmVerified;
    }


    public void setRmVerified(String rmVerified) {
        this.rmVerified = rmVerified;
    }


    public String getRmVerifiedBy() {
        return rmVerifiedBy;
    }


    public void setRmVerifiedBy(String rmVerifiedBy) {
        this.rmVerifiedBy = rmVerifiedBy;
    }


    public String getLocationPtr() {
        return locationPtr;
    }


    public void setLocationPtr(String locationPtr) {
        this.locationPtr = locationPtr;
    }


    public String getLocationInfo() {
        return locationInfo;
    }


    public void setLocationInfo(String locationInfo) {
        this.locationInfo = locationInfo;
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


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public String getResponsibleRoleSuf() {
        return responsibleRoleSuf;
    }


    public void setResponsibleRoleSuf(String responsibleRoleSuf) {
        this.responsibleRoleSuf = responsibleRoleSuf;
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


    public String getAuthority() {
        return authority;
    }


    public void setAuthority(String authority) {
        this.authority = authority;
    }


    public int getEeaPrimary() {
        return eeaPrimary;
    }


    public void setEeaPrimary(int eeaPrimary) {
        this.eeaPrimary = eeaPrimary;
    }


    public String getParameters() {
        return parameters;
    }


    public void setParameters(String parameters) {
        this.parameters = parameters;
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


    public String getDpsirD() {
        return dpsirD;
    }


    public void setDpsirD(String dpsirD) {
        this.dpsirD = dpsirD;
    }


    public String getDpsirP() {
        return dpsirP;
    }


    public void setDpsirP(String dpsirP) {
        this.dpsirP = dpsirP;
    }


    public String getDpsirS() {
        return dpsirS;
    }


    public void setDpsirS(String dpsirS) {
        this.dpsirS = dpsirS;
    }


    public String getDpsirI() {
        return dpsirI;
    }


    public void setDpsirI(String dpsirI) {
        this.dpsirI = dpsirI;
    }


    public String getDpsirR() {
        return dpsirR;
    }


    public void setDpsirR(String dpsirR) {
        this.dpsirR = dpsirR;
    }


    public String getLegalMoral() {
        return legalMoral;
    }


    public void setLegalMoral(String legalMoral) {
        this.legalMoral = legalMoral;
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


    public String getSourceCelexRef() {
        return sourceCelexRef;
    }


    public void setSourceCelexRef(String sourceCelexRef) {
        this.sourceCelexRef = sourceCelexRef;
    }


    public String getSourceCode() {
        return sourceCode;
    }


    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
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


    public String getRespRoleUrl() {
        return respRoleUrl;
    }


    public void setRespRoleUrl(String respRoleUrl) {
        this.respRoleUrl = respRoleUrl;
    }


    public String getRespRoleMembersUrl() {
        return respRoleMembersUrl;
    }


    public void setRespRoleMembersUrl(String respRoleMembersUrl) {
        this.respRoleMembersUrl = respRoleMembersUrl;
    }


    public String getCoordRoleId() {
        return coordRoleId;
    }


    public void setCoordRoleId(String coordRoleId) {
        this.coordRoleId = coordRoleId;
    }


    public String getCoordRoleName() {
        return coordRoleName;
    }


    public void setCoordRoleName(String coordRoleName) {
        this.coordRoleName = coordRoleName;
    }


    public String getCoordRoleUrl() {
        return coordRoleUrl;
    }


    public void setCoordRoleUrl(String coordRoleUrl) {
        this.coordRoleUrl = coordRoleUrl;
    }


    public String getCoordRoleMembersUrl() {
        return coordRoleMembersUrl;
    }


    public void setCoordRoleMembersUrl(String coordRoleMembersUrl) {
        this.coordRoleMembersUrl = coordRoleMembersUrl;
    }


    public String getLookupCValue() {
        return lookupCValue;
    }


    public void setLookupCValue(String lookupCValue) {
        this.lookupCValue = lookupCValue;
    }


    public String getLookupCTerm() {
        return lookupCTerm;
    }


    public void setLookupCTerm(String lookupCTerm) {
        this.lookupCTerm = lookupCTerm;
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


    public String getValidatedBy() {
        return validatedBy;
    }


    public void setValidatedBy(String validatedBy) {
        this.validatedBy = validatedBy;
    }


}
