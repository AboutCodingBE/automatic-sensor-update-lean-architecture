package be.aboutcoding.lean.automaticsensorupdate;

import com.opencsv.exceptions.CsvException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SensorValidationProcessTest {

    @Mock
    private IdParser idParser;

    @Mock
    private SensorInformationClient sensorInfoClient;

    @Mock
    private FirmwareVerifier firmwareVerifier;

    @Mock
    private ConfigurationVerifier configVerifier;

    @Mock
    private TaskClient taskClient;

    private SensorValidationProcess validationProcess;

    @BeforeEach
    void setUp() {
        validationProcess = new SensorValidationProcess(
                idParser, sensorInfoClient, firmwareVerifier, configVerifier, taskClient);
    }

    @Test
    void validateSensors_AllScenarios() throws IOException, CsvException {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "test.csv", "test.csv", "text/csv", "id,type\n1,TS50X\n2,TS50X\n3,TS50X".getBytes());

        List<String> ids = List.of("1", "2", "3");

        // Sensor with missing firmware
        TS50X sensor1 = new TS50X("1", null, "config123.cfg");

        // Sensor with invalid firmware
        TS50X sensor2 = new TS50X("2", "50.1.12Rev1", "config123.cfg");

        // Sensor with valid firmware but invalid config
        TS50X sensor3 = new TS50X("3", "60.1.12Rev1", "wrongconfig.cfg");

        List<TS50X> sensors = List.of(sensor1, sensor2, sensor3);

        when(idParser.parseIds(file)).thenReturn(ids);
        when(sensorInfoClient.getSensorsInformation(ids)).thenReturn(sensors);

        when(firmwareVerifier.isValid(null)).thenReturn(false);
        when(firmwareVerifier.isValid("50.1.12Rev1")).thenReturn(false);
        when(firmwareVerifier.isValid("60.1.12Rev1")).thenReturn(true);

        when(configVerifier.isValid("config123.cfg")).thenReturn(true);
        when(configVerifier.isValid("wrongconfig.cfg")).thenReturn(false);

        when(taskClient.scheduleFirmwareUpdate(any(TS50X.class))).thenAnswer(invocation -> {
            TS50X sensor = invocation.getArgument(0);
            sensor.setStatus("updating_firmware");
            return sensor;
        });

        when(taskClient.scheduleConfigurationUpdate(any(TS50X.class))).thenAnswer(invocation -> {
            TS50X sensor = invocation.getArgument(0);
            sensor.setStatus("updating_configuration");
            return sensor;
        });

        // When
        List<TS50X> result = validationProcess.validateSensors(file);

        // Then
        assertThat(result).hasSize(3);

        // Sensor 1: Missing firmware
        assertThat(result.get(0).getId()).isEqualTo("1");
        assertThat(result.get(0).getStatus()).isEqualTo("firmware_unknown");

        // Sensor 2: Invalid firmware
        assertThat(result.get(1).getId()).isEqualTo("2");
        assertThat(result.get(1).getStatus()).isEqualTo("updating_firmware");

        // Sensor 3: Valid firmware, invalid config
        assertThat(result.get(2).getId()).isEqualTo("3");
        assertThat(result.get(2).getStatus()).isEqualTo("updating_configuration");

        // Verify interactions
        verify(idParser).parseIds(file);
        verify(sensorInfoClient).getSensorsInformation(ids);
        verify(firmwareVerifier, times(2)).isValid(anyString());
        verify(firmwareVerifier).isValid(null);
        verify(configVerifier).isValid("wrongconfig.cfg");
        verify(taskClient).scheduleFirmwareUpdate(sensor2);
        verify(taskClient).scheduleConfigurationUpdate(sensor3);
    }
}