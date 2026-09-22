
package co.wethinkcode.healthsafe;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class WardServiceTest {

    private HttpServer server;
    private String baseUrl;
    private IngestionClient ingestionClient;

    @BeforeEach
    void setUp() throws IOException {
        // Create a local HTTP server on a random available port
        server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        ingestionClient = new IngestionClient();
        server.start();
        int port = server.getAddress().getPort();
        baseUrl = "http://localhost:" + port;
    }

    @AfterEach
    void tearDown() {
        // Stop the server after each test
        server.stop(0);
    }

    private void createEndpoint(String path, String response) {
        server.createContext(path, exchange -> {
            byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, responseBytes.length);
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(responseBytes);
            }
        });
    }

    @Test
    void shouldGetIngestionData() {
        String json = """
                {
                    "ward-1": {
                        "department": "Emergency",
                        "beds_available": "10"
                    },
                    "ward-2": {
                        "department": "Cardiology",
                        "beds_available": "5"
                    }
                }
                """;
        createEndpoint("/wards", json);
        var result = ingestionClient.getIngestionData(baseUrl + "/wards");

        assertNotNull(result);
        assertTrue(result.has("ward-1"));
        assertTrue(result.has("ward-2"));
        assertEquals("Emergency", result.getAsJsonObject("ward-1").get("department").getAsString());
        assertEquals("Cardiology", result.getAsJsonObject("ward-2").get("department").getAsString());
    }

    @Test
    void shouldGetWardWhenIdExists() {
        String json = """
                {
                    "ward-1": {
                        "department": "Emergency",
                        "beds_available": "10"
                    },
                    "ward-2": {
                        "department": "Cardiology",
                        "beds_available": "5"
                    }
                }
                """;
        createEndpoint("/wards", json);
        String result = ingestionClient.getWard(baseUrl + "/wards", "ward-1");

        assertNotNull(result);
        assertEquals("{\"ward-1\":\"Emergency\"}", result);
    }

    @Test
    void shouldReturnEmptyJsonWhenWardDoesNotExist() {
        String json = """
                {
                    "ward-1": {
                        "department": "Emergency",
                        "beds_available": "10"
                    }
                }
                """;
        createEndpoint("/wards", json);
        String result = ingestionClient.getWard(baseUrl + "/wards", "ward-999");

        assertNotNull(result);
        assertEquals("{}", result);
    }

    @Test
    void shouldReturnCorrectWardWhenMultipleWardsExist() {
        String json = """
                {
                    "ward-1": {
                        "department": "Emergency"
                    },
                    "ward-2": {
                        "department": "Cardiology"
                    },
                    "ward-3": {
                        "department": "Pediatrics"
                    }
                }
                """;
        createEndpoint("/wards", json);
        String result = ingestionClient.getWard(baseUrl + "/wards", "ward-3");
        assertEquals("{\"ward-3\":\"Pediatrics\"}", result);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenServerIsUnavailable() {
        // No endpoint is created and the server is stopped
        server.stop(0);
        String invalidUrl = baseUrl + "/wards";
        assertThrows(RuntimeException.class, () -> ingestionClient.getIngestionData(invalidUrl));
    }
}