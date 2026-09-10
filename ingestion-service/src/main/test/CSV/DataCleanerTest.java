package CSV;

import co.wethinkcode.healthsafe.DataCleaner;
import co.wethinkcode.healthsafe.ReadCSV;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.*;

import javax.xml.crypto.Data;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DataCleanerTest {

    private List<CSVRecord> csvRecords;
    DataCleaner dataCleaner;
    private Map<String, Map<String, String>> records;

    @BeforeEach
    void setUp() {
        String csvFile = "wards-outdated.csv";
        csvRecords = new ReadCSV().read(csvFile);
        dataCleaner = new DataCleaner();
        records = dataCleaner.clean(csvRecords);
    }

    @Test
    void testNotNull() {
        // Check if the records map is null
        assertNotNull(records);
        // Check if the map inside records map is null
        assertNotNull(records.values());
    }

    @Test
    void testCleanWardId() {
        assertFalse(records.containsKey("w-02"));
        assertTrue(records.containsKey("W-02"));
        assertFalse(records.containsKey(csvRecords.get(1).get("ward_id")));

    }

    @Test
    void testCleanWing() {
        assertNotEquals(" East Wing ", records.get("W-01").get("wing"));
        assertEquals("East wing", records.get("W-01").get("wing"));
        assertNotEquals(csvRecords.get(3).get("Wing"), records.get("W-04").get("wing"));
    }

    @Test
    void testCleanDepartment() {
        String cleanedDepartment = dataCleaner.cleanDepartment(csvRecords.get(1).get("department"));
        assertNotEquals(csvRecords.get(1).get("department"),cleanedDepartment);
    }

    @Test
    void testCleanBedsAvailable() {
        String cleanedBedsAvailable = dataCleaner.cleanBedsAvailable(csvRecords.get(1).get("beds_available"));
        assertNotEquals(csvRecords.get(1).get("beds_available"), cleanedBedsAvailable);
        assertEquals("false", cleanedBedsAvailable);
        assertEquals("true", dataCleaner.cleanBedsAvailable(csvRecords.get(0).get("beds_available")));
    }
}
