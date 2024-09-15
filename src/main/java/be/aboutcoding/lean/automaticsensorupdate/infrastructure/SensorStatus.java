package be.aboutcoding.lean.automaticsensorupdate.infrastructure;

import be.aboutcoding.lean.automaticsensorupdate.model.TS50X;

public record SensorStatus(Long id, String status) {

    public static SensorStatus from(TS50X sensor) {
        return new SensorStatus(sensor.getId(), sensor.getStatus().toString());
    }
}
