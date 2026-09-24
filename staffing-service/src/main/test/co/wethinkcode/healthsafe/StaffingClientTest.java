package co.wethinkcode.healthsafe;

import com.google.gson.JsonObject;
import io.javalin.Javalin;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class StaffingClientTest {

    private static Javalin server;
    private static String baseUrl;
    private static StaffingClient client;

    @BeforeAll
    static void setUp() {
        baseUrl = "http://localhost:11";
        client = new StaffingClient();
        String data = """
            {
                "level": 6
            }
            """;

        server = Javalin.create().start(11);
        server.get("/alert-level", ctx -> {
            ctx.contentType("application/json");
            ctx.result(data);
        });
    }

    @AfterAll
    static void tearDown() {
        server.stop();
    }

    @Test
    void shouldGetData() {
        JsonObject result = client.getData(baseUrl + "/alert-level");
        assertNotNull(result);
        assertTrue(result.has("level"));
    }

    @Test
    void shouldGetCorrectAlertLevel() {
        JsonObject result = client.getData(baseUrl + "/alert-level");
        assertEquals(6, result.get("level").getAsInt());
    }
}