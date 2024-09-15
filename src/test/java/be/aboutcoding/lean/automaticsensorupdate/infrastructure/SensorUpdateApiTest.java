package be.aboutcoding.lean.automaticsensorupdate.infrastructure;

import be.aboutcoding.lean.automaticsensorupdate.process.CheckSensorStatusProcess;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ResourceUtils;

import java.io.FileInputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest
class SensorUpdateApiTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CheckSensorStatusProcess statusProcess;
    @InjectMocks
    private SensorUpdateApi api;

    @Test
    void should_correctly_parse_sensor_ids() throws Exception {
        // setup
        var idFile = aMultipartFileOf("file", "examples/sensors.csv");

        mockMvc.perform(multipart("/sensor/status")
                        .file(idFile))
                .andExpect(status().isOk());

        verify(statusProcess, times(1)).checkFor(List.of(123435L, 67890L, 34234455677L));
    }

    private MockMultipartFile aMultipartFileOf(String fileParameterName, String location) throws Exception {
        var inputStream = new FileInputStream(ResourceUtils.getFile("classpath:" + location));
        return new MockMultipartFile(fileParameterName,
                "sensors.csv",
                MediaType.TEXT_PLAIN_VALUE,
                inputStream);
    }

}