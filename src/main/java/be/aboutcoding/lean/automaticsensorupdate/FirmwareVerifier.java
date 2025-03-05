package be.aboutcoding.lean.automaticsensorupdate;

import org.springframework.stereotype.Component;

@Component
public class FirmwareVerifier {

    private static final String REQUIRED_FIRMWARE_VERSION = "59.1.12Rev4";

    public boolean isValid(String currentVersion) {
        if (currentVersion == null || currentVersion.isEmpty()) {
            return false;
        }

        return compareVersions(currentVersion, REQUIRED_FIRMWARE_VERSION) >= 0;
    }

    private int compareVersions(String version1, String version2) {
        // Split versions into components
        String[] v1Parts = parseFirmwareVersion(version1);
        String[] v2Parts = parseFirmwareVersion(version2);

        // Compare each component
        for (int i = 0; i < Math.min(v1Parts.length, v2Parts.length); i++) {
            int num1 = Integer.parseInt(v1Parts[i]);
            int num2 = Integer.parseInt(v2Parts[i]);

            if (num1 != num2) {
                return Integer.compare(num1, num2);
            }
        }

        // If all compared components are equal, the longer version is considered greater
        return Integer.compare(v1Parts.length, v2Parts.length);
    }

    private String[] parseFirmwareVersion(String version) {
        // Replace "Rev" with "." to make it a consistent delimiter
        String normalizedVersion = version.replace("Rev", ".");

        // Split by dot and remove any non-numeric chars
        String[] parts = normalizedVersion.split("\\.");
        String[] result = new String[parts.length];

        for (int i = 0; i < parts.length; i++) {
            // Extract only numeric part
            result[i] = parts[i].replaceAll("\\D+", "");
        }

        return result;
    }
}
