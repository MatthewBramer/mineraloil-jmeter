package com.lithium.mineraloil.jmeter.reports;

import lombok.extern.slf4j.Slf4j;
import org.apache.jmeter.report.dashboard.ReportGenerator;

@Slf4j
public class DashboardReport {
    public DashboardReport(String filename) {
        try {
            new ReportGenerator(filename, null).generate();
        } catch (Exception e) {
            log.error("Error generating dashboard report: {}", e.getMessage());
        }
    }
}
