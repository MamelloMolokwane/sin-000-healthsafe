package co.wethinkcode.healthsafe;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.javalin.Javalin;
import org.junit.jupiter.api.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AlertServiceTest {

    private static Javalin server;
    private static String baseUrl;
    private static WardClient client;

    @BeforeAll
    static void setUp() {
        baseUrl = "http://localhost:10";
        client = new WardClient();
        String data = """
            {
                "ward-1": {
                    "wing": "A",
                    "department": "Emergency"
                },
                "ward-2": {
                    "wing": "A",
                    "department": "Emergency"
                },
                "ward-3": {
                    "wing": "B",
                    "department": "Cardiology"
                },
                "ward-4": {
                    "wing": "B",
                    "department": "Cardiology"
                },
                "ward-5": {
                    "wing": "C",
                    "department": "Pediatrics"
                }
            }
            """;
        server = Javalin.create().start(10);
        server.get("/wards", ctx -> {
           ctx.contentType("application/json");
           ctx.result(data);
        });
    }

    @AfterAll
    static void tearDown() {
        server.stop();
    }

    @Test
    void shouldGetWardData() {
        JsonObject result = client.getWardData(baseUrl + "/wards");

        assertNotNull(result);
        assertTrue(result.has("ward-1"));
        assertTrue(result.has("ward-2"));
    }

    @Test
    void shouldGetCorrectWing() {
        JsonObject result = client.getWardData(baseUrl + "/wards");
        assertEquals(
                "A",
                result.getAsJsonObject("ward-1")
                        .get("wing")
                        .getAsString()
        );
    }

    @Test
    void shouldGetCorrectDepartment() {
        JsonObject result = client.getWardData(baseUrl + "/wards");
        assertEquals(
                "Emergency",
                result.getAsJsonObject("ward-1")
                        .get("department")
                        .getAsString()
        );
    }

    // Test countWingAndDepartments
    @Test
    void shouldCountWingAndDepartment() {
        JsonObject data = new Gson().fromJson("""
        {
            "ward-1": {
                "wing": "A",
                "department": "Emergency"
            }
        }
        """, JsonObject.class);

        Map<String, Integer> result =
                client.countWingAndDepartments(data);
        assertEquals(
                1,
                result.get("A Emergency")
        );
    }

    @Test
    void shouldCountRepeatedWingAndDepartment() {
        JsonObject data = new Gson().fromJson("""
        {
            "ward-1": {
                "wing": "A",
                "department": "Emergency"
            },
            "ward-2": {
                "wing": "A",
                "department": "Emergency"
            },
            "ward-3": {
                "wing": "A",
                "department": "Emergency"
            }
        }
        """, JsonObject.class);

        Map<String, Integer> result =
                client.countWingAndDepartments(data);
        assertEquals(
                3,
                result.get("A Emergency")
        );
    }

    @Test
    void shouldCountDifferentWingAndDepartmentsSeparately() {
        JsonObject data = new Gson().fromJson("""
        {
            "ward-1": {
                "wing": "A",
                "department": "Emergency"
            },
            "ward-2": {
                "wing": "A",
                "department": "Cardiology"
            },
            "ward-3": {
                "wing": "B",
                "department": "Emergency"
            }
        }
        """, JsonObject.class);

        Map<String, Integer> result =
                client.countWingAndDepartments(data);
        assertEquals(1, result.get("A Emergency"));
        assertEquals(1, result.get("A Cardiology"));
        assertEquals(1, result.get("B Emergency"));
    }

    // Test keepBelow8
    @Test
    void shouldKeepOccurrenceBelow8() {
        Integer result = client.keepBelow8(5);
        assertEquals(5, result);
    }

    @Test
    void shouldCapOccurrenceAt8() {
        Integer result = client.keepBelow8(10);
        assertEquals(8, result);
    }
}
