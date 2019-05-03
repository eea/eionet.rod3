package eionet.rod.model;

public class InstrumentClassificationDTO {

    //Fields from T_SOURCE_CLASS table
    private Integer classId;
    private String classificator;
    private String className;

    public InstrumentClassificationDTO() {
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

}
