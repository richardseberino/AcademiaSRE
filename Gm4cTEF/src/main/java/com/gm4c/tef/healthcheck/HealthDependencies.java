package com.gm4c.tef.healthcheck;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.gm4c.tef.repository.DependenciesHealthIndicatorRepository;

@Configuration
@ConfigurationProperties(prefix = "management.health")
public class HealthDependencies {
        
        private List<DependenciesHealthIndicatorRepository> dependencies;

        public List<DependenciesHealthIndicatorRepository> getDependencies() {
            return dependencies;
        }

        public void setDependencies(List<DependenciesHealthIndicatorRepository> dependencies) {
            this.dependencies = dependencies;
        }
}