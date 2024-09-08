package be.aboutcoding.lean.automaticsensorupdate.infrastructure;

import lombok.Getter;

public enum TaskType {
    CONFIGURATION_UPDATE("update_configuration"),
    FIRMWARE_UPDATE("update_firmware");

    @Getter
    private final String apiValue;

    private TaskType(String apiValue) {
        this.apiValue = apiValue;
    }
}
