package eionet.rod.model;

/**
 * @author jrobles
 */
public class Spatial {

    private Integer spatialId;
    private String name;
    private String type;
    private String twoLetter;
    private String memberCountry;


    /**
     * Constructor.
     */

    public Spatial() {

    }

    public Spatial(int spatialId, String name, String type, String twoLetter, String memberCountry) {
        this.spatialId = spatialId;
        this.name = name;
        this.type = type;
        this.twoLetter = twoLetter;
        this.memberCountry = memberCountry;

    }

    /**
     * @return the spatialId
     */
    public Integer getSpatialId() {
        return spatialId;
    }

    /**
     * @param spatialId the spatialId to set
     */
    public void setSpatialId(Integer spatialId) {
        this.spatialId = spatialId;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the twoLetter
     */
    public String getTwoLetter() {
        return twoLetter;
    }

    /**
     * @param twoLetter the twoLetter to set
     */
    public void setTwoLetter(String twoLetter) {
        this.twoLetter = twoLetter;
    }

    /**
     * @return the memberCountry
     */
    public String getMemberCountry() {
        return memberCountry;
    }

    /**
     * @param memberCountry the memberCountry to set
     */
    public void setMemberCountry(String memberCountry) {
        this.memberCountry = memberCountry;
    }


}
