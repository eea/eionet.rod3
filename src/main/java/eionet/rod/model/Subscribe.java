package eionet.rod.model;

import java.util.List;

import eionet.rod.util.RODUtil;

public class Subscribe {

	private String id;
    private String sid;
    
    private String selectedCountry;
   
    private String selectedIssue;
    private String clientName;
    private String clientId;
    
    private String selectedClient;
    private String eventTypeDeadline;
    private String eventTypeOblChange;
    private String eventTypeObligation;
    private String eventTypeInstrument;
    private String eventTypeNewInstrument;

    
    private String obligationName;
    private String obligationId;
    private List<String> obligations;
    private String selectedObligation;
    private String instrumentName;
    private List<String> instruments;
    private String selectedInstrument;
	
    public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public String getSelectedCountry() {
		return selectedCountry;
	}
	public void setSelectedCountry(String selectedCountry) {
		this.selectedCountry = selectedCountry;
	}
	public String getSelectedIssue() {
		return selectedIssue;
	}
	public void setSelectedIssue(String selectedIssue) {
		this.selectedIssue = selectedIssue;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getSelectedClient() {
		return selectedClient;
	}
	public void setSelectedClient(String selectedClient) {
		this.selectedClient = selectedClient;
	}
	public String getEventTypeDeadline() {
		return eventTypeDeadline;
	}
	public void setEventTypeDeadline(String eventTypeDeadline) {
		this.eventTypeDeadline = eventTypeDeadline;
	}
	public String getEventTypeOblChange() {
		return eventTypeOblChange;
	}
	public void setEventTypeOblChange(String eventTypeOblChange) {
		this.eventTypeOblChange = eventTypeOblChange;
	}
	public String getEventTypeObligation() {
		return eventTypeObligation;
	}
	public void setEventTypeObligation(String eventTypeObligation) {
		this.eventTypeObligation = eventTypeObligation;
	}
	public String getEventTypeInstrument() {
		return eventTypeInstrument;
	}
	public void setEventTypeInstrument(String eventTypeInstrument) {
		this.eventTypeInstrument = eventTypeInstrument;
	}
	public String getEventTypeNewInstrument() {
		return eventTypeNewInstrument;
	}
	public void setEventTypeNewInstrument(String eventTypeNewInstrument) {
		this.eventTypeNewInstrument = eventTypeNewInstrument;
	}
	
	public String getObligationName() {
		return obligationName;
	}
	public void setObligationName(String obligationName) {
		this.obligationName = obligationName;
	}
	public String getObligationId() {
		return obligationId;
	}
	public void setObligationId(String obligationId) {
		this.obligationId = obligationId;
	}
	public List<String> getObligations() {
		return obligations;
	}
	public void setObligations(List<String> obligations) {
		this.obligations = obligations;
	}
	public String getSelectedObligation() {
		return selectedObligation;
	}
	public void setSelectedObligation(String selectedObligation) {
		this.selectedObligation = selectedObligation;
	}
	public String getInstrumentName() {
		return instrumentName;
	}
	public void setInstrumentName(String instrumentName) {
		this.instrumentName = instrumentName;
	}
	public List<String> getInstruments() {
		return instruments;
	}
	public void setInstruments(List<String> instruments) {
		this.instruments = instruments;
	}
	public String getSelectedInstrument() {
		return selectedInstrument;
	}
	public void setSelectedInstrument(String selectedInstrument) {
		this.selectedInstrument = selectedInstrument;
	}
	public String truncateText(String truncateText) {
	       return RODUtil.truncateText(truncateText);
	}

}
