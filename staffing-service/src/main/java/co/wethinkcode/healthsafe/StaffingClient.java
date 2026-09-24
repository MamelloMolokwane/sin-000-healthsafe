package co.wethinkcode.healthsafe;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class StaffingClient {

    private Gson gson;

    public StaffingClient() {
        gson = new Gson();
    }


    public JsonObject getData(String url) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            // Build the request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .header("accept", "application/json")
                    .build();

            // Send the request and get the response back as String
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // then turn the string response into a JsonObject using Gson and return it;
            JsonObject jsonObject = gson.fromJson(response.body(), JsonObject.class);
            return jsonObject;
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 1. Get ward data from ward-service
    // 2. Get hospital alert-level from alert-level-service
    // 3. Compute a schedule
        // What do they mean compute a schedule? How do we compute a schedule. Why don't they ever specify anything here🫩?
        // Not mentioned how we do that but method could look like this computeSchedule(wards, hospitalEmergencyStatus).
        // computeSchedule() must output the wards hashmap/JsonObject with emergency status and number of doctors needed added in the hashmap/JsonObject?🤔.
        // So maybe the emergency status should determine the number of doctors?🤔.
            // 0-2 = 1 doctor
            // 3-5 = 3 doctors
            // 6-7 = 4 doctors
            // 8 = 5 doctors

    public int assignStaff(int emergencyStatus) {
        if (emergencyStatus < 3) {
            return 1;
        } else if (emergencyStatus < 6) {
            return 3;
        } else if (emergencyStatus < 8) {
            return 4;
        } else {
            return 5;
        }
    }

    public JsonObject computeSchedule(JsonObject wards, int emergencyStatus) {
        // Since you can't add things to a JsonObject while you're looping through it, I'll need to create a copy of the JsonObject.
        int doctorsNeeded = assignStaff(emergencyStatus);
        JsonObject newWards = wards.deepCopy();

        for (Map.Entry<String, JsonElement> ward: wards.entrySet()) {
            String wardId = ward.getKey();
            newWards.get(wardId).getAsJsonObject().addProperty("Emergency Status", emergencyStatus);
            newWards.get(wardId).getAsJsonObject().addProperty("Doctor Assigned", doctorsNeeded);
        }

        return newWards;
    }
}
