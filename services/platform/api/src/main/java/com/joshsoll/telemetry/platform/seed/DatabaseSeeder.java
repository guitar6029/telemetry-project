package com.joshsoll.telemetry.platform.seed;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.joshsoll.telemetry.platform.device.DeviceStatus;
import com.joshsoll.telemetry.platform.device.entity.Device;
import com.joshsoll.telemetry.platform.device.repository.DeviceRepository;
import com.joshsoll.telemetry.platform.deviceTemplate.entity.DeviceTemplate;
import com.joshsoll.telemetry.platform.deviceTemplate.repository.DeviceTemplateRepository;
import com.joshsoll.telemetry.platform.hierarchy.entity.HierarchyNode;
import com.joshsoll.telemetry.platform.hierarchy.repository.HierarchyNodeRepository;
import com.joshsoll.telemetry.platform.metricDefinition.MetricDataType;
import com.joshsoll.telemetry.platform.metricDefinition.entity.MetricDefinition;
import com.joshsoll.telemetry.platform.metricDefinition.repository.MetricDefinitionRepository;
import com.joshsoll.telemetry.platform.metricValue.entity.MetricValue;
import com.joshsoll.telemetry.platform.metricValue.repository.MetricValueRepository;
import com.joshsoll.telemetry.platform.organization.entity.Organization;
import com.joshsoll.telemetry.platform.organization.repository.OrganizationRepository;
import com.joshsoll.telemetry.platform.telemetryEvent.entity.TelemetryEvent;
import com.joshsoll.telemetry.platform.telemetryEvent.repository.TelemetryEventRepository;

@Component
@Profile("dev")
public class DatabaseSeeder implements CommandLineRunner {
        private final OrganizationRepository organizationRepository;
        private final HierarchyNodeRepository hierarchyNodeRepository;
        private final DeviceTemplateRepository deviceTemplateRepository;
        private final DeviceRepository deviceRepository;
        private final MetricDefinitionRepository metricDefinitionRepository;
        private final TelemetryEventRepository telemetryEventRepository;
        private final MetricValueRepository metricValueRepository;
        private static final Logger log = LoggerFactory.getLogger(DatabaseSeeder.class);

        public DatabaseSeeder(
                        OrganizationRepository organizationRepository,
                        HierarchyNodeRepository hierarchyNodeRepository,
                        DeviceTemplateRepository deviceTemplateRepository,
                        DeviceRepository deviceRepository,
                        MetricDefinitionRepository metricDefinitionRepository,
                        TelemetryEventRepository telemetryEventRepository,
                        MetricValueRepository metricValueRepository) {

                this.organizationRepository = organizationRepository;
                this.hierarchyNodeRepository = hierarchyNodeRepository;
                this.deviceTemplateRepository = deviceTemplateRepository;
                this.deviceRepository = deviceRepository;
                this.metricDefinitionRepository = metricDefinitionRepository;
                this.telemetryEventRepository = telemetryEventRepository;
                this.metricValueRepository = metricValueRepository;
        }

        @Override
        public void run(String... args) {

                if (organizationRepository.count() > 0) {
                        log.info("Database already seeded.");
                        return;
                }

                log.info("Seeding development database...");

                List<Organization> organizations = seedOrganizations();

                List<HierarchyNode> nodes = seedHierarchy(organizations);

                List<DeviceTemplate> templates = seedDeviceTemplates(organizations);

                List<Device> devices = seedDevices(
                                organizations,
                                templates,
                                nodes);

                List<MetricDefinition> definitions = seedMetricDefinitions(templates);

                List<TelemetryEvent> telemetryEvents = seedTelemetry(devices);

                seedMetricValues(telemetryEvents, definitions);

                log.info("Development database seeded successfully.");
        }

        private List<Organization> seedOrganizations() {
                Instant now = Instant.now();
                Organization acme = new Organization(
                                "Acme Industries",
                                "acme-industries",
                                now,
                                now);

                Organization globex = new Organization(
                                "Globex Corporation",
                                "globex-corporation",
                                now,
                                now);

                organizationRepository.saveAll(List.of(acme, globex));
                return List.of(acme, globex);
        }

