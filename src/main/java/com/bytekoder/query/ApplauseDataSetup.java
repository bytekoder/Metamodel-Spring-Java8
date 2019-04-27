package com.bytekoder.query;

import org.apache.metamodel.DataContext;
import org.apache.metamodel.DataContextFactory;
import org.apache.metamodel.csv.CsvConfiguration;
import org.apache.metamodel.schema.Schema;
import org.apache.metamodel.schema.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

 public class ApplauseDataSetup {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplauseDataSetup.class);

     private static final String BUGS_DATASET = "src/main/resources/datasets/bugs.csv";
     private static final String DEVICES_DATASET = "src/main/resources/datasets/devices.csv";
     private static final String TESTERS_DATASET = "src/main/resources/datasets/testers.csv";
     private static final String TESTERS_TO_DEVICE_MAP = "src/main/resources/datasets//tester_device.csv";

     private static CsvConfiguration csvConfiguration;

     static DataContext bugsDataCtx;
     static DataContext deviceDataCtx;
     static DataContext testerDataCtx;
     private static DataContext testerDeviceDataCtx;

     static Table bugs;
     static Table device;
     static Table testers;
     private static Table testerDevice;


    public static void setupTables() {

        csvConfiguration = new CsvConfiguration(1);
        bugsDataCtx = DataContextFactory.createCsvDataContext(new File(BUGS_DATASET), csvConfiguration);
        deviceDataCtx = DataContextFactory.createCsvDataContext(new File(DEVICES_DATASET), csvConfiguration);
        testerDataCtx = DataContextFactory.createCsvDataContext(new File(TESTERS_DATASET), csvConfiguration);
        testerDeviceDataCtx = DataContextFactory.createCsvDataContext(new File(TESTERS_TO_DEVICE_MAP), csvConfiguration);

        Schema bugsSchema = bugsDataCtx.getDefaultSchema();
        Schema deviceSchema = deviceDataCtx.getDefaultSchema();
        Schema testerSchema = testerDataCtx.getDefaultSchema();
        Schema testerDeviceSchema = testerDeviceDataCtx.getDefaultSchema();

        bugs = bugsSchema.getTables()[0];
        device = deviceSchema.getTables()[0];
        testers = testerSchema.getTables()[0];
        testerDevice = testerDeviceSchema.getTables()[0];

        LOGGER.info("Data is ready to be queried...");
    }
}
