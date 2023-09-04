package org.apache.coyote.http11.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import org.apache.coyote.http11.ResourceProvider;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.apache.coyote.http11.service.LoginService;

public class HandlerMapper {

    private final Map<Mapper, Controller> contollerByMapper = new HashMap<>();
    private final ResourceProvider resourceProvider;

    public HandlerMapper(ResourceProvider resourceProvider) {
        enrollHandler();
        this.resourceProvider = resourceProvider;
    }

    private void enrollHandler() {
        contollerByMapper.put(
            request -> "/login".equals(request.getRequestLine().getPath()) &&
                HttpMethod.POST.equals(request.getRequestLine().getMethod()),
            new LoginController(new LoginService()));

        contollerByMapper.put(
            request -> "/register".equals(request.getRequestLine().getPath()) &&
                HttpMethod.POST.equals(request.getRequestLine().getMethod()),
            new SignUpController(new LoginService()));

        contollerByMapper.put(
            request -> "/login".equals(request.getRequestLine().getPath()) &&
                HttpMethod.GET.equals(request.getRequestLine().getMethod()),
            new LoginViewController());

        contollerByMapper.put(
            request -> "/register".equals(request.getRequestLine().getPath()) &&
                HttpMethod.GET.equals(request.getRequestLine().getMethod()),
            new SignUpViewController());
    }

    public boolean haveAvailableHandler(HttpRequest httpRequest) {
        return contollerByMapper.keySet().stream()
            .anyMatch(mapper -> mapper.canHandle(httpRequest));
    }

    public Controller getHandler(HttpRequest httpRequest) {
        Mapper mapper = contollerByMapper.keySet().stream()
            .filter(mp -> mp.canHandle(httpRequest))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("해당 요청을 해결할 수 있는 핸들러가 없습니다."));
        return contollerByMapper.get(mapper);
    }

    public String controllerResponse(HttpRequest httpRequest) {
        Controller handler = getHandler(httpRequest);
        HttpResponse<Object> httpResponse = (HttpResponse<Object>) handler.handle(httpRequest);
        return makeResponse(httpResponse);
    }

    private String makeResponse(HttpResponse<Object> httpResponse) {
        StringBuilder response = new StringBuilder();
        response.append(requestLine(httpResponse));
        Optional<String> body = bodyOf(httpResponse);
        if (body.isPresent()) {
            return response.append(responseWithBody(httpResponse, body.get())).toString();
        }
        String str = responseNoBody(httpResponse);
        response.append("\r\n");
        return response.append(str).toString();
    }


    private String requestLine(HttpResponse<Object> httpResponse) {
        HttpStatusCode httpStatusCode = HttpStatusCode.of(httpResponse.getStatusCode());
        return "HTTP/1.1 " + httpStatusCode.getStatusCode() + " " + httpStatusCode.name() + " ";
    }

    private Optional<String> bodyOf(HttpResponse<Object> httpResponse) {
        if (httpResponse.isViewResponse()) {
            return Optional.of(resourceProvider.resourceBodyOf(httpResponse.getViewPath()));
        }
        return Optional.empty();
    }

    private String responseWithBody(HttpResponse<Object> httpResponse, String body) {
        Map<String, String> headers = httpResponse.getHeaders();
        StringJoiner stringJoiner = new StringJoiner("\r\n");
        stringJoiner.add(headerResponse(headers));
        stringJoiner.add(resourceProvider.contentTypeOf(httpResponse.getViewPath()));
        stringJoiner.add("Content-Length: " + body.getBytes().length + " ");
        stringJoiner.add("");
        stringJoiner.add(body);
        return stringJoiner.toString();
    }

    private String responseNoBody(HttpResponse<Object> httpResponse) {
        Map<String, String> headers = httpResponse.getHeaders();
        StringJoiner stringJoiner = new StringJoiner("\r\n");
        stringJoiner.add(headerResponse(headers));
        stringJoiner.add("");
        return stringJoiner.toString();
    }

    private String headerResponse(Map<String, String> headers) {
        return headers.keySet()
            .stream()
            .map(headerName -> makeHeader(headerName, headers.get(headerName)))
            .collect(Collectors.joining("\r\n"));
    }

    private String makeHeader(String headerName, String value) {
        return headerName + ": " + value;
    }

    @FunctionalInterface
    private interface Mapper {

        Boolean canHandle(HttpRequest httpRequest);
    }
}
