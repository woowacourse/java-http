package org.apache.coyote;

import org.apache.coyote.http11.RequestHandler;
import org.apache.coyote.http11.RequestMapping;

public abstract class Config {

    public void init() {
        addControllers();
        setExceptionHandler();
    }

    protected abstract void addControllers();

    protected abstract void setExceptionHandler();

    protected void addController(final Controller controller) {
        RequestMapping.addController(controller);
    }

    protected void setExceptionHandler(final ExceptionHandler exceptionHandler) {
        RequestHandler.setExceptionHandler(exceptionHandler);
    }
}
