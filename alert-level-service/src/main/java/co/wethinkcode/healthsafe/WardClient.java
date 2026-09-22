package co.wethinkcode.healthsafe;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.util.HashMap;
import java.util.Map;

public class WardClient {

    private Gson gson;

    public WardClient() {
        gson = new Gson();
    }

    // 1. Get the wing and department from the ward-server endpoint /wards.
    public JsonObject getWardData(String url) {

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

    // 2. Count the amount of times the wing and department show up.
    public Map<String, Integer> countWingAndDepartments(JsonObject jsonObject) {
        Map<String, Integer> occurrence = new HashMap<>();

        for (Map.Entry<String, JsonElement> ward: jsonObject.entrySet()) {
            String wingAndDepartment = ward.getValue().getAsJsonObject().get("wing").getAsString()
                    + " " + ward.getValue().getAsJsonObject().get("department").getAsString();

            if (occurrence.containsKey(wingAndDepartment)) {
                int count = occurrence.get(wingAndDepartment);
                occurrence.put(wingAndDepartment, count + 1);
            } else {
                occurrence.put(wingAndDepartment, 1);
            }
        }

        return occurrence;
    }

    // 3. Turn the count into an alert-service, remember that if the count > 8 then the alert-service is 8
    public Integer keepBelow8(Integer occurrence) {
        if (occurrence > 8) {
            return 8;
        }
        return occurrence;
    }

}
