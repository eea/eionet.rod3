package eionet.rod.model;

import java.util.List;

import eionet.rod.util.RODUtil;

public class InstrumentFactsheetDTO {
	
	//Fields from T_SOURCE table
    private Integer sourceId;
    private String sourceCode;
    private String sourceCelexRef;
    private String sourceTitle;
    private String sourceAlias;
    private String sourceUrl;
    private String sourceAbstract;
    private String sourceValidFrom;
    private String sourceComment;
    private String sourceEcEntryIntoForce;
    private String sourceEcAccession;
    private String sourceSecretariat;
    private String sourceSecretariatUrl;
    private String sourceIssuedByUrl;
    private String sourceTerminate;
    
    //Fields from T_CLIENT_SOURCE_LNK table
    private Integer clientSourceLnkFKClientId;
    private Integer clientSourceLnkFKSourceId;
    private String clientSourceLnkStatus;
    
    //Fields from T_CLIENT table
    private Integer clientId;
    private String clientName;
    private String clientUrl;
    
    //Fields from T_SOURCE_LNK table
    private Integer sourceLnkFKSourceParentId;
    private Integer sourceLnkFKSourceChildId;
    
    private List<InstrumentClassificationDTO> classifications;
    private List<String> selectedClassifications;
    
    private InstrumentDTO parent;
    private List<InstrumentDTO> relatedInstruments;
    private List<InstrumentObligationDTO> obligations;
    
	public InstrumentFactsheetDTO() {    	
    }

	public Integer getSourceId() {
		return sourceId;
	}

	public void setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
	}

	public String getSourceCode() {
		return sourceCode;
	}

	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}

	public String getSourceCelexRef() {
		return sourceCelexRef;
	}

	public void setSourceCelexRef(String sourceCelexRef) {
		this.sourceCelexRef = sourceCelexRef;
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

	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	public String getSourceAbstract() {
		return sourceAbstract;
	}

	public void setSourceAbstract(String sourceAbstract) {
		this.sourceAbstract = sourceAbstract;
	}

	public String getSourceValidFrom() {
		return sourceValidFrom;
	}

	public void setSourceValidFrom(String sourceValidFrom) {
		this.sourceValidFrom = sourceValidFrom;
	}

	public String getSourceComment() {
		return sourceComment;
	}

	public void setSourceComment(String sourceComment) {
		this.sourceComment = sourceComment;
	}

	public String getSourceEcEntryIntoForce() {
		return sourceEcEntryIntoForce;
	}

	public void setSourceEcEntryIntoForce(String sourceEcEntryIntoForce) {
		this.sourceEcEntryIntoForce = sourceEcEntryIntoForce;
	}

	public String getSourceEcAccession() {
		return sourceEcAccession;
	}

	public void setSourceEcAccession(String sourceEcAccession) {
		this.sourceEcAccession = sourceEcAccession;
	}

	public String getSourceSecretariat() {
		return sourceSecretariat;
	}

	public void setSourceSecretariat(String sourceSecretariat) {
		this.sourceSecretariat = sourceSecretariat;
	}

	public String getSourceSecretariatUrl() {
		return sourceSecretariatUrl;
	}

	public void setSourceSecretariatUrl(String sourceSecretariatUrl) {
		this.sourceSecretariatUrl = sourceSecretariatUrl;
	}
	
	public String getSourceIssuedByUrl() {
		return sourceIssuedByUrl;
	}

	public void setSourceIssuedByUrl(String sourceIssuedByUrl) {
		this.sourceIssuedByUrl = sourceIssuedByUrl;
	}

	public List<InstrumentObligationDTO> getObligations() {
		return obligations;
	}

	public void setObligations(List<InstrumentObligationDTO> obligations) {
		this.obligations = obligations;
	}

	public Integer getClientId() {
		return clientId;
	}

	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public Integer getClientSourceLnkFKClientId() {
		return clientSourceLnkFKClientId;
	}

	public void setClientSourceLnkFKClientId(Integer clientSourceLnkFKClientId) {
		this.clientSourceLnkFKClientId = clientSourceLnkFKClientId;
	}

	public Integer getClientSourceLnkFKSourceId() {
		return clientSourceLnkFKSourceId;
	}

	public void setClientSourceLnkFKSourceId(Integer clientSourceLnkFKSourceId) {
		this.clientSourceLnkFKSourceId = clientSourceLnkFKSourceId;
	}

	public String getClientSourceLnkStatus() {
		return clientSourceLnkStatus;
	}

	public void setClientSourceLnkStatus(String clientSourceLnkStatus) {
		this.clientSourceLnkStatus = clientSourceLnkStatus;
	}

	public String getClientUrl() {
		return clientUrl;
	}

	public void setClientUrl(String clientUrl) {
		this.clientUrl = clientUrl;
	}

	public String getSourceTerminate() {
		return sourceTerminate;
	}

	public void setSourceTerminate(String sourceTerminate) {
		this.sourceTerminate = sourceTerminate;
	}

	public InstrumentDTO getParent() {
		return parent;
	}

	public void setParent(InstrumentDTO parent) {
		this.parent = parent;
	}

	public List<InstrumentDTO> getRelatedInstruments() {
		return relatedInstruments;
	}

	public void setRelatedInstruments(List<InstrumentDTO> relatedInstruments) {
		this.relatedInstruments = relatedInstruments;
	}

	public Integer getSourceLnkFKSourceParentId() {
		return sourceLnkFKSourceParentId;
	}

	public void setSourceLnkFKSourceParentId(Integer sourceLnkFKSourceParentId) {
		this.sourceLnkFKSourceParentId = sourceLnkFKSourceParentId;
	}

	public Integer getSourceLnkFKSourceChildId() {
		return sourceLnkFKSourceChildId;
	}

	public void setSourceLnkFKSourceChildId(Integer sourceLnkFKSourceChildId) {
		this.sourceLnkFKSourceChildId = sourceLnkFKSourceChildId;
	}

	public List<InstrumentClassificationDTO> getClassifications() {
		return classifications;
	}

	public void setClassifications(List<InstrumentClassificationDTO> classifications) {
		this.classifications = classifications;
	}

	public List<String> getSelectedClassifications() {
		return selectedClassifications;
	}

	public void setSelectedClassifications(List<String> selectedClassifications) {
		this.selectedClassifications = selectedClassifications;
	}
	
	public String truncateText(String truncateText) {
	       return RODUtil.truncateText(truncateText);
	}
    
}
