package eionet.rod.model;

public class UndoDTO {
	
	private Integer undoTime;
    private String table;
    private String column;
    private String operation;
    private String quotes;
    private String primaryKey;
    private String value;
    private Integer subTransNr;
    private String show;    
    
    /**
     * Constructor.
     */
	public UndoDTO() {
	}

	public Integer getUndoTime() {
		return undoTime;
	}

	public void setUndoTime(Integer undoTime) {
		this.undoTime = undoTime;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
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


}