        private List<HierarchyNode> seedHierarchy(List<Organization> organizations) {

                Instant now = Instant.now();

                Organization acme = organizations.get(0);

                HierarchyNode miami = new HierarchyNode(
                                "Miami Plant",
                                acme,
                                null,
                                now,
                                now);

                HierarchyNode dallas = new HierarchyNode(
                                "Dallas Plant",
                                acme,
                                null,
                                now,
                                now);

                hierarchyNodeRepository.saveAll(List.of(miami, dallas));
                return List.of(miami, dallas);
        }

        private List<DeviceTemplate> seedDeviceTemplates(List<Organization> organizations) {
                Instant now = Instant.now();

                Organization acme = organizations.stream()
                                .filter(o -> o.getSlug().equals("acme-industries"))
                                .findFirst()
                                .orElseThrow();

                Organization globex = organizations.stream()
                                .filter(o -> o.getSlug().equals("globex-corporation"))
                                .findFirst()
                                .orElseThrow();
                DeviceTemplate temperatureSensor = new DeviceTemplate(
                                "Temperature Sensor",
                                "Industrial temperature monitoring sensor.",
                                acme,
                                false,
                                now,
                                now);

                DeviceTemplate pressureSensor = new DeviceTemplate(
                                "Pressure Sensor",
                                "Hydraulic pressure monitoring sensor.",
                                acme,
                                false,
                                now,
                                now);

                DeviceTemplate plcController = new DeviceTemplate(
                                "PLC Controller",
                                "Programmable logic controller.",
                                acme,
                                false,
                                now,
                                now);

                DeviceTemplate gpsTracker = new DeviceTemplate(
                                "GPS Tracker",
                                "Fleet GPS tracking device.",
                                globex,
                                false,
                                now,
                                now);

                DeviceTemplate environmentalSensor = new DeviceTemplate(
                                "Environmental Sensor",
                                "Temperature and humidity monitoring device.",
                                globex,
                                false,
                                now,
                                now);

                DeviceTemplate fuelMonitor = new DeviceTemplate(
                                "Fuel Monitor",
                                "Vehicle fuel level monitoring device.",
                                globex,
                                false,
                                now,
                                now);

                deviceTemplateRepository.saveAll(List.of(
                                temperatureSensor,
                                pressureSensor,
                                plcController,
                                gpsTracker,
                                environmentalSensor,
                                fuelMonitor));

                return List.of(
                                temperatureSensor,
                                pressureSensor,
                                plcController,
                                gpsTracker,
                                environmentalSensor,
                                fuelMonitor);
        }

        private List<Device> seedDevices(
                        List<Organization> organizations,
                        List<DeviceTemplate> templates,
                        List<HierarchyNode> nodes) {

                Organization acme = organizations.stream()
                                .filter(o -> o.getSlug().equals("acme-industries"))
                                .findFirst()
                                .orElseThrow();

                Organization globex = organizations.stream()
                                .filter(o -> o.getSlug().equals("globex-corporation"))
                                .findFirst()
                                .orElseThrow();
                DeviceTemplate temperatureSensor = templates.stream()
                                .filter(t -> t.getName().equals("Temperature Sensor"))
                                .findFirst()
                                .orElseThrow();

                DeviceTemplate pressureSensor = templates.stream()
                                .filter(t -> t.getName().equals("Pressure Sensor"))
                                .findFirst()
                                .orElseThrow();

                DeviceTemplate gpsTracker = templates.stream()
                                .filter(t -> t.getName().equals("GPS Tracker"))
                                .findFirst()
                                .orElseThrow();

                HierarchyNode miami = nodes.stream()
                                .filter(n -> n.getName().equals("Miami Plant"))
                                .findFirst()
                                .orElseThrow();

                HierarchyNode dallas = nodes.stream()
                                .filter(n -> n.getName().equals("Dallas Plant"))
                                .findFirst()
                                .orElseThrow();

                Instant now = Instant.now();

                Device temp001 = new Device(
                                "TEMP-001",
                                "Siemens",
                                "TS-100",
                                "SN-TEMP-0001",
                                "1.0.0",
                                DeviceStatus.ONLINE,
                                acme,
                                miami,
                                temperatureSensor,
                                now,
                                now);

                Device temp002 = new Device(
                                "TEMP-002",
                                "Siemens",
                                "TS-100",
                                "SN-TEMP-0002",
                                "1.0.0",
                                DeviceStatus.ONLINE,
                                acme,
                                dallas,
                                temperatureSensor,
                                now,
                                now);

                Device pressure001 = new Device(
                                "PRESS-001",
                                "Honeywell",
                                "PS-200",
                                "SN-PRESS-0001",
                                "2.1.3",
                                DeviceStatus.OFFLINE,
                                acme,
                                miami,
                                pressureSensor,
                                now,
                                now);

                Device gps001 = new Device(
                                "GPS-001",
                                "Garmin",
                                "GT-500",
                                "SN-GPS-0001",
                                "3.0.1",
                                DeviceStatus.ONLINE,
                                globex,
                                dallas,
                                gpsTracker,
                                now,
                                now);

                deviceRepository.saveAll(List.of(
                                temp001,
                                temp002,
                                pressure001,
                                gps001));

                return List.of(
                                temp001,
                                temp002,
                                pressure001,
                                gps001);

        }

