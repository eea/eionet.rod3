package eionet.rod.model;

/**
 *
 * @author altnyris
 *
 */
public class InstrumentDTO {

    //Fields from T_SOURCE table
    private Integer sourceId;
    private String sourceTitle;
    private String sourceAlias;
    private String sourceLegalName;


    /**
     * Constructor.
     */
    public InstrumentDTO() {
    }


    public Integer getSourceId() {
        return sourceId;
    }


    public void setSourceId(Integer sourceId) {
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


    public String getSourceLegalName() {
        return sourceLegalName;
    }


    public void setSourceLegalName(String sourceLegalName) {
        this.sourceLegalName = sourceLegalName;
    }




}
