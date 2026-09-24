package co.wethinkcode.healthsafe;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StaffingServiceTest {

    private final StaffingClient client = new StaffingClient();

    // Test assignStaff

    @Test
    void shouldAssignOneDoctorForLowEmergencyStatus() {
        int result = client.assignStaff(0);
        assertEquals(1, result);
    }

    @Test
    void shouldAssignOneDoctorForEmergencyStatusTwo() {
        int result = client.assignStaff(2);
        assertEquals(1, result);
    }

    @Test
    void shouldAssignThreeDoctorsForEmergencyStatusThree() {
        int result = client.assignStaff(3);
        assertEquals(3, result);
    }

    @Test
    void shouldAssignThreeDoctorsForEmergencyStatusFive() {
        int result = client.assignStaff(5);
        assertEquals(3, result);
    }

    @Test
    void shouldAssignFourDoctorsForEmergencyStatusSix() {
        int result = client.assignStaff(6);
        assertEquals(4, result);
    }

    @Test
    void shouldAssignFourDoctorsForEmergencyStatusSeven() {
        int result = client.assignStaff(7);
        assertEquals(4, result);
    }

    @Test
    void shouldAssignEightDoctorsForEmergencyStatusEight() {
        int result = client.assignStaff(8);
        assertEquals(5, result);
    }


    // Test computeSchedule

    @Test
    void shouldAddEmergencyStatusToWard() {
        JsonObject wards = new Gson().fromJson("""
            {
                "ward-1": {
                    "wing": "A",
                    "department": "Emergency"
                }
            }
            """, JsonObject.class);
        JsonObject result = client.computeSchedule(wards, 5);

        assertEquals(5, result.getAsJsonObject("ward-1").get("Emergency Status").getAsInt());
    }

    @Test
    void shouldAddDoctorAssignmentToWard() {
        JsonObject wards = new Gson().fromJson("""
            {
                "ward-1": {
                    "wing": "A",
                    "department": "Emergency"
                }
            }
            """, JsonObject.class);
        JsonObject result = client.computeSchedule(wards, 5);
        assertEquals(3, result.getAsJsonObject("ward-1").get("Doctor Assigned").getAsInt());
    }

    @Test
    void shouldComputeScheduleForMultipleWards() {
        JsonObject wards = new Gson().fromJson("""
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
                    "department": "Pediatrics"
                }
            }
            """, JsonObject.class);

        JsonObject result = client.computeSchedule(wards, 6);

        assertEquals(6, result.getAsJsonObject("ward-1").get("Emergency Status").getAsInt());
        assertEquals(4, result.getAsJsonObject("ward-1").get("Doctor Assigned").getAsInt());
        assertEquals(4, result.getAsJsonObject("ward-2").get("Doctor Assigned").getAsInt());
        assertEquals(4, result.getAsJsonObject("ward-3").get("Doctor Assigned").getAsInt());
    }

    @Test
    void shouldNotModifyOriginalWardData() {
        JsonObject wards = new Gson().fromJson("""
            {
                "ward-1": {
                    "wing": "A",
                    "department": "Emergency"
                }
            }
            """, JsonObject.class);

        client.computeSchedule(wards, 6);

        assertFalse(wards.getAsJsonObject("ward-1").has("Emergency Status"));
        assertFalse(wards.getAsJsonObject("ward-1").has("Doctor Assigned"));
    }

    @Test
    void shouldKeepExistingWardInformation() {
        JsonObject wards = new Gson().fromJson("""
            {
                "ward-1": {
                    "wing": "A",
                    "department": "Emergency"
                }
            }
            """, JsonObject.class);

        JsonObject result = client.computeSchedule(wards, 6);

        assertEquals("A", result.getAsJsonObject("ward-1").get("wing").getAsString());
        assertEquals("Emergency", result.getAsJsonObject("ward-1").get("department").getAsString());
    }
}