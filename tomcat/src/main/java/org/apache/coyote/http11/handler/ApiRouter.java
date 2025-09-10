package org.apache.coyote.http11.handler;

import java.util.Map.Entry;
import org.apache.coyote.http11.general.ContentType;
import org.apache.coyote.http11.general.HttpProtocolVersion;
import org.apache.coyote.http11.handler.controllerResponse.ApplicationResponse;
import org.apache.coyote.http11.httpRequest.HttpRequest;
import org.apache.coyote.http11.httpResponse.HttpResponse;
import org.apache.coyote.http11.httpResponse.HttpStatus;

public class ApiRouter {

    private final RoutingTable routingTable;

    public ApiRouter() {
        this.routingTable = RoutingTable.initializeRouteMap();
    }

    public HttpResponse route(HttpRequest httpRequest) {
        Controller controller = routingTable.findControllerOfPath(httpRequest.getPath());
        if (controller == null) {
            return HttpResponse.of(httpRequest.getProtocolVersion(), HttpStatus.NOT_FOUND, ContentType.TEXT_HTML, "존재하지 않는 엔드포인트입니다.");
        }
        ApplicationResponse applicationResponse = controller.service(httpRequest);
        return handleHttpResponse(applicationResponse, httpRequest.getProtocolVersion());
    }

    private HttpResponse handleHttpResponse(ApplicationResponse applicationResponse, HttpProtocolVersion protocolVersion) {
        HttpResponse httpResponse = applicationResponse.toHttpResponse(protocolVersion);
        addHeadersFromControllerResponse(httpResponse, applicationResponse);
        return httpResponse;
    }

    private void addHeadersFromControllerResponse(HttpResponse httpResponse, ApplicationResponse applicationResponse) {
        for (Entry<String, String> header : applicationResponse.headers().getHeaders().entrySet()) {
            httpResponse.addHeader(header.getKey(), header.getValue());
        }
    }
}
