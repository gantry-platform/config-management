package ai.gantry.configmanagement.service;

import ai.gantry.configmanagement.model.Record;
import ai.gantry.configmanagement.model.Zone;

import java.util.List;

public interface DnsWrapper {
    List<Zone> getZones();
    Zone createZone(String zoneName) throws Exception;

    void deleteZone(String zoneName);
    Zone getZone(String zoneName);

    List<Record> getRecords(String zoneName) throws Exception;
    Record createRecord(String zoneName, Record record);

    void deleteRecord(String zoneName, String recordName);
    Record getRecord(String zoneName, String recordName, String type) throws Exception;
}
