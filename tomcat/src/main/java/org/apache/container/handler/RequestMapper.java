package org.apache.container.handler;

import java.util.HashMap;
import java.util.Map;
import org.apache.container.exception.ContainerNotInitializedException;

public class RequestMapper {

    private final Map<String, RequestHandler> handlers = new HashMap<>();
    private RequestHandler defaultHandler;
    private ExceptionHandler exceptionHandler;
    private boolean initialized = false;

    public void setDefaultHandler(final RequestHandler defaultHandler) {
        this.defaultHandler = defaultHandler;
        if (exceptionHandler != null) {
            initialized = true;
        }
    }

    public void setExceptionHandler(final ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        if (defaultHandler != null) {
            initialized = true;
        }
    }

    public void addHandler(final String path, final RequestHandler requestHandler) {
        handlers.put(path, requestHandler);
    }

    public RequestHandler findHandler(final String path) {
        if (!initialized) {
            throw new ContainerNotInitializedException();
        }
        return handlers.getOrDefault(path, defaultHandler);
    }

    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public boolean isInitialized() {
        return initialized;
    }
}
