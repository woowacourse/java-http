package org.apache.catalina.core.config;

import org.apache.catalina.core.controller.RequestMapping;

public interface Configuration {

    void addController(final RequestMapping requestMapping);

    void setExceptionHandler(final RequestMapping requestMapping);

    void setResourceController(final RequestMapping requestMapping);
}
