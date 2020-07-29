package ai.gantry.configmanagement.service;

import ai.gantry.configmanagement.api.ZoneAlreadyExistException;
import ai.gantry.configmanagement.model.Record;
import ai.gantry.configmanagement.model.Zone;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.route53.Route53Client;
import software.amazon.awssdk.services.route53.model.*;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class Route53Impl implements DnsWrapper {

    @Value("${aws.route53.accessKeyId}")
    private String accessKeyId;

    @Value("${aws.route53.secretAccessKey}")
    private String secretAccessKey;

    @Value("${domain.postfix}")
    private String domainPostFix;
    private Route53Client client;

    @PostConstruct
    public void init() {
        AwsBasicCredentials cred = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
        client = Route53Client.builder()
                .region(Region.AWS_GLOBAL)
                .credentialsProvider(StaticCredentialsProvider.create(cred))
                .build();
    }

    @Override
    public List<Zone> getZones() {
        List<Zone> zones = new ArrayList<>();
        ListHostedZonesResponse res = client.listHostedZones();
        for(HostedZone z : res.hostedZones()) {
            if(!z.name().endsWith(domainPostFix)) {
                continue;
            }
            zones.add(makeZone(z));
        }
        return zones;
    }

    @Override
    public Zone createZone(String zoneName) throws Exception {
        if(exists(zoneName)) {
            throw new ZoneAlreadyExistException("Already exists");
        }
        String ref = Long.toString(System.currentTimeMillis());
        CreateHostedZoneRequest req = CreateHostedZoneRequest.builder().callerReference(ref).name(zoneName).build();
        CreateHostedZoneResponse res = client.createHostedZone(req);
        HostedZone created = res.hostedZone();
        return makeZone(created);
    }

    private Zone makeZone(HostedZone z) {
        Zone zone = new Zone();
        zone.setName(z.name());
        zone.setZoneId(z.id());
        return zone;
    }

    private boolean exists(String zoneName) {
        Zone zone = getZone(zoneName);
        return zone != null;
    }

    @Override
    public void deleteZone(String zoneName) {
        Zone zone = getZone(zoneName);
        if(zone != null) {
            DeleteHostedZoneRequest req = DeleteHostedZoneRequest.builder().id(zone.getZoneId()).build();
            client.deleteHostedZone(req);
        }
    }

    @Override
    public Zone getZone(String zoneName) {
        Zone zone = null;
        ListHostedZonesResponse res = client.listHostedZones();
        for(HostedZone z : res.hostedZones()) {
            if(!z.name().endsWith(domainPostFix)) {
                continue;
            }

            if(z.name().equals(zoneName)) {
                zone = makeZone(z);
                break;
            }
        }
        return zone;
    }

    @Override
    public List<Record> getRecords(String zoneName) throws Exception {
        Zone zone = getZone(zoneName);
        if(zone == null) {
            throw new Exception("No zone");
        }

        List<Record> records = new ArrayList<>();
        ListResourceRecordSetsRequest req = ListResourceRecordSetsRequest.builder()
                .hostedZoneId(zone.getZoneId()).build();
        ListResourceRecordSetsResponse res = client.listResourceRecordSets(req);
        for(ResourceRecordSet r : res.resourceRecordSets()) {
            Record record = new Record();
            record.setName(r.name());
            record.setType(r.typeAsString());

            List<String> values = new ArrayList<>();
            for(ResourceRecord v : r.resourceRecords()) {
                values.add(v.value());
            }
            record.setValues(values);
            AliasTarget alias = r.aliasTarget();
            if(alias != null) {
                record.setAlias(alias.dnsName());
            }
            record.setTtl(r.ttl());
            records.add(record);
        }
        return records;
    }

    @Override
    public Record createRecord(String zoneName, Record record) {
        return null;
    }

    @Override
    public void deleteRecord(String zoneName, String recordName) {

    }

    @Override
    public Record getRecord(String zoneName, String recordName, String type) throws Exception {
        Zone zone = getZone(zoneName);
        if(zone == null) {
            throw new Exception("No zone");
        }

        Record record = null;
        ListResourceRecordSetsRequest req = ListResourceRecordSetsRequest.builder()
                .hostedZoneId(zone.getZoneId()).build();
        ListResourceRecordSetsResponse res = client.listResourceRecordSets(req);
        for(ResourceRecordSet r : res.resourceRecordSets()) {
            if(!r.name().equals(recordName)) {
                continue;
            }

            if(!r.typeAsString().equals(type)) {
                continue;
            }

            record = new Record();
            record.setName(r.name());
            record.setType(r.typeAsString());

            List<String> values = new ArrayList<>();
            for(ResourceRecord v : r.resourceRecords()) {
                values.add(v.value());
            }
            record.setValues(values);
            AliasTarget alias = r.aliasTarget();
            if(alias != null) {
                record.setAlias(alias.dnsName());
            }
            record.setTtl(r.ttl());
        }
        return record;
    }
}
