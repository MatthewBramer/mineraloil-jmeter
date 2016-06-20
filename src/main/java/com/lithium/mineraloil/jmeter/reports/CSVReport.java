package com.lithium.mineraloil.jmeter.reports;

import com.lithium.mineraloil.jmeter.reports.models.CSVHttpSample;
import lithium.datainv.classifier.PageNameClassifier;
import lithium.util.Config;
import org.supercsv.cellprocessor.ParseBool;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ParseLong;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

/**
 * Created by viren.thakkar on 6/16/16.
 */
public class CSVReport {

    String csvFileName;
    PageNameClassifier pageNameClassifier;
    public CSVReport(String fileName) {
        this.csvFileName = fileName;
        Config config = new lithium.util.Config();
        config.put("tapestry.context.name", "t5");
         this.pageNameClassifier = new PageNameClassifier(config);
    }

    public void createReportableResults(String reportableFileName) {
        ICsvBeanReader beanReader = null;
        ICsvBeanWriter beanWriter = null;
        try {
            beanReader = new CsvBeanReader(new FileReader(csvFileName), CsvPreference.STANDARD_PREFERENCE);
            File srcFile = new File(csvFileName);
            srcFile.getName().replace(".csv", "");

            beanWriter = new CsvBeanWriter(new FileWriter(reportableFileName),
                    CsvPreference.STANDARD_PREFERENCE);
            // the header elements are used to map the values to the bean (names must match)
            final String[] header = beanReader.getHeader(true);
            final CellProcessor[] processors = getProcessors();

            // write the header
            beanWriter.writeHeader(header);

            CSVHttpSample sample;
            while ((sample = beanReader.read(CSVHttpSample.class, header, processors)) != null) {
                String pageName=this.pageNameClassifier.classify(sample.getLabel());
                sample.setLabel(pageName);
                beanWriter.write(sample,header,processors);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (beanReader != null) {
                try {
                    beanReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if( beanWriter != null ) {
                try {
                    beanWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static CellProcessor[] getProcessors(){
        return new CellProcessor[] {
                new ParseLong(),
                new ParseLong(),
                new NotNull(),
                new org.supercsv.cellprocessor.Optional(),
                new org.supercsv.cellprocessor.Optional(),
                new org.supercsv.cellprocessor.Optional(),
                new org.supercsv.cellprocessor.Optional(),
                new ParseLong(),
                new ParseInt(),
                new ParseInt(),
                new ParseInt(),
                new ParseInt(),
                new ParseInt(),
                new org.supercsv.cellprocessor.Optional(),
                new ParseInt()
                };
    }


}
