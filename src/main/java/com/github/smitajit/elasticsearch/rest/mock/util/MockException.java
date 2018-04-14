package com.github.smitajit.elasticsearch.rest.mock.util;

public class MockException extends RuntimeException {

    public MockException(String message) {
        super(message);
    }

    public MockException(Throwable e) {
        super(e);
    }
}
