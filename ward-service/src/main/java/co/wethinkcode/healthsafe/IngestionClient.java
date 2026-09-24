package co.wethinkcode.healthsafe;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.util.Map;

public class IngestionClient {

    private Gson gson;
    private HttpClient client;

    public IngestionClient() {
        gson = new Gson();
        client = HttpClient.newHttpClient();
    }

    public JsonObject getIngestionData(String url) {

        try {
//            HttpClient client = HttpClient.newHttpClient();
            // Build request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .header("accept", "application/json")
                    .build();

            // Send the request and get the response back as string
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Turning the response into a Map<String, Map<String, String>> would be needlessly complex a JsonObject would work here.
            JsonObject responseData = gson.fromJson(response.body(), JsonObject.class);
            return responseData;

        } catch (InterruptedException | IOException e) {
//            return "There was a problem while getting the data: " + e;
            throw new RuntimeException("There was a problem while getting the data: " + e);
        }
    }

    public String getWard(String url, String id) {
        JsonObject wardData = getIngestionData(url);
        System.out.println("Ward Date: " + wardData);
        JsonObject wards = new JsonObject();
        for (Map.Entry<String, JsonElement> ward: wardData.entrySet()) {
            String wardId = ward.getKey();
            if (wardId.equals(id)) {
                wards.add(ward.getKey(), ward.getValue().getAsJsonObject().get("department"));
                return wards.toString();
            }
        }
        System.out.println("Wards: " + wards);
        return wards.toString();
    }
}
