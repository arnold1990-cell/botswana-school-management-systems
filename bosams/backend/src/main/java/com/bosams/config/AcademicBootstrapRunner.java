package com.bosams.config;

import com.bosams.service.AcademicsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AcademicBootstrapRunner implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(AcademicBootstrapRunner.class);

    private final AcademicsService academicsService;

    public AcademicBootstrapRunner(AcademicsService academicsService) {
        this.academicsService = academicsService;
    }

    @Override
    public void run(ApplicationArguments args) {
        var active = academicsService.ensureActiveYearSetup();
        log.info("Academic bootstrap complete activeYear={} activeId={}", active.getYear(), active.getId());
    }
}
