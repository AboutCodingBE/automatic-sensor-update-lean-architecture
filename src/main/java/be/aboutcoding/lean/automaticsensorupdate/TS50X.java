package be.aboutcoding.lean.automaticsensorupdate;

public class TS50X {
    private String id;
    private String firmwareVersion;
    private String configuration;
    private String status;

    public TS50X() {
    }

    public TS50X(String id, String firmwareVersion, String configuration) {
        this.id = id;
        this.firmwareVersion = firmwareVersion;
        this.configuration = configuration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
