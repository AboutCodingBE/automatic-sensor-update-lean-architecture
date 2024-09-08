package be.aboutcoding.lean.automaticsensorupdate.infrastructure;

import be.aboutcoding.lean.automaticsensorupdate.model.TS50X;

public record Task(Long id, TaskType type, String configurationFilename){

    public static Task createConfigUpdateTaskFor(Long sensorId) {
        return new Task(sensorId, TaskType.CONFIGURATION_UPDATE, TS50X.TARGET_CONFIGURATION);
    }

    public static Task createFirmwareUpdateTaskFor(Long sensorId) {
        return new Task(sensorId, TaskType.FIRMWARE_UPDATE, null);
    }
}
