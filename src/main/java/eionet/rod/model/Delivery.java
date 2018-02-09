package eionet.rod.model;

public class Delivery {
	
	//Fields from T_DELIVERY table
    private Integer deliveryFKObligationId;
    private Integer deliveryFKSpatialId;
    private String deliveryTitle;
    private String deliveryUrl;
    private String deliveryUploadDate;
    private String deliveryType;
    private String deliveryFormat;
    private String deliveryCoverage;
    private String deliveryCoverageNote;
    
    
	public Integer getDeliveryFKObligationId() {
		return deliveryFKObligationId;
	}
	public void setDeliveryFKObligationId(Integer deliveryFKObligationId) {
		this.deliveryFKObligationId = deliveryFKObligationId;
	}
	public Integer getDeliveryFKSpatialId() {
		return deliveryFKSpatialId;
	}
	public void setDeliveryFKSpatialId(Integer deliveryFKSpatialId) {
		this.deliveryFKSpatialId = deliveryFKSpatialId;
	}
	public String getDeliveryTitle() {
		return deliveryTitle;
	}
	public void setDeliveryTitle(String deliveryTitle) {
		this.deliveryTitle = deliveryTitle;
	}
	public String getDeliveryUrl() {
		return deliveryUrl;
	}
	public void setDeliveryUrl(String deliveryUrl) {
		this.deliveryUrl = deliveryUrl;
	}
	public String getDeliveryUploadDate() {
		return deliveryUploadDate;
	}
	public void setDeliveryUploadDate(String deliveryUploadDate) {
		this.deliveryUploadDate = deliveryUploadDate;
	}
	public String getDeliveryType() {
		return deliveryType;
	}
	public void setDeliveryType(String deliveryType) {
		this.deliveryType = deliveryType;
	}
	public String getDeliveryFormat() {
		return deliveryFormat;
	}
	public void setDeliveryFormat(String deliveryFormat) {
		this.deliveryFormat = deliveryFormat;
	}
	public String getDeliveryCoverage() {
		return deliveryCoverage;
	}
	public void setDeliveryCoverage(String deliveryCoverage) {
		this.deliveryCoverage = deliveryCoverage;
	}
	public String getDeliveryCoverageNote() {
		return deliveryCoverageNote;
	}
	public void setDeliveryCoverageNote(String deliveryCoverageNote) {
		this.deliveryCoverageNote = deliveryCoverageNote;
	}
	
}
