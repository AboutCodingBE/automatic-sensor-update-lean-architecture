package be.aboutcoding.lean.automaticsensorupdate.process;

import be.aboutcoding.lean.automaticsensorupdate.infrastructure.SensorInformation;
import be.aboutcoding.lean.automaticsensorupdate.model.TS50X;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SensorInformationClient {

    private final RestTemplate restTemplate;

    public SensorInformationClient(@Value("${api.base-url}") String baseUrl, RestTemplateBuilder templateBuilder) {
        this.restTemplate = templateBuilder.rootUri(baseUrl).build();
    }

    public List<TS50X> getSensorsWithIdIn(List<Long> ids) {
        return ids.stream()
                .map(this::getInformationFor)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(SensorInformation::toTS50X)
                .collect(Collectors.toList());
    }

    private Optional<SensorInformation> getInformationFor(Long id) {
        var response = restTemplate.getForEntity("/sensor/{id}", SensorInformation.class, id);
        if (response.getStatusCode().value() == 404) {
            log.error("The sensor with id {} could not be found", id);
            return Optional.empty();
        }
        else if (response.getStatusCode().is5xxServerError()) {
            log.error("The id {} return no sensor information because it triggered an error", id);
            return Optional.empty();
        }
        return Optional.ofNullable(response.getBody());
    }
}
