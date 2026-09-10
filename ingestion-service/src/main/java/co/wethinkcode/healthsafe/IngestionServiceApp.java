package co.wethinkcode.healthsafe;

import io.javalin.Javalin;
import org.apache.commons.csv.CSVRecord;
import java.util.List;
import java.util.Map;

public class IngestionServiceApp {

    public static void main(String[] args) {
        String csvFile = "wards-outdated.csv";
        List<CSVRecord> csvRecords = new ReadCSV().read(csvFile);
        Map<String, Map<String, String>> records = new DataCleaner().clean(csvRecords);
        System.out.println(records);

        Javalin app = Javalin.create().start(7030);

        app.get("/health", ctx -> ctx.result("OK"));
        app.get("/ward-service", ctx ->
                ctx.json(records)
        );

        // TODO: read and clean src/main/resources/wards-outdated.csv (wards, wings, specialist departments data —
        // trim whitespace, fix casing, normalize dates/booleans) and expose the
        // cleaned records here for the other services to consume.
    }
}
