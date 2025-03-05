package be.aboutcoding.lean.automaticsensorupdate;

import com.opencsv.exceptions.CsvException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sensors")
public class SensorValidationAPI {

    private final SensorValidationProcess validationProcess;

    public SensorValidationAPI(SensorValidationProcess validationProcess) {
        this.validationProcess = validationProcess;
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateSensors(@RequestParam("file") MultipartFile file) {
        try {
            List<TS50X> validatedSensors = validationProcess.validateSensors(file);

            // Format the response as requested: "id status"
            List<String> formattedResults = validatedSensors.stream()
                    .map(sensor -> sensor.getId() + " " + sensor.getStatus())
                    .collect(Collectors.toList());

            return ResponseEntity.ok(formattedResults);
        } catch (IOException | CsvException e) {
            return ResponseEntity.badRequest().body("Error processing file: " + e.getMessage());
        }
    }
}
