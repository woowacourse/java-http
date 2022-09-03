package org.apache.config;

import java.util.List;
import org.apache.mvc.Controller;
import org.apache.mvc.handlerchain.ControllerRequestHandlerChain;
import org.apache.mvc.handlerchain.NotFoundHandlerChain;
import org.apache.mvc.handlerchain.RequestHandlerChain;
import org.apache.mvc.handlerchain.StaticFileRequestHandlerChain;

public class CompositionRoot {

    public RequestHandlerChain getHandlerChain(List<Controller> controllers) {
        return new ControllerRequestHandlerChain(
                new StaticFileRequestHandlerChain(
                        new NotFoundHandlerChain()
                ), controllers
        );
    }
}
