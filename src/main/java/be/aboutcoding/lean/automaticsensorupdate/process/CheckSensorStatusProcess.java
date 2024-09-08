package be.aboutcoding.lean.automaticsensorupdate.process;

import be.aboutcoding.lean.automaticsensorupdate.model.ShippingStatus;
import be.aboutcoding.lean.automaticsensorupdate.model.TS50X;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CheckSensorStatusProcess {

    private final SensorInformationClient sensorClient;
    private final TaskClient taskClient;

    public CheckSensorStatusProcess(SensorInformationClient sensorClient, TaskClient taskClient) {
        this.sensorClient = sensorClient;
        this.taskClient = taskClient;
    }

    public List<TS50X> checkFor(List<Long> sensorIds) {

        var targetSensors = sensorClient.getSensorsWithIdIn(sensorIds);

        for (var sensor : targetSensors) {
            if (!sensor.hasValidFirmware()) {
                taskClient.scheduleFirmwareUpdateFor(sensor.getId());
                sensor.setStatus(ShippingStatus.UPDATING_FIRMWARE);
            }
            if (!sensor.isUpdatingFirmware() && !sensor.hasLatestConfiguration()) {
                taskClient.scheduleConfigurationUpdateFor(sensor.getId());
                sensor.setStatus(ShippingStatus.UPDATING_CONFIGURATION);
            }
        }
        return targetSensors;
    }
}
