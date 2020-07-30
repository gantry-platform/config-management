package ai.gantry.configmanagement.service;

import ai.gantry.configmanagement.model.Record;
import ai.gantry.configmanagement.model.Zone;

import java.util.List;

public interface DnsWrapper {
    List<Zone> getZones();
    Zone createZone(String zoneName) throws Exception;

    void deleteZone(String zoneName) throws Exception;
    Zone getZone(String zoneName);

    List<Record> getRecords(String zoneName) throws Exception;
    Record createRecord(String zoneName, Record record) throws Exception;
    Record updateRecord(String zoneName, Record record) throws Exception;

    void deleteRecord(String zoneName, String recordName, String type) throws Exception;
    Record getRecord(String zoneName, String recordName, String type) throws Exception;
}
