package co.wethinkcode.healthsafe;

import co.wethinkcode.healthsafe.mq.MqConfig;
import com.google.gson.JsonObject;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;

import java.util.Map;

public class WardServiceApp {

    public static void main(String[] args) {
        String url = "http://localhost:7030/wards";
        IngestionClient client = new IngestionClient();
        JsonObject allWards = client.getIngestionData(url);

        MqConfig mq = new MqConfig();
        mq.subscribe();
        checkEquipment(mq);

        Javalin app = Javalin.create(
                config -> config.jsonMapper(new JavalinJackson())
        ).start(7031);

        app.get("/health", ctx -> ctx.result("OK"));
        app.get("/wards", ctx -> {
            ctx.contentType("application/json");
            ctx.result(allWards.toString());
        });
        app.get("/wards/{id}", ctx -> {
            ctx.contentType("application/json");
            ctx.result(client.getWard(url, ctx.pathParam("id")));
        });

        
        // TODO (Provides lists of wards and departments.)
        // Add domain endpoints for ward-service here.
    }

    private static void checkEquipment(MqConfig mq) {
        Map<String, Map<String, Boolean>> equipment = Map.of(
                "W-01", Map.of("Ventilator", true, "Heart Monitor", false),
                "W-02", Map.of("Ventilator", false)
        );

        for (Map.Entry<String, Map<String, Boolean>> ward : equipment.entrySet()) {
            for (Map.Entry<String, Boolean> item : ward.getValue().entrySet()) {
                if (item.getValue()) {
                    String message = ward.getKey() + ": " + item.getKey() + " is faulty";
                    mq.publish(message);
                }
            }
        }
    }
}

// MQ TODO: subscribes to ActiveMQ topic MqConfig.TOPIC at MqConfig.BROKER_URL (see co.wethinkcode.healthsafe.mq.MqConfig)
// MQ TODO: publishes to ActiveMQ queue MqConfig.QUEUE when it detects an equipment failure on one of its wards.
