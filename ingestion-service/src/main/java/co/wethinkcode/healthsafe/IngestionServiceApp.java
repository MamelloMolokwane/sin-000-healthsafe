package co.wethinkcode.healthsafe;

import io.javalin.Javalin;
import org.apache.commons.csv.CSVRecord;
import java.util.List;

public class IngestionServiceApp {

    public static void main(String[] args) {
        String csvFile = "wards-outdated.csv";
        List<CSVRecord> csv = new ReadCSV().read(csvFile);
        System.out.println(csv);

        Javalin app = Javalin.create().start(7030);

        app.get("/health", ctx -> ctx.result("OK"));

        // TODO: read and clean src/main/resources/wards-outdated.csv (wards, wings, specialist departments data —
        // trim whitespace, fix casing, normalize dates/booleans) and expose the
        // cleaned records here for the other services to consume.
    }
}
