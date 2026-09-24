package co.wethinkcode.healthsafe;

import co.wethinkcode.healthsafe.mq.MqConfig;
import com.google.gson.JsonObject;
import io.javalin.Javalin;

public class StaffingServiceApp {

    public static void main(String[] args) {
        String wardServiceUrl = "http://localhost:7031/wards";
        String alertLevelServiceUrl = "http://localhost:7032/alert-level";
        StaffingClient client = new StaffingClient();
        JsonObject wards = client.getData(wardServiceUrl);
        JsonObject alertLevel = client.getData(alertLevelServiceUrl);
        JsonObject assignStaff = client.computeSchedule(wards, alertLevel.get("level").getAsInt());

        MqConfig mq = new MqConfig();
//        mq.publish(assignStaff.toString());

        Javalin app = Javalin.create().start(7033);

        app.get("/health", ctx -> ctx.result("OK"));
        app.get("/staffing/ward/{id}", ctx -> {
            ctx.contentType("application/json");

            JsonObject ward = assignStaff.get(ctx.pathParam("id")).getAsJsonObject();
            String message = "Ward " +
                    ctx.pathParam("id") +
                    " status is " +
                    ward.get("Emergency Status").toString() +
                    " " +
                    ward.get("Doctor Assigned").toString() +
                    " doctors needed";
            mq.publish(message);

            ctx.result(assignStaff.get(ctx.pathParam("id")).toString());
        });

        // TODO (Provides on-call schedules for doctors based on ward and status.)
        // Add domain endpoints for staffing-service here.

    }
}

// MQ TODO: publishes to ActiveMQ topic MqConfig.TOPIC at MqConfig.BROKER_URL (see co.wethinkcode.healthsafe.mq.MqConfig)
