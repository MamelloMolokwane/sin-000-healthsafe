package co.wethinkcode.healthsafe;

import com.google.gson.JsonObject;
import io.javalin.Javalin;

import java.util.Map;

public class AlertLevelServiceApp {

    public static void main(String[] args) {
        String url = "http://localhost:7031/wards";
        WardClient client = new WardClient();
        JsonObject wardsAndDepartments = client.getWardData(url);
        System.out.println(wardsAndDepartments);
        Map<String, Integer> occurrence = client.countWingAndDepartments(wardsAndDepartments);
        int alertLevel = client.keepBelow8(occurrence.values().stream().mapToInt(Integer::intValue).sum());

        Javalin app = Javalin.create().start(7032);

        app.get("/health", ctx -> ctx.result("OK"));
        app.get("/alert-level", ctx -> {
            ctx.contentType("application/json");
            ctx.result(Map.of("level", alertLevel).toString());
        });

        // TODO (Tracks the hospital Emergency Status (0-8, 8 = full Code Blue).)
        // Add domain endpoints for alert-level-service here.
    }

}
