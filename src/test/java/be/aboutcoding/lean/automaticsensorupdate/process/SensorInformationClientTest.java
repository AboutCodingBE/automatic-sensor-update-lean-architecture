package be.aboutcoding.lean.automaticsensorupdate.process;

import be.aboutcoding.lean.automaticsensorupdate.model.TS50X;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest()
@ActiveProfiles("test")
@WireMockTest(httpPort = 8086)
class SensorInformationClientTest {

    @Autowired
    private SensorInformationClient client;

    @Test
    void should_retun_a_list_of_sensors(WireMockRuntimeInfo runtimeInfo) {
        // setup
        var ids = List.of(12345L);

        stubFor(get(urlPathMatching("/api/sensor/12345"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(sensorInformationFor(12345))));

        var sensors = client.getSensorsWithIdIn(ids);

        assertThat(sensors).hasSize(1)
                .extracting(TS50X::getId)
                .contains(12345L);


    }

    private String sensorInformationFor(int id) {
        return """
                {
                  "serial": %d,
                  "type": "TS50X",
                  "status_id": 1,
                  "current_configuration": "some_oonfiguration.cfg",
                  "current_firmware": "59.1.12Rev4",
                  "created_at": "2022-03-31 11:26:08",
                  "updated_at": "2022-10-18 17:53:48",
                  "status_name": "Idle",
                  "next_task": null,
                  "task_count": 5,
                  "activity_status": "Online",
                  "task_queue": [124355, 44435322]
                }
                """.formatted(id);
    }

}