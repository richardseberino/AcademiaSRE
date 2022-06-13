package com.gm4c.healthcheck.repository;

import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import com.gm4c.healthcheck.dto.CassandraHealthIndicatorDto;

public interface CassandraHealthIndicatorRepository extends CassandraRepository<CassandraHealthIndicatorDto, String> 
{
	
	@Query("SELECT release_version from system.local")
	List<CassandraHealthIndicatorDto> findVersion();

}
