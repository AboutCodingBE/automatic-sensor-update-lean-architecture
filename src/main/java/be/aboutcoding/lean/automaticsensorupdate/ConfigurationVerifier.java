package be.aboutcoding.lean.automaticsensorupdate;

import org.springframework.stereotype.Component;

@Component
public class ConfigurationVerifier {

    private static final String REQUIRED_CONFIGURATION = "config123.cfg";

    public boolean isValid(String configuration) {
        if (configuration == null || configuration.isEmpty()) {
            return false;
        }

        return REQUIRED_CONFIGURATION.equals(configuration);
    }
}
