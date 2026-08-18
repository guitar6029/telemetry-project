package com.joshsoll.telemetry.platform.device.importer.storage;

import java.io.InputStream;
import java.util.UUID;

public interface DeviceImportArtifactStorage {
    String store(UUID importId, InputStream data);

    InputStream retrieve(String storageKey);

    void delete(String storageKey);
}
