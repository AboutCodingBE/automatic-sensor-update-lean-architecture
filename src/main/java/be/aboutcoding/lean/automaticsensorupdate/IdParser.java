package be.aboutcoding.lean.automaticsensorupdate;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class IdParser {
    public List<String> parseIds(MultipartFile file) throws IOException, CsvException {
        List<String> ids = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            List<String[]> rows = reader.readAll();

            // Skip header row
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                if (row.length > 0) {
                    // Assuming ID is the first column and trimming whitespace
                    String id = row[0].trim();
                    if (!id.isEmpty()) {
                        ids.add(id);
                    }
                }
            }
        }

        return ids;
    }
}
