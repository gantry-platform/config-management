package ai.gantry.configmanagement.service;

import ai.gantry.configmanagement.api.RecordAlreadyExistException;
import ai.gantry.configmanagement.api.RecordNotFoundException;
import ai.gantry.configmanagement.api.ZoneAlreadyExistException;
import ai.gantry.configmanagement.api.ZoneNotFoundException;
import ai.gantry.configmanagement.model.Record;
import ai.gantry.configmanagement.model.Zone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.route53.Route53Client;
import software.amazon.awssdk.services.route53.model.*;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Service
public class Route53Impl implements DnsWrapper {

    private static final Logger logger = LoggerFactory.getLogger(Route53Impl.class);

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
        if(isExistZone(zoneName)) {
            logger.warn(String.format("Zone is already exists: %s", zoneName));
            throw new ZoneAlreadyExistException("Already exists");
        }
        String ref = Long.toString(System.currentTimeMillis());
        logger.info(String.format("Creating zone name: %s caller ref: %s", zoneName, ref));
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

    private boolean isExistZone(String zoneName) {
        Zone zone = getZone(zoneName);
        return zone != null;
    }

    @Override
    public void deleteZone(String zoneName) throws Exception {
        Zone zone = getZone(zoneName);
        if(zone != null) {
            logger.info(String.format("Deleting non required records in zone: %s", zoneName));
            deleteNonRequiredRecords(zoneName);
            DeleteHostedZoneRequest req = DeleteHostedZoneRequest.builder().id(zone.getZoneId()).build();
            client.deleteHostedZone(req);
        } else {
            logger.info(String.format("Zone to be deleted not found: %s", zoneName));
        }
    }

    @Override
    public Zone getZone(String zoneName) {
        Zone zone = null;
        ListHostedZonesResponse res = client.listHostedZones();
        for(HostedZone z : res.hostedZones()) {
            if (!z.name().endsWith(domainPostFix)) {
                continue;
            }

            if (z.name().equals(zoneName)) {
                zone = makeZone(z);
                break;
            }
        }
        if(zone == null) {
            logger.info(String.format("Zone is not found: %s", zoneName));
        }
        return zone;
    }

