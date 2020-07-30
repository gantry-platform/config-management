package ai.gantry.configmanagement.api;

import ai.gantry.configmanagement.model.Error;
import ai.gantry.configmanagement.model.Record;
import ai.gantry.configmanagement.model.Zone;
import ai.gantry.configmanagement.service.DnsWrapper;
import ai.gantry.configmanagement.service.Route53Impl;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-07-27T16:19:55.143+09:00[Asia/Seoul]")
@Controller
public class DnsApiController implements DnsApi {

    private static final Logger log = LoggerFactory.getLogger(DnsApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;
    // TODO: Interface를 유지하면서 dependency injection 처리
    @Autowired
    private DnsWrapper dnsWrapper;

    @org.springframework.beans.factory.annotation.Autowired
    public DnsApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<List<Zone>> zonesGet() {
        return new ResponseEntity<List<Zone>>(dnsWrapper.getZones(), HttpStatus.OK);
    }

    public ResponseEntity<Zone> zonesZonePost(@ApiParam(value = "zone name",required=true) @PathVariable("zone") String zone
    ) throws Exception {
        log.info(String.format("Create zone: %s", zone));
        if(!zone.endsWith(".")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Zone name should be ended with .(dot)");
        }

        try {
            Zone created = dnsWrapper.createZone(zone);
            return new ResponseEntity<Zone>(created, HttpStatus.OK);
        } catch(ZoneAlreadyExistException e) {
            throw new ApiException(HttpStatus.CONFLICT, "Duplicated zone name: " + zone);
        }
    }

    public ResponseEntity<Void> zonesZoneDelete(@ApiParam(value = "zone name",required=true) @PathVariable("zone") String zone
) throws Exception {
        log.info(String.format("Delete zone: %s", zone));
        if(!zone.endsWith(".")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Zone name should be ended with .(dot)");
        }

        dnsWrapper.deleteZone(zone);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    public ResponseEntity<Zone> zonesZoneGet(@ApiParam(value = "zone name",required=true) @PathVariable("zone") String zone
) throws Exception {
        log.info(String.format("Get zone: %s", zone));
        if(!zone.endsWith(".")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Zone name should be ended with .(dot)");
        }
        Zone zoneInfo = dnsWrapper.getZone(zone);
        if(zoneInfo == null) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "There is no zone named: " + zone);
        }
        return new ResponseEntity<Zone>(zoneInfo, HttpStatus.OK);
    }

    public ResponseEntity<List<Record>> zonesZoneRecordsGet(@ApiParam(value = "zone name",required=true) @PathVariable("zone") String zone
) throws Exception {
        log.info(String.format("Get records in zone: %s", zone));
        if(!zone.endsWith(".")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Zone name should be ended with .(dot)");
        }

        try {
            List<Record> records = dnsWrapper.getRecords(zone);
            return new ResponseEntity<List<Record>>(records, HttpStatus.OK);
        } catch(ZoneNotFoundException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "There is no zone named: " + zone);
        }
    }

    public ResponseEntity<Record> zonesZoneRecordsPost(@ApiParam(value = "" ,required=true )  @Valid @RequestBody Record body
            ,@ApiParam(value = "zone name",required=true) @PathVariable("zone") String zone
    ) throws Exception {
        log.info(String.format("Create a record in a zone: %s", zone));
        if(!zone.endsWith(".") || !body.getName().endsWith(".")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Zone and record name should be ended with .(dot)");
        }

        try {
            Record record = dnsWrapper.createRecord(zone, body);
            return new ResponseEntity<Record>(record, HttpStatus.OK);
        } catch(ZoneNotFoundException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "There is no zone named: " + zone);
        } catch(RecordAlreadyExistException e) {
            throw new ApiException(HttpStatus.CONFLICT,
                    String.format("There is a record %s:%s in zone: %s ", body.getType(), body.getName(), zone));
        } catch(Exception e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ResponseEntity<Record> zonesZoneRecordsPut(@ApiParam(value = "" ,required=true )  @Valid @RequestBody Record body
            ,@ApiParam(value = "zone name",required=true) @PathVariable("zone") String zone
    ) throws Exception {
        log.info(String.format("Update a record in a zone: %s", zone));
        if(!zone.endsWith(".") || !body.getName().endsWith(".")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Zone and record name should be ended with .(dot)");
        }

        try {
            Record record = dnsWrapper.updateRecord(zone, body);
            return new ResponseEntity<Record>(record, HttpStatus.OK);
        } catch(ZoneNotFoundException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "There is no zone named: " + zone);
        } catch(RecordNotFoundException e) {
            throw new NotFoundException(HttpStatus.NOT_FOUND,
                    String.format("There is no record %s:%s in zone: %s ", body.getType(), body.getName(), zone));
        } catch(Exception e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public ResponseEntity<Void> zonesZoneRecordsRecordDelete(@ApiParam(value = "zone name",required=true) @PathVariable("zone") String zone
            ,@ApiParam(value = "record name",required=true) @PathVariable("record") String record
            ,@NotNull @ApiParam(value = "record type", required = true) @Valid @RequestParam(value = "type", required = true) String type
    ) throws Exception {
        log.info(String.format("Delete a record in a zone: %s", zone));
        if(!zone.endsWith(".") || !record.endsWith(".")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Zone and record name should be ended with .(dot)");
        }

        try {
            dnsWrapper.deleteRecord(zone, record, type);
            return new ResponseEntity<Void>(HttpStatus.OK);
        } catch(ZoneNotFoundException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "There is no zone named: " + zone);
        } catch(RecordNotFoundException e) {
            throw new NotFoundException(HttpStatus.NOT_FOUND,
                    String.format("There is no %s type record: %s in zone: %s", type, record, zone));
        }
    }

    public ResponseEntity<Record> zonesZoneRecordsRecordGet(@ApiParam(value = "zone name",required=true) @PathVariable("zone") String zone
,@ApiParam(value = "record name",required=true) @PathVariable("record") String record
,@NotNull @ApiParam(value = "record type", required = true) @Valid @RequestParam(value = "type", required = true) String type
) throws Exception {
        log.info(String.format("Get a record: %s in zone: %s", record, zone));
        if(!zone.endsWith(".") || !record.endsWith(".")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Zone and record name should be ended with .(dot)");
        }

        try {
            Record recordInfo = dnsWrapper.getRecord(zone, record, type);
            if (recordInfo == null) {
                throw new NotFoundException(HttpStatus.NOT_FOUND,
                        "There is no record named: " + record + " type: " + type);
            }
            return new ResponseEntity<Record>(recordInfo, HttpStatus.OK);
        } catch(ZoneNotFoundException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "There is no zone named: " + zone);
        }
    }
}
