package be.aboutcoding.lean.automaticsensorupdate.infrastructure;


import be.aboutcoding.lean.automaticsensorupdate.process.CheckSensorStatusProcess;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/sensor/status")
public class SensorUpdateApi {

    private CheckSensorStatusProcess statusCheck;
    private IdParser parser = new IdParser();

    public SensorUpdateApi(CheckSensorStatusProcess statusCheck) {
        this.statusCheck = statusCheck;
    }

    @PostMapping
    public List<SensorStatus> getStatusFor(@RequestParam("file") MultipartFile file) {
        var ids = parser.apply(file);

        var sensors =  statusCheck.checkFor(ids);
        return sensors.stream()
                .map(SensorStatus::from)
                .toList();
    }
}
