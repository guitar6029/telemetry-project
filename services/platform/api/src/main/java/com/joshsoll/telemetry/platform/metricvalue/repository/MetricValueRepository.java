package com.joshsoll.telemetry.platform.metricvalue.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.joshsoll.telemetry.platform.metricvalue.entity.MetricValue;

public interface MetricValueRepository extends JpaRepository<MetricValue, UUID> {

}
