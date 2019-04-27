package com.bytekoder.exception;


public class DeviceNotSupportedException extends RuntimeException {

    public DeviceNotSupportedException(final String device) {
        super("Device [" + device + "] not supported");
    }

}
