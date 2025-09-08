package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.exception.MethodNotAllowedException;
import org.apache.coyote.http11.domain.HttpMethod;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(AbstractController.class);

    protected Map<HttpMethod, HttpMethodCommand> commands;

    protected AbstractController() {
        commands = new HashMap<>();
        registerCommands();
    }

    protected abstract void registerCommands();

    protected void addCommand(HttpMethod method, HttpMethodCommand command) {
        commands.put(method, command);
    }

    @Override
    public String service(final Http11Request request, final Http11Response response) {
        log.debug("Request HTTP Method: {}", request.getMethod());
        final HttpMethodCommand command = commands.get(request.getMethod());
        if (command == null) {
            throw new MethodNotAllowedException(response);
        }
        return command.execute(request, response);
    }
}
