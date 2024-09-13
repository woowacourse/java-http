package org.apache.catalina.mapper;

import org.apache.catalina.controller.Controller;

public class RequestMapping {

    public Controller getController(final String startLine) {
        if (startLine.equals("/login")) {
            return null;
        }
        if (startLine.equals("/registry")) {
            return null;
        }
        return null;
    }
}
