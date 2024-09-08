package be.aboutcoding.lean.automaticsensorupdate.process;

import be.aboutcoding.lean.automaticsensorupdate.infrastructure.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class TaskClient {
    private final RestTemplate restTemplate;

    public TaskClient(@Value("${api.base-url}") String baseUrl, RestTemplateBuilder templateBuilder) {
        this.restTemplate = templateBuilder.rootUri(baseUrl).build();
    }

    public void scheduleFirmwareUpdateFor(Long id) {
        var response = restTemplate.postForEntity("/task", Task.createFirmwareUpdateTaskFor(id), Long.class);
        if (response.getStatusCode().is5xxServerError()) {
            log.error("Creating a firmware update task for sensor with id {} failed", id);
        }
    }


    public void scheduleConfigurationUpdateFor(Long id) {
        var response = restTemplate.postForEntity("/task", Task.createConfigUpdateTaskFor(id), Long.class);
        if (response.getStatusCode().is5xxServerError()) {
            log.error("Creating a firmware update task for sensor with id {} failed", id);
        }
    }
}
