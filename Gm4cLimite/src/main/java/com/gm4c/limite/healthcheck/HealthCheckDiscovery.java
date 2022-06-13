package com.gm4c.limite.healthcheck;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "management.health.package", matchIfMissing = false)
@ComponentScan("${management.health.package}")
public class HealthCheckDiscovery {

}
