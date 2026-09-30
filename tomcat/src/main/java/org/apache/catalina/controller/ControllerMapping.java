package org.apache.catalina.controller;

public interface ControllerMapping {

    Controller getController(String path);
}
