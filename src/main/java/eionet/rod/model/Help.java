package eionet.rod.model;

public class Help {

	private String helpId;
	private String title;
	private String text;
	
	/**
     * Constructor.
     */
    public Help() {
    }

    /**
	 * @return the helpId
	 */
	public String getHelpId() {
		return helpId;
	}
	/**
	 * @param helpId the helpId to set
	 */
	public void setHelpId(String helpId) {
		this.helpId = helpId;
	}
    
    /**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * 
	 * @return text
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * 
	 * @param text
	 */
	public void setText(String text) {
		this.text = text;
	}
	
}
