package com.techcourse.servlet;

public interface Servlet {

    boolean canHandle(byte[] input);

    String handle(byte[] input);
}
