package com.joshsoll.telemetry.platform.device.importer.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.joshsoll.telemetry.platform.device.importer.entity.OutboxMessage;

public interface OutboxMessageRepository extends JpaRepository<OutboxMessage, UUID> {

    List<OutboxMessage> findTop10ByPublishedAtIsNullOrderByCreatedAtAsc();
}
