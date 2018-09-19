package eionet.rod;

import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;

public class ECas20ServiceTicketValidator extends Cas20ServiceTicketValidator {

    private String urlSuffix;

    public ECas20ServiceTicketValidator(final String casServerUrlPrefix) {
        super(casServerUrlPrefix);
        try {
            urlSuffix = System.getProperty("cas.url.suffix", "serviceValidate");
        } catch (Exception e) {
            urlSuffix = "serviceValidate";
        }
    }

    protected String getUrlSuffix() {
        return urlSuffix;
    }

}
