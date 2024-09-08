package be.aboutcoding.lean.automaticsensorupdate.process;

import be.aboutcoding.lean.automaticsensorupdate.model.ShippingStatus;
import be.aboutcoding.lean.automaticsensorupdate.model.TS50X;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
public class CheckSensorStatus {

    private final SensorInformationClient sensor;
    private final TaskClient taskClient;

    public CheckSensorStatus(SensorInformationClient sensor, TaskClient taskClient) {
        this.sensor = sensor;
        this.taskClient = taskClient;
    }

    public List<TS50X> start(List<Long> sensorIds) {

        var targetSensors = sensor.getSensorsWithIdIn(sensorIds);

        for (var sensor : targetSensors) {
            if (!sensor.hasValidFirmware()) {
                taskClient.scheduleFirmwareUpdateFor(sensor.getId());
                sensor.setStatus(ShippingStatus.UPDATING_FIRMWARE);
            }
        }

        for (var sensor : targetSensors) {
            if (!sensor.isUpdatingFirmware() && !sensor.hasLatestConfiguration()) {
                taskClient.scheduleConfigurationUpdateFor(sensor.getId());
                sensor.setStatus(ShippingStatus.UPDATING_CONFIGURATION);
            }
        }

        return targetSensors;
    }
}