        private List<MetricDefinition> seedMetricDefinitions(List<DeviceTemplate> templates) {

                Instant now = Instant.now();

                DeviceTemplate temperatureSensor = templates.stream()
                                .filter(t -> t.getName().equals("Temperature Sensor"))
                                .findFirst()
                                .orElseThrow();

                DeviceTemplate pressureSensor = templates.stream()
                                .filter(t -> t.getName().equals("Pressure Sensor"))
                                .findFirst()
                                .orElseThrow();

                DeviceTemplate gpsTracker = templates.stream()
                                .filter(t -> t.getName().equals("GPS Tracker"))
                                .findFirst()
                                .orElseThrow();

                MetricDefinition temperature = new MetricDefinition(
                                "Temperature",
                                "Current temperature.",
                                "temperature",
                                MetricDataType.NUMBER,
                                "°C",
                                temperatureSensor,
                                now,
                                now);

                MetricDefinition batteryVoltage = new MetricDefinition(
                                "Battery Voltage",
                                "Current battery voltage.",
                                "batteryVoltage",
                                MetricDataType.NUMBER,
                                "V",
                                temperatureSensor,
                                now,
                                now);

                MetricDefinition pressure = new MetricDefinition(
                                "Pressure",
                                "Current pressure.",
                                "pressure",
                                MetricDataType.NUMBER,
                                "psi",
                                pressureSensor,
                                now,
                                now);

                MetricDefinition pressureBattery = new MetricDefinition(
                                "Battery Voltage",
                                "Current battery voltage.",
                                "batteryVoltage",
                                MetricDataType.NUMBER,
                                "V",
                                pressureSensor,
                                now,
                                now);

                MetricDefinition latitude = new MetricDefinition(
                                "Latitude",
                                "Current latitude.",
                                "latitude",
                                MetricDataType.NUMBER,
                                "degrees",
                                gpsTracker,
                                now,
                                now);

                MetricDefinition longitude = new MetricDefinition(
                                "Longitude",
                                "Current longitude.",
                                "longitude",
                                MetricDataType.NUMBER,
                                "degrees",
                                gpsTracker,
                                now,
                                now);

                metricDefinitionRepository.saveAll(List.of(
                                temperature,
                                batteryVoltage,
                                pressure,
                                pressureBattery,
                                latitude,
                                longitude));

                return List.of(
                                temperature,
                                batteryVoltage,
                                pressure,
                                pressureBattery,
                                latitude,
                                longitude);
        }

