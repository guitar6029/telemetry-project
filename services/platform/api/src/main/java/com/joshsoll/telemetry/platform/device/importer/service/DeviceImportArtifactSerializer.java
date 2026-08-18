package com.joshsoll.telemetry.platform.device.importer.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.springframework.stereotype.Service;

import com.joshsoll.telemetry.platform.device.importer.dto.PreparedDeviceImportRow;
import com.joshsoll.telemetry.platform.device.importer.exception.DeviceImportArtifactException;

import tools.jackson.databind.ObjectMapper;

@Service
public class DeviceImportArtifactSerializer {

    private final ObjectMapper objectMapper;

    public DeviceImportArtifactSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public InputStream serialize(List<PreparedDeviceImportRow> rows) {
        ByteArrayOutputStream data = new ByteArrayOutputStream();

        try (GZIPOutputStream gzip = new GZIPOutputStream(data);
                Writer writer = new OutputStreamWriter(gzip, StandardCharsets.UTF_8)) {

            for (PreparedDeviceImportRow row : rows) {
                writer.write(objectMapper.writeValueAsString(row));
                writer.write('\n');
            }
        } catch (IOException ex) {
            throw new DeviceImportArtifactException(
                    "Failed to serialize device import artifact.", ex);
        }

        // Closing GZIPOutputStream finalizes the compressed stream.
        return new ByteArrayInputStream(data.toByteArray());
    }
}
