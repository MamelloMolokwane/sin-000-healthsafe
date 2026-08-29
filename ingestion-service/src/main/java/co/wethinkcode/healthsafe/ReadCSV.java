package co.wethinkcode.healthsafe;

import org.apache.commons.csv.*;

import java.io.*;
import java.util.List;

public class ReadCSV {
//    private final String csvFile = "wards-outdated.csv";



    public List<CSVRecord> read(String csvFile) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(csvFile);
        if (inputStream == null) {
            throw new IllegalStateException("Could not find: " + csvFile);
        }

        try (Reader csvReader = new InputStreamReader(inputStream);
             CSVParser csvParser = new CSVParser(csvReader, CSVFormat.DEFAULT.builder().setHeader().setTrim(true).setSkipHeaderRecord(true).build())) {
            return csvParser.getRecords();
        } catch (IOException e) {
            throw new RuntimeException("There was an error reading the file: ",e);
        }
    }
}
