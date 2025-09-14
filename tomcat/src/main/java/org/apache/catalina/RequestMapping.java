package org.apache.catalina;

public interface RequestMapping {

    boolean isExistsController(String resourcePath);

    Controller getController(String resourcePath);
}
