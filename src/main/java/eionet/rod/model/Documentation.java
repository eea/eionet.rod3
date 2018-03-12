package eionet.rod.model;

public class Documentation implements java.io.Serializable { 

		private static final long serialVersionUID = 1L;
		
		private String areaId;
	    private String screenId;
	    private String description;
	    private String html;


	    /**
	     *Constructor
	     */
	    public Documentation() {
	    }


	    public String getAreaId() {
	        return areaId;
	    }


	    public void setAreaId(String areaId) {
	        this.areaId = areaId;
	    }


	    public String getScreenId() {
	        return screenId;
	    }


	    public void setScreenId(String screenId) {
	        this.screenId = screenId;
	    }


	    public String getDescription() {
	        return description;
	    }


	    public void setDescription(String description) {
	        this.description = description;
	    }


	    public String getHtml() {
	        return html;
	    }


	    public void setHtml(String html) {
	        this.html = html;
	    }


}
