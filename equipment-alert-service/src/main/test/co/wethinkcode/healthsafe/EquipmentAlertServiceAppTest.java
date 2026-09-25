package co.wethinkcode.healthsafe;

import io.javalin.Javalin;
import org.junit.jupiter.api.*;

import java.net.ServerSocket;
import java.net.URI;
import java.net.http.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EquipmentAlertServiceAppTest {
    private Javalin app;
    private int port;

    @BeforeEach
    void setUp() throws Exception {
        port = findFreePort();
        app = Javalin.create();
        app.get("/health", ctx -> ctx.result("OK"));
        app.start(port);
    }

    @AfterEach
    void tearDown() {
        app.stop();
    }

    @Test
    void healthEndpointReturnsOk() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/health"))
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("OK", response.body());
    }

    private int findFreePort() throws Exception {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }
}