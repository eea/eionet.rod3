package eionet.rod.model;

public class InstrumentsListDTO {

    //Fields from T_SOURCE_CLASS table
    private Integer classId;
    private String classificator;
    private String className;

    //Fields from T_SOURCE_LNK table
    private Integer parentId;

    public InstrumentsListDTO() {
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public String getClassificator() {
        return classificator;
    }

    public void setClassificator(String classificator) {
        this.classificator = classificator;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

}