    @Override
    public List<Record> getRecords(String zoneName) throws Exception {
        Zone zone = getZone(zoneName);
        if(zone == null) {
            logger.warn(String.format("No such zone: %s", zoneName));
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
    public Record createRecord(String zoneName, Record record) throws Exception {
        Zone zone = getZone(zoneName);
        if(zone == null) {
            logger.warn(String.format("No such zone: %s", zoneName));
            throw new ZoneNotFoundException();
        }

        if(isExistRecord(zoneName, record.getName(), record.getType())) {
            logger.warn(String.format("Record is already exists. %s type: %s in zone: %s",
                    record.getType(), record.getName(), zoneName));
            throw new RecordAlreadyExistException();
        }

        logger.info(String.format("Creating a record in a zone named: %s", zoneName));
        logger.info(String.format("Record to be created: %s", record.toString()));

        String alias = record.getAlias();
        ResourceRecordSet.Builder builder = ResourceRecordSet.builder();
        if(alias != null) {
            logger.info(String.format("Alias target: %s", alias));
            AliasTarget target = AliasTarget.builder().dnsName(alias).build();
            builder = builder.aliasTarget(target);
        } else {
            List<ResourceRecord> records = new ArrayList<>();
            for(String v : record.getValues()) {
                ResourceRecord r = ResourceRecord.builder().value(v).build();
                records.add(r);
            }
            Long ttl = new Long(300);
            if(record.getTtl() != null && record.getTtl() > 0) {
                ttl = record.getTtl();
            } else {
                logger.info(String.format("Use default ttl value: %d", ttl));
            }
            builder = builder.resourceRecords(records).type(record.getType().toUpperCase()).ttl(ttl);
        }

        ResourceRecordSet recordSet = builder.name(record.getName()).build();
        Change change = Change.builder().action(ChangeAction.CREATE).resourceRecordSet(recordSet).build();
        ChangeBatch batch = ChangeBatch.builder().changes(change).build();
        ChangeResourceRecordSetsRequest req = ChangeResourceRecordSetsRequest.builder()
                .hostedZoneId(zone.getZoneId()).changeBatch(batch).build();
        client.changeResourceRecordSets(req);

        return getRecord(zone.getName(), record.getName(), record.getType());
    }

    private void deleteNonRequiredRecords(String zoneName) throws Exception {
        Zone zone = getZone(zoneName);
        if(zone == null) {
            logger.warn(String.format("No such zone: %s", zoneName));
            throw new ZoneNotFoundException();
        }

        List<Record> records = getRecords(zoneName);
        Iterator<Record> i = records.iterator();
        while(i.hasNext()) {
            Record r = i.next();
            String type = r.getType();
            if (type.equals("SOA") || type.equals("NS")) {
                i.remove();
            }
        }
        logger.info(String.format("Non required records to be deleted: %s", records.toString()));

        List<Change> changes = makeRecordChanges(ChangeAction.DELETE, records);
        ChangeBatch batch = ChangeBatch.builder().changes(changes).build();
        ChangeResourceRecordSetsRequest req = ChangeResourceRecordSetsRequest.builder()
                .hostedZoneId(zone.getZoneId()).changeBatch(batch).build();
        client.changeResourceRecordSets(req);
    }

    private List<Change> makeRecordChanges(ChangeAction action, List<Record> records) {
        List<Change> changes = new ArrayList<>();
        for(Record record : records) {
            String alias = record.getAlias();
            ResourceRecordSet.Builder builder = ResourceRecordSet.builder();
            if(alias != null) {
                logger.info(String.format("Alias target: %s", alias));
                AliasTarget target = AliasTarget.builder().dnsName(alias).build();
                builder = builder.aliasTarget(target);
            } else {
                List<ResourceRecord> resourceRecords = new ArrayList<>();
                for(String v : record.getValues()) {
                    ResourceRecord r = ResourceRecord.builder().value(v).build();
                    resourceRecords.add(r);
                }
                builder = builder.resourceRecords(resourceRecords).type(record.getType().toUpperCase()).ttl(record.getTtl());
            }

            ResourceRecordSet recordSet = builder.name(record.getName()).build();
            Change change = Change.builder().action(action).resourceRecordSet(recordSet).build();
            changes.add(change);
        }
        return changes;
    }

    @Override
    public void deleteRecord(String zoneName, String recordName, String type) throws Exception {
        Zone zone = getZone(zoneName);
        if(zone == null) {
            logger.warn(String.format("No such zone: %s", zoneName));
            throw new ZoneNotFoundException();
        }

        type = type.toUpperCase();
        logger.info(String.format("Deleting a record in a zone named: %s", zoneName));
        logger.info(String.format("Record to be deleted: %s type: %s", recordName, type));
        Record record = getRecord(zoneName, recordName, type);
        if(record == null) {
            throw new RecordNotFoundException();
        }

        /*
        String alias = record.getAlias();
        ResourceRecordSet.Builder builder = ResourceRecordSet.builder();
        if(alias != null) {
            logger.info(String.format("Alias target: %s", alias));
            AliasTarget target = AliasTarget.builder().dnsName(alias).build();
            builder = builder.aliasTarget(target);
        } else {
            List<ResourceRecord> records = new ArrayList<>();
            for(String v : record.getValues()) {
                ResourceRecord r = ResourceRecord.builder().value(v).build();
                records.add(r);
            }
            builder = builder.resourceRecords(records).type(record.getType().toUpperCase()).ttl(record.getTtl());
        }

        ResourceRecordSet recordSet = builder.name(record.getName()).build();
        Change change = Change.builder().action(ChangeAction.DELETE).resourceRecordSet(recordSet).build();
         */
        List<Change> changes = makeRecordChanges(ChangeAction.DELETE, Arrays.asList(record));
        ChangeBatch batch = ChangeBatch.builder().changes(changes).build();
        ChangeResourceRecordSetsRequest req = ChangeResourceRecordSetsRequest.builder()
                .hostedZoneId(zone.getZoneId()).changeBatch(batch).build();
        client.changeResourceRecordSets(req);
    }

    private boolean isExistRecord(String zoneName, String recordName, String type) throws Exception {
        Record record = getRecord(zoneName, recordName, type);
        return record != null;
    }

    @Override
    public Record getRecord(String zoneName, String recordName, String type) throws Exception {
        Zone zone = getZone(zoneName);
        if(zone == null) {
            logger.warn(String.format("No such zone: %s", zoneName));
            throw new ZoneNotFoundException();
        }

        type = type.toUpperCase();
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
