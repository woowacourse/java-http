package org.apache.coyote.http11.handle;

import org.apache.coyote.http11.handle.logic.dto.HandleResponse;
import org.apache.coyote.http11.handle.logic.LogicHandler;
import org.apache.coyote.http11.handle.view.CssHandler;
import org.apache.coyote.http11.handle.view.HtmlHandler;
import org.apache.coyote.http11.handle.view.IcoHandler;
import org.apache.coyote.http11.handle.view.JSHandler;
import org.apache.coyote.http11.handle.view.ViewHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.start.HttpExtension;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.apache.coyote.http11.request.start.HttpExtension.*;
import static org.apache.coyote.http11.response.HttpStatus.*;
import static org.apache.coyote.http11.response.HttpStatus.FOUND;
import static org.apache.coyote.http11.response.HttpStatus.OK;

public class DispatcherServlet {
    private static final Map<HttpExtension, ViewHandler> viewHandlers;
    private static final LogicHandler logicHandler = new LogicHandler();

    static {
        viewHandlers = new HashMap<>();
        viewHandlers.put(HTML, new HtmlHandler());
        viewHandlers.put(CSS, new CssHandler());
        viewHandlers.put(JS, new JSHandler());
        viewHandlers.put(ICO, new IcoHandler());
    }

    public static HttpResponse from(HttpRequest request) throws IOException {
        HttpExtension extension = request.getHttpStartLine().getExtension();
        if (extension.equals(DEFAULT)) {
            return handleLogicRequest(request);
        }
        return handleViewRequest(request, extension);
    }

    private static HttpResponse handleViewRequest(final HttpRequest request, final HttpExtension extension) throws IOException {
        final ViewHandler viewHandler = viewHandlers.get(extension);
        return viewHandler.handle(request);
    }

    private static HttpResponse handleLogicRequest(final HttpRequest request) {
        final HandleResponse handleResponse = logicHandler.handle(request);
        if (handleResponse.getHttpStatus().equals(OK)
                || handleResponse.getHttpStatus().equals(FOUND)) {
            return HttpResponse.of(request.getHttpStartLine().getHttpVersion(), FOUND.getStatusName(), handleResponse.getBody(), "/index.html");
        }
        if (handleResponse.getHttpStatus().equals(NOT)) {
            return HttpResponse.of(request.getHttpStartLine().getHttpVersion(), FOUND.getStatusName(), "/login.html");
        }
        return HttpResponse.of(request.getHttpStartLine().getHttpVersion(), FOUND.getStatusName(), "/login.html");
    }
}
