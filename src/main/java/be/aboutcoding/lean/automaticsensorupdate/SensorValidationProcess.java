package be.aboutcoding.lean.automaticsensorupdate;

import com.opencsv.exceptions.CsvException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class SensorValidationProcess {

    private final IdParser idParser;
    private final SensorInformationClient sensorInfoClient;
    private final FirmwareVerifier firmwareVerifier;
    private final ConfigurationVerifier configVerifier;
    private final TaskClient taskClient;

    public SensorValidationProcess(
            IdParser idParser,
            SensorInformationClient sensorInfoClient,
            FirmwareVerifier firmwareVerifier,
            ConfigurationVerifier configVerifier,
            TaskClient taskClient) {
        this.idParser = idParser;
        this.sensorInfoClient = sensorInfoClient;
        this.firmwareVerifier = firmwareVerifier;
        this.configVerifier = configVerifier;
        this.taskClient = taskClient;
    }

    public List<TS50X> validateSensors(MultipartFile file) throws IOException, CsvException {
        // Parse IDs from CSV
        List<String> ids = idParser.parseIds(file);

        // Get sensor information for all IDs
        List<TS50X> sensors = sensorInfoClient.getSensorsInformation(ids);

        // Validate each sensor and set status accordingly
        for (TS50X sensor : sensors) {
            validateSensor(sensor);
        }

        return sensors;
    }

    private void validateSensor(TS50X sensor) {
        // Check firmware version
        if (sensor.getFirmwareVersion() == null) {
            sensor.setStatus("firmware_unknown");
            return;
        }

        if (!firmwareVerifier.isValid(sensor.getFirmwareVersion())) {
            taskClient.scheduleFirmwareUpdate(sensor);
            return;
        }

        // Check configuration (only if firmware is valid)
        if (sensor.getConfiguration() == null || !configVerifier.isValid(sensor.getConfiguration())) {
            taskClient.scheduleConfigurationUpdate(sensor);
            return;
        }

        // Both firmware and configuration are valid
        sensor.setStatus("ready");
    }
}
