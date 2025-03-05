package be.aboutcoding.lean.automaticsensorupdate;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SensorInformationClient {

    // In a real implementation, this would call an external API
    // For now, we'll simulate the API call
    public List<TS50X> getSensorsInformation(List<String> ids) {
        List<TS50X> sensors = new ArrayList<>();

        for (String id : ids) {
            TS50X sensor = fetchSensorDetails(id);
            sensors.add(sensor);
        }

        return sensors;
    }

    private TS50X fetchSensorDetails(String id) {
        // This would be replaced with an actual API call
        return new TS50X(id, null, null);
    }
}