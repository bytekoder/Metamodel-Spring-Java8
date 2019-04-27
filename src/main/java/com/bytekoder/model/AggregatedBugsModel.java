package com.bytekoder.model;

import lombok.Data;

@Data
public class AggregatedBugsModel {

    private String testerId;
    private String deviceId;
    private String device;
    private long bugsByTesterPerDevice;
}
