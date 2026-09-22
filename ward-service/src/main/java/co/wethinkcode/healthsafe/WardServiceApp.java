package co.wethinkcode.healthsafe;

import com.google.gson.JsonObject;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;

public class WardServiceApp {

    public static void main(String[] args) {
        String url = "http://localhost:7030/wards";
        IngestionClient client = new IngestionClient();
        JsonObject allWards = client.getIngestionData(url);

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
}

// MQ TODO: subscribes to ActiveMQ topic MqConfig.TOPIC at MqConfig.BROKER_URL (see co.wethinkcode.healthsafe.mq.MqConfig)
// MQ TODO: publishes to ActiveMQ queue MqConfig.QUEUE when it detects an equipment failure on one of its wards.
