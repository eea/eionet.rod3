package eionet.rod.model;

import eionet.rod.util.RODUtil;

public class UndoDTO {
	
	private long undoTime;
    private String tab;
    private String col;
    private String operation;
    private String quotes;
    private String primaryKey;
    private String value;
    private Integer subTransNr;
    private String show; 
    private String date;
    private String userName;
    private String description;
    
    /**
     * Constructor.
     */
	public UndoDTO() {
	}

	public long getUndoTime() {
		return undoTime;
	}

	public void setUndoTime(long undoTime) {
		this.undoTime = undoTime;
	}

	public String getTab() {
		return tab;
	}

	public void setTab(String tab) {
		this.tab = tab;
	}

	public String getCol() {
		return col;
	}

	public void setCol(String col) {
		this.col = col;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getQuotes() {
		return quotes;
	}

	public void setQuotes(String quotes) {
		this.quotes = quotes;
	}

	public String getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getSubTransNr() {
		return subTransNr;
	}

	public void setSubTransNr(Integer subTransNr) {
		this.subTransNr = subTransNr;
	}

	public String getShow() {
		return show;
	}

	public void setShow(String show) {
		this.show = show;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String truncateText(String truncateText) {
	       return RODUtil.truncateText(truncateText);
	}

}
