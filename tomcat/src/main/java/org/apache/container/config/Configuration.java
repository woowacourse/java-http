package org.apache.container.config;

import org.apache.container.handler.RequestMapper;

public interface Configuration {

    void addHandlers(RequestMapper requestMapper);

    void setDefaultHandler(RequestMapper requestMapper);

    void setExceptionHandler(RequestMapper requestMapper);
}
