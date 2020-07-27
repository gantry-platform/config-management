package ai.gantry.configmanagement.service;

import ai.gantry.configmanagement.model.Record;
import ai.gantry.configmanagement.model.Zone;

import java.util.List;

public interface DnsWrapper {
    List<Zone> getZones();
    Zone createZone(String zoneName);

    void deleteZone(String zoneName);
    Zone getZone(String zoneName);

    List<Record> getRecords(String zoneName);
    Record createRecord(String zoneName, Record record);

    void deleteRecord(String zoneName, String recordName);
    Record getRecord(String zoneName, String recordName);
}