        private List<TelemetryEvent> seedTelemetry(List<Device> devices) {

                Instant now = Instant.now();

                Device temp001 = devices.stream()
                                .filter(d -> d.getName().equals("TEMP-001"))
                                .findFirst()
                                .orElseThrow();

                Device temp002 = devices.stream()
                                .filter(d -> d.getName().equals("TEMP-002"))
                                .findFirst()
                                .orElseThrow();

                Device pressure001 = devices.stream()
                                .filter(d -> d.getName().equals("PRESS-001"))
                                .findFirst()
                                .orElseThrow();

                Device gps001 = devices.stream()
                                .filter(d -> d.getName().equals("GPS-001"))
                                .findFirst()
                                .orElseThrow();

                TelemetryEvent tempEvent1 = new TelemetryEvent(
                                temp001,
                                "{\"temperature\":23.8,\"batteryVoltage\":3.71}",
                                now,
                                now);

                TelemetryEvent tempEvent2 = new TelemetryEvent(
                                temp002,
                                "{\"temperature\":24.1,\"batteryVoltage\":3.69}",
                                now,
                                now);

                TelemetryEvent pressureEvent = new TelemetryEvent(
                                pressure001,
                                "{\"pressure\":138.2,\"batteryVoltage\":3.64}",
                                now,
                                now);

                TelemetryEvent gpsEvent = new TelemetryEvent(
                                gps001,
                                "{\"latitude\":25.7617,\"longitude\":-80.1918}",
                                now,
                                now);

                telemetryEventRepository.saveAll(List.of(
                                tempEvent1,
                                tempEvent2,
                                pressureEvent,
                                gpsEvent));

                return List.of(
                                tempEvent1,
                                tempEvent2,
                                pressureEvent,
                                gpsEvent);
        }

        private List<MetricValue> seedMetricValues(
                        List<TelemetryEvent> events,
                        List<MetricDefinition> definitions) {

                Instant now = Instant.now();

                TelemetryEvent tempEvent1 = events.get(0);
                TelemetryEvent tempEvent2 = events.get(1);
                TelemetryEvent pressureEvent = events.get(2);
                TelemetryEvent gpsEvent = events.get(3);

                MetricDefinition temperature = definitions.stream()
                                .filter(d -> d.getIncomingFieldName().equals("temperature"))
                                .findFirst()
                                .orElseThrow();

                MetricDefinition batteryVoltage = definitions.stream()
                                .filter(d -> d.getIncomingFieldName().equals("batteryVoltage"))
                                .findFirst()
                                .orElseThrow();

                MetricDefinition pressure = definitions.stream()
                                .filter(d -> d.getIncomingFieldName().equals("pressure"))
                                .findFirst()
                                .orElseThrow();

                MetricDefinition latitude = definitions.stream()
                                .filter(d -> d.getIncomingFieldName().equals("latitude"))
                                .findFirst()
                                .orElseThrow();

                MetricDefinition longitude = definitions.stream()
                                .filter(d -> d.getIncomingFieldName().equals("longitude"))
                                .findFirst()
                                .orElseThrow();

                MetricValue tempValue1 = new MetricValue(
                                tempEvent1,
                                temperature,
                                new BigDecimal("23.8"),
                                null,
                                null,
                                now);

                MetricValue tempBattery1 = new MetricValue(
                                tempEvent1,
                                batteryVoltage,
                                new BigDecimal("3.71"),
                                null,
                                null,
                                now);

                MetricValue tempValue2 = new MetricValue(
                                tempEvent2,
                                temperature,
                                new BigDecimal("24.1"),
                                null,
                                null,
                                now);

                MetricValue tempBattery2 = new MetricValue(
                                tempEvent2,
                                batteryVoltage,
                                new BigDecimal("3.69"),
                                null,
                                null,
                                now);

                MetricValue pressureValue = new MetricValue(
                                pressureEvent,
                                pressure,
                                new BigDecimal("138.2"),
                                null,
                                null,
                                now);

                MetricValue gpsLatitude = new MetricValue(
                                gpsEvent,
                                latitude,
                                new BigDecimal("25.7617"),
                                null,
                                null,
                                now);

                MetricValue gpsLongitude = new MetricValue(
                                gpsEvent,
                                longitude,
                                new BigDecimal("-80.1918"),
                                null,
                                null,
                                now);

                metricValueRepository.saveAll(List.of(
                                tempValue1,
                                tempBattery1,
                                tempValue2,
                                tempBattery2,
                                pressureValue,
                                gpsLatitude,
                                gpsLongitude));

                return List.of(
                                tempValue1,
                                tempBattery1,
                                tempValue2,
                                tempBattery2,
                                pressureValue,
                                gpsLatitude,
                                gpsLongitude);
        }
}
