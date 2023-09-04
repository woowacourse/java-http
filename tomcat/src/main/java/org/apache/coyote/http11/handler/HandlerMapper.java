package org.apache.coyote.http11.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.apache.coyote.http11.Request;
import org.apache.coyote.http11.RequestLine;
import org.apache.coyote.http11.Response;

public class HandlerMapper {
    private static Map<HandlerStatus, Function<Request, Response>> HANDLERS = new HashMap<>();

    public HandlerMapper() {
        init();
    }

    public void init() {
        HANDLERS.put(new HandlerStatus("GET", "/"), this::rootHandler);
    }

    public Response rootHandler(final Request request) {
        final var responseBody = "Hello world!";

        final String response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        return new Response(response);
    }


    public Response htmlHandler(final Request request) {
        final var responseBody = request.getRequestLine().readFile();

        final String response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        return new Response(response);
    }


    public Response cssHandler(final Request request) {
        final var responseBody = request.getRequestLine().readFile();

        final String response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/css;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        return new Response(response);
    }

    public Response jsHandler(final Request request) {
        final var responseBody = request.getRequestLine().readFile();

        final String response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/javascript;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        return new Response(response);
    }

    public Response handle(final Request request) {
        final RequestLine requestLine = request.getRequestLine();
        final String path = requestLine.getPath();
        final String httpMethod = requestLine.getHttpMethod();
        final Map<String, String> queryParameter = requestLine.getRequestURI().getQueryParameter();
        final Set<String> queryParameterKeys = queryParameter.keySet();
        final HandlerStatus handlerStatus = new HandlerStatus(httpMethod, path, queryParameterKeys);

        final Function<Request, Response> handler = HANDLERS.get(handlerStatus);
        if (handler != null) {
            return handler.apply(request);
        }

        if (requestLine.getPath().endsWith(".html")) {
            return htmlHandler(request);
        } else if (requestLine.getPath().endsWith(".css")) {
            return cssHandler(request);
        } else if (requestLine.getPath().endsWith(".js")) {
            return jsHandler(request);
        }
        throw new IllegalArgumentException("매핑되는 핸들러가 존재하지 않습니다.");
    }
}
