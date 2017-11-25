package eionet.rod.model;

import java.util.List;

/**
 *
 * @author altnyris
 *
 */
public class ClientDTO {

    private Integer clientId;
    private String name;
    private String acronym;

    private String shortName;
    private String address;
    private String url;
    private String email;
    private String postalCode;
    private String city;
    private String description;
    private String country;

    private List<ObligationFactsheetDTO> directObligations;
    private List<ObligationFactsheetDTO> indirectObligations;
    private List<InstrumentDTO> directInstruments;
    private List<InstrumentDTO> indirectInstruments;

    /**
     * Constructor.
     */
    public ClientDTO() {
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<ObligationFactsheetDTO> getDirectObligations() {
        return directObligations;
    }

    public void setDirectObligations(List<ObligationFactsheetDTO> directObligations) {
        this.directObligations = directObligations;
    }

    public List<ObligationFactsheetDTO> getIndirectObligations() {
        return indirectObligations;
    }

    public void setIndirectObligations(
            List<ObligationFactsheetDTO> indirectObligations) {
        this.indirectObligations = indirectObligations;
    }

    public List<InstrumentDTO> getDirectInstruments() {
        return directInstruments;
    }

    public void setDirectInstruments(List<InstrumentDTO> directInstruments) {
        this.directInstruments = directInstruments;
    }

    public List<InstrumentDTO> getIndirectInstruments() {
        return indirectInstruments;
    }

    public void setIndirectInstruments(List<InstrumentDTO> indirectInstruments) {
        this.indirectInstruments = indirectInstruments;
    }


}
