package be.aboutcoding.lean.automaticsensorupdate;

import org.springframework.stereotype.Component;

@Component
public class TaskClient {

    // In a real implementation, this would call an external task scheduling service

    public TS50X scheduleFirmwareUpdate(TS50X sensor) {
        // Simulate a call to schedule a firmware update
        sensor.setStatus("updating_firmware");
        return sensor;
    }

    public TS50X scheduleConfigurationUpdate(TS50X sensor) {
        // Simulate a call to schedule a configuration update
        sensor.setStatus("updating_configuration");
        return sensor;
    }
}