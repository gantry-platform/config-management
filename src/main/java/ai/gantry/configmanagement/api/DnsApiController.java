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
        /*
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<List<Zone>>(objectMapper.readValue("[ {\n  \"name\" : \"name\",\n  \"zoneId\" : \"zoneId\"\n}, {\n  \"name\" : \"name\",\n  \"zoneId\" : \"zoneId\"\n} ]", List.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<List<Zone>>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<List<Zone>>(HttpStatus.NOT_IMPLEMENTED);
         */
    }

    public ResponseEntity<Zone> zonesPost(@ApiParam(value = "" ,required=true )  @Valid @RequestBody Zone body
) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<Zone>(objectMapper.readValue("{\n  \"name\" : \"name\",\n  \"zoneId\" : \"zoneId\"\n}", Zone.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<Zone>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<Zone>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<Void> zonesZoneDelete(@ApiParam(value = "zone name",required=true) @PathVariable("zone") String zone
) {
        String accept = request.getHeader("Accept");
        return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<Zone> zonesZoneGet(@ApiParam(value = "zone name",required=true) @PathVariable("zone") String zone
) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<Zone>(objectMapper.readValue("{\n  \"name\" : \"name\",\n  \"zoneId\" : \"zoneId\"\n}", Zone.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<Zone>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<Zone>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<List<Record>> zonesZoneRecordsGet(@ApiParam(value = "zone name",required=true) @PathVariable("zone") String zone
) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<List<Record>>(objectMapper.readValue("[ {\n  \"values\" : [ \"values\", \"values\" ],\n  \"name\" : \"name\",\n  \"type\" : \"type\",\n  \"ttl\" : 0\n}, {\n  \"values\" : [ \"values\", \"values\" ],\n  \"name\" : \"name\",\n  \"type\" : \"type\",\n  \"ttl\" : 0\n} ]", List.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<List<Record>>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<List<Record>>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<Record> zonesZoneRecordsPost(@ApiParam(value = "" ,required=true )  @Valid @RequestBody Record body
,@ApiParam(value = "zone name",required=true) @PathVariable("zone") String zone
,@ApiParam(value = "record name",required=true) @PathVariable("record") String record
) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<Record>(objectMapper.readValue("{\n  \"values\" : [ \"values\", \"values\" ],\n  \"name\" : \"name\",\n  \"type\" : \"type\",\n  \"ttl\" : 0\n}", Record.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<Record>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<Record>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<Void> zonesZoneRecordsRecordDelete(@ApiParam(value = "zone name",required=true) @PathVariable("zone") String zone
,@ApiParam(value = "record name",required=true) @PathVariable("record") String record
) {
        String accept = request.getHeader("Accept");
        return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<Record> zonesZoneRecordsRecordGet(@ApiParam(value = "zone name",required=true) @PathVariable("zone") String zone
,@ApiParam(value = "record name",required=true) @PathVariable("record") String record
) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<Record>(objectMapper.readValue("{\n  \"values\" : [ \"values\", \"values\" ],\n  \"name\" : \"name\",\n  \"type\" : \"type\",\n  \"ttl\" : 0\n}", Record.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<Record>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<Record>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<Record> zonesZoneRecordsRecordPut(@ApiParam(value = "" ,required=true )  @Valid @RequestBody Record body
,@ApiParam(value = "zone name",required=true) @PathVariable("zone") String zone
,@ApiParam(value = "record name",required=true) @PathVariable("record") String record
) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<Record>(objectMapper.readValue("{\n  \"values\" : [ \"values\", \"values\" ],\n  \"name\" : \"name\",\n  \"type\" : \"type\",\n  \"ttl\" : 0\n}", Record.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<Record>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<Record>(HttpStatus.NOT_IMPLEMENTED);
    }

}
