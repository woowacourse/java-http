package org.apache.coyote.http11;

public interface Configuration {

    void addControllers(RequestMapping requestMapping);

    void setFileController(RequestMapping requestMapping);
}
