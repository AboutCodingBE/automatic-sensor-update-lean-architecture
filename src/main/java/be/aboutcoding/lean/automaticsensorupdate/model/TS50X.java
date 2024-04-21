package be.aboutcoding.lean.automaticsensorupdate.model;

import lombok.Getter;
import lombok.Setter;

public class TS50X {
    private static final String VALID_FIRMWARE_VERSION = "59.1.12Rev4";
    public static final String TARGET_CONFIGURATION = "ts50x-20230811T10301211.cfg";

    @Getter
    private final Long id;
    private final String currentFirmwareVersion;
    private final String currentConfiguration;
    @Getter
    @Setter
    private ShippingStatus status;

    public TS50X(Long id, String firmwareVersion, String configuration) {
        if (id == null || id < 0) {
            throw new IllegalArgumentException("Sensor id cannot be null or negative");
        }
        if (firmwareVersion == null) {
            throw new IllegalArgumentException("Sensor firmware version cannot be null");
        }
        this.id = id;
        this.currentFirmwareVersion = firmwareVersion;
        this.currentConfiguration = configuration;
    }

    public boolean isUpdatingFirmware() {
        return this.status.equals(ShippingStatus.UPDATING_FIRMWARE);
    }

    public boolean hasLatestConfiguration() {
        return this.currentConfiguration.equals(TARGET_CONFIGURATION);
    }

    public boolean hasValidFirmware() {
        if (!VALID_FIRMWARE_VERSION.equals(currentFirmwareVersion)) {
            var currentVersion = new SemanticVersion(currentFirmwareVersion);
            var validVersion = new SemanticVersion(VALID_FIRMWARE_VERSION);
            return currentVersion.isEqualOrLargerThan(validVersion);
        }
        return true;
    }

    private static class SemanticVersion {
        int major = 0;
        int minor = 0;
        int patch = 0;
        int revision = 0;
        String rawVersion;

        SemanticVersion(String version) {
            this.rawVersion = version;
            parse();
        }

        void parse() {
            if (this.rawVersion != null && !this.rawVersion.isEmpty()) {
                String[] versionParts = rawVersion.split("\\.");
                try {
                    this.major = Integer.parseInt(versionParts[0]);
                    this.minor = Integer.parseInt(versionParts[1]);
                    String[] patchRevision = versionParts[2].split("Rev");
                    this.patch = Integer.parseInt(patchRevision[0]);

                    if (patchRevision.length == 2) {
                        this.revision = Integer.parseInt(patchRevision[1]);
                    }
                } catch (NumberFormatException formatException) {
                    this.major = 0;
                    this.minor = 0;
                    this.patch = 0;
                    this.revision = 0;
                }
            }
        }

        boolean isEqualOrLargerThan(SemanticVersion other) {
            if (other.major == this.major &&
                    other.minor == this.minor &&
                    other.patch == this.patch &&
                    other.revision == this.revision) {
                return true;
            }
            if (this.major > other.major) {
                return true;
            }
            if (this.major == other.major && this.minor > other.minor) {
                return true;
            }
            if (this.major == other.major && this.minor == other.minor && this.patch > other.patch) {
                return true;
            } else return this.major == other.major && this.minor == other.minor && this.patch == other.patch &&
                    this.revision > other.revision;
        }
    }
}
