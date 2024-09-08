package be.aboutcoding.lean.automaticsensorupdate.infrastructure;

import be.aboutcoding.lean.automaticsensorupdate.model.TS50X;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SensorInformation(@JsonProperty Long serial,
                                @JsonProperty String type,
                                @JsonProperty Integer statusId,
                                @JsonProperty String currentConfiguration,
                                @JsonProperty String  currentFirmware) {

    public static TS50X toTS50X(SensorInformation info) {
        return new TS50X(info.serial, info.currentFirmware(), info.currentConfiguration());
    }
}
