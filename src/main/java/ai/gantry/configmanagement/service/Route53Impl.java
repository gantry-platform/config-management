package ai.gantry.configmanagement.service;

import ai.gantry.configmanagement.model.Record;
import ai.gantry.configmanagement.model.Zone;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.route53.Route53Client;
import software.amazon.awssdk.services.route53.model.HostedZone;
import software.amazon.awssdk.services.route53.model.ListHostedZonesResponse;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class Route53Impl implements DnsWrapper {

    @Value("${aws.route53.accessKeyId}")
    private String accessKeyId;

    @Value("${aws.route53.secretAccessKey}")
    private String secretAccessKey;
    private Route53Client client;

    @PostConstruct
    public void init() {
//        System.out.println("id:" + accessKeyId + " key:" + secretAccessKey);
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
            Zone zone = new Zone();
            zone.setName(z.name());
            zone.setZoneId(z.id());
            zones.add(zone);
        }
        return zones;
    }

    @Override
    public Zone createZone(String zoneName) {
        return null;
    }

    @Override
    public void deleteZone(String zoneName) {

    }

    @Override
    public Zone getZone(String zoneName) {
        return null;
    }

    @Override
    public List<Record> getRecords(String zoneName) {
        return null;
    }

    @Override
    public Record createRecord(String zoneName, Record record) {
        return null;
    }

    @Override
    public void deleteRecord(String zoneName, String recordName) {

    }

    @Override
    public Record getRecord(String zoneName, String recordName) {
        return null;
    }
}
