package be.aboutcoding.lean.automaticsensorupdate.process;

import be.aboutcoding.lean.automaticsensorupdate.infrastructure.SensorInformation;
import be.aboutcoding.lean.automaticsensorupdate.infrastructure.Task;
import be.aboutcoding.lean.automaticsensorupdate.model.ShippingStatus;
import be.aboutcoding.lean.automaticsensorupdate.model.TS50X;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckSensorStatusProcessTest {

    @Mock
    private SensorInformationClient sensorInformationClient;

    @Mock
    private TaskClient taskClient;

    @InjectMocks
    private CheckSensorStatusProcess sensorStatusProcess;

    private static final String OUTDATED_FIRMWARE_VERSION = "50.1.1Rev0";
    private static final String VALID_FIRMWARE_VERSION = "60.1.1Rev12";

    private static final String NON_MATCHING_CONFIG_FILE = "random_config.cfg";
    private static final String MATCHING_CONFIG_FILE = "ts50x-20230811T10301211.cfg";

    private static final Long SENSOR_ID = 1L;

    @Test
    void should_correctly_handle_sensor_with_outdated_firmware() {
        // setup
        var sensorIds = List.of(SENSOR_ID);
        var sensorInfo = aSensorWith(SENSOR_ID, OUTDATED_FIRMWARE_VERSION, NON_MATCHING_CONFIG_FILE);

        when(sensorInformationClient.getSensorsWithIdIn(sensorIds)).thenReturn(List.of(sensorInfo));

        // invoke
        var result = sensorStatusProcess.checkFor(sensorIds);

        // assert
        var expectedSensor = aSensorWith(SENSOR_ID, OUTDATED_FIRMWARE_VERSION, NON_MATCHING_CONFIG_FILE);
        expectedSensor.setStatus(ShippingStatus.UPDATING_FIRMWARE);

        verify(taskClient, times(1)).scheduleFirmwareUpdateFor(SENSOR_ID);
        assertThat(result).containsExactly(expectedSensor);
    }

    @Test
    void should_correctly_handle_sensor_with_valid_firmware() {
        // setup
        var sensorIds = List.of(SENSOR_ID);
        var sensor = aSensorWith(SENSOR_ID, VALID_FIRMWARE_VERSION, NON_MATCHING_CONFIG_FILE);
        ;
        when(sensorInformationClient.getSensorsWithIdIn(sensorIds)).thenReturn(List.of(sensor));

        // invoke
        var result = sensorStatusProcess.checkFor(sensorIds);

        // assert
        var expectedSensor = aSensorWith(SENSOR_ID, VALID_FIRMWARE_VERSION, NON_MATCHING_CONFIG_FILE);
        expectedSensor.setStatus(ShippingStatus.UPDATING_CONFIGURATION);

        verify(taskClient, times(1)).scheduleConfigurationUpdateFor(SENSOR_ID);
        assertThat(result).containsExactly(expectedSensor);
    }
    
    @Test
    void should_correctly_handle_sensor_that_is_ready_to_ship() {
        // setup
        var sensorIds = List.of(SENSOR_ID);
        var sensor = aSensorWith(SENSOR_ID, VALID_FIRMWARE_VERSION, MATCHING_CONFIG_FILE);
        sensor.setStatus(ShippingStatus.UNKNOWN);

        when(sensorInformationClient.getSensorsWithIdIn(sensorIds)).thenReturn(List.of(sensor));

        // invoke
        var result = sensorStatusProcess.checkFor(sensorIds);

        // assert
        sensor.setStatus(ShippingStatus.READY);
        assertThat(result).containsExactly(sensor);
    }

    private TS50X aSensorWith(Long sensorId, String firmwareVersion, String configurationFile) {
        return  new TS50X(sensorId, firmwareVersion, configurationFile);
    }

}