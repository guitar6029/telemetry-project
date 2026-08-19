package com.joshsoll.telemetry.platform.device.importer.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.joshsoll.telemetry.platform.device.importer.entity.DeviceImport;
import com.joshsoll.telemetry.platform.device.importer.entity.DeviceImportArtifact;
import com.joshsoll.telemetry.platform.device.importer.exception.DeviceImportArtifactException;
import com.joshsoll.telemetry.platform.device.importer.repository.DeviceImportArtifactRepository;

@Service
public class DeviceImportArtifactService {

    private final DeviceImportArtifactRepository deviceImportArtifactRepository;

    public DeviceImportArtifactService(
            DeviceImportArtifactRepository deviceImportArtifactRepository) {
        this.deviceImportArtifactRepository = deviceImportArtifactRepository;
    }

    public UUID saveArtifact(
            DeviceImport deviceImport,
            String fileName,
            String contentType,
            InputStream artifact) {

        try {
            byte[] content = artifact.readAllBytes();

            DeviceImportArtifact entity = new DeviceImportArtifact(
                    deviceImport,
                    fileName,
                    contentType,
                    content);

            return deviceImportArtifactRepository.save(entity).getId();

        } catch (IOException ex) {
            throw new DeviceImportArtifactException(
                    "Failed to save device import artifact.", ex);
        }
    }
}
