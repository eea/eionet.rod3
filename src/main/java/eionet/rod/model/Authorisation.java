package eionet.rod.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Object to hold user and roles.
 */
public class Authorisation {

    private String userId;

    /**
     * List of roles.
     */
    private List<String> authorisations;

    /**
     * Constructor of empty object.
     */
    public Authorisation() {
        this.authorisations = new ArrayList<>();
    }

    /**
     * Constructor.
     */
    public Authorisation(String userId, List<String> authorisations) {
        this.userId = userId;
        this.authorisations = authorisations;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getAuthorisations() {
        return authorisations;
    }

    public void setAuthorisations(List<String> authorisations) {
        this.authorisations = authorisations;
    }

}
