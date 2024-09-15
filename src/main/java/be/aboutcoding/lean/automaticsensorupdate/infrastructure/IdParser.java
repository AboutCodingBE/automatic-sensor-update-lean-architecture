package be.aboutcoding.lean.automaticsensorupdate.infrastructure;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class IdParser implements Function<MultipartFile, List<Long>> {

    @Override
    public List<Long> apply(MultipartFile file) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             var csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

            var csvRecords = csvParser.getRecords();
            var ids = new ArrayList<Long>();

            for (CSVRecord csvRecord : csvRecords) {
                ids.add(Long.parseLong(csvRecord.get("Id")));
            }

            return ids;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse id file: " + e.getMessage());
        }
    }
}
