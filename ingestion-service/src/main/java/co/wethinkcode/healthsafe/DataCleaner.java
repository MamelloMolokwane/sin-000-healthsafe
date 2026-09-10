package co.wethinkcode.healthsafe;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataCleaner {

    public boolean isNull(String missing) {
        List<String> nullPlaceholders = List.of("N/A", "TBD", "blank", "-", "NaN", "unknown");
        return nullPlaceholders.contains(missing);
    }

    public String cleanWardId(String wardId) {
        if (wardId == null || isNull(wardId)) {
            return "false";
        }
        return wardId.strip().toUpperCase();
    }

    public String cleanWing(String wing) {
        if (wing == null || isNull(wing)) {
            return "false";
        }
        return StringUtils.capitalize(wing.strip().toLowerCase());
    }

    public String cleanDepartment(String department) {
        if (department == null || isNull(department)) {
            return "false";
        }
        return StringUtils.capitalize(department.strip().toLowerCase());
    }

    public String cleanBedsAvailable(String bedsAvailable) {
        return String.valueOf(StringUtils.isNumeric(bedsAvailable));
    }

    public Map<String, Map<String, String>> clean(List<CSVRecord> csvRecords) {
        Map<String, Map<String, String>> records = new HashMap<>();
        String notes = "";
        for (CSVRecord record: csvRecords) {
            // Remove duplicates
            if (records.containsKey(cleanWing(record.get("Wing")))) {
                continue;
            }
            String bedsAvailable = cleanBedsAvailable(record.get("beds_available"));
            if (bedsAvailable.equals("false")) {
                notes = "bedsAvailable was non-numeric (" + record.get("beds_available") + ") — flagged for follow-up";
            }
            Map<String, String> recordData = Map.of(
                    "wing", cleanWing(record.get("Wing")),
                    "department", cleanDepartment(record.get("department")),
                    "bedsAvailable", bedsAvailable,
                    "notes", notes
            );
            records.putIfAbsent(cleanWardId(record.get("ward_id")), recordData);
        }
        return records;
    }
}
