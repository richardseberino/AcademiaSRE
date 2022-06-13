package com.gm4c.healthcheck.dto;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("local")
public class CassandraHealthIndicatorDto {
		
		@Column private String release_version;

		@PrimaryKey
		@Column private String key;

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getRelease_version() {
			return release_version;
		}

		public void setRelease_version(String release_version) {
			this.release_version = release_version;
		}
		
}
