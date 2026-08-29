package CSV;

import co.wethinkcode.healthsafe.ReadCSV;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReadCSVTest {
    private final String csvFile = "wards-outdated.csv";
    private final ReadCSV readCSV = new ReadCSV();

    @Test
    void testDataNotNull() {
        List<CSVRecord> records = readCSV.read(csvFile);
        assertNotNull(records);
    }

    @Test
    void testWardIdColumn() {
        List<CSVRecord> records = readCSV.read(csvFile);
        assertNotNull(records.get(0).get("ward_id"));
        assertEquals("w-02", records.get(1).get("ward_id"));
    }

    @Test
    void testWingColumn() {
        List<CSVRecord> records = readCSV.read(csvFile);
        assertNotNull(records.get(0).get("Wing"));
        assertTrue(records.get(0).get("Wing").contains("East Wing"));
    }

    @Test
    void testDepartmentColumn() {
        List<CSVRecord> records = readCSV.read(csvFile);
        assertNotNull(records.get(0).get("department"));
        assertEquals("Cardiology", records.get(0).get("department"));
    }

    @Test
    void testBedsAvailableColumn() {
        List<CSVRecord> records = readCSV.read(csvFile);
        assertNotNull(records.get(0).get("beds_available"));
        assertEquals(3, Integer.parseInt(records.get(0).get("beds_available")));
    }
}
