package com.bytekoder.exception;


public class TesterNotFoundException extends RuntimeException {

    public TesterNotFoundException(String device, String country) {
        super("No tester found in country [" + country + "] with Device [" + device + "]");
    }
}
