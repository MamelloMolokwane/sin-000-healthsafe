package co.wethinkcode.healthsafe;

import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import org.apache.commons.csv.CSVRecord;
import java.util.List;
import java.util.Map;

public class IngestionServiceApp {

    public static void main(String[] args) {
        String csvFile = "wards-outdated.csv";
        List<CSVRecord> csvRecords = new ReadCSV().read(csvFile);
        Map<String, Map<String, String>> records = new DataCleaner().clean(csvRecords);
        prettyPrint(records);

        Javalin app = Javalin.create(
                config -> config.jsonMapper(new JavalinJackson())
        ).start(7030);

        app.get("/health", ctx -> ctx.result("OK"));
        app.get("/wards", ctx ->
                ctx.json(records)
        );

    }

    private static void prettyPrint(Map<String, Map<String, String>> records) {
        System.out.println("========== CLEANED WARD DATA ==========");

        records.forEach((ward, details) -> {
            System.out.println("\n[" + ward + "]");

            details.forEach((key, value) ->
                    System.out.printf("  %-20s : %s%n", key, value)
            );
        });

        System.out.println("=======================================");
    }
}
