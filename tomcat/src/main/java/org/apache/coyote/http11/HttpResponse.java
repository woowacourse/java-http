package org.apache.coyote.http11;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.coyote.http11.handler.HandlerResult;
import org.apache.coyote.http11.request.HttpRequest;

public class HttpResponse {

    private final HttpRequest request;
    private final HandlerResult handlerResult;

    public HttpResponse(HttpRequest request, HandlerResult handlerResult) {
        this.request = request;
        this.handlerResult = handlerResult;
    }

    public String getResponse() {
        final HttpStatusCode statusCode = handlerResult.getStatusCode();
        final List<String> headers = handlerResult.getResponseHeader()
                .entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                .collect(Collectors.toList());

        String result = request.getProtocol() + " " + statusCode.getCode() + " " + statusCode.getValue() + " ";

        for (String header : headers) {
            result = String.join("\r\n", result, header);
        }

        return String.join("\r\n",
                result,
                "",
                handlerResult.getResponseBody());
    }
}
