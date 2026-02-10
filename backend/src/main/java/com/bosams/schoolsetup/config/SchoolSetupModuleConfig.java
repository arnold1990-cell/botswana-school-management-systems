package com.bosams.schoolsetup.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity
public class SchoolSetupModuleConfig {
    // This foundational module provides reusable reference data for every downstream BoSAMS bounded context.
}
