package eionet.rod.model;

public class HierarchyInstrumentDTO {
	
	private Integer sourceId;
	private String sourceAlias;
	private String sourceUrl;
	private Integer sourceParentId;
	private String sourceParentAlias;
	
	public HierarchyInstrumentDTO() {		
	}

	public Integer getSourceId() {
		return sourceId;
	}

	public void setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
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

	public Integer getSourceParentId() {
		return sourceParentId;
	}

	public void setSourceParentId(Integer sourceParentId) {
		this.sourceParentId = sourceParentId;
	}

	public String getSourceParentAlias() {
		return sourceParentAlias;
	}

	public void setSourceParentAlias(String sourceParentAlias) {
		this.sourceParentAlias = sourceParentAlias;
	}

}
