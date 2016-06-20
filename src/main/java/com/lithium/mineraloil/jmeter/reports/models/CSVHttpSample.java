package com.lithium.mineraloil.jmeter.reports.models;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by viren.thakkar on 6/16/16.
 */

@Getter
@Setter
public class CSVHttpSample {

    public long timeStamp;
    public long elapsed;
    public String label;
    public String responseCode;
    public String responseMessage;
    public String threadName;
    public String success;
    public long bytes;
    public int grpThreads;
    public int allThreads;
    public int Latency;
    public int SampleCount;
    public int ErrorCount;
    public String Hostname;
    public int IdleTime;
}
