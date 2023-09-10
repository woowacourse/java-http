package org.apache.coyote.http11.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import org.apache.coyote.http11.ResourceProvider;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.http11.service.LoginService;

public class HandlerMapper {

    private final Map<Mapper, Controller> controllerByMapper = new HashMap<>();
    private final ResourceProvider resourceProvider;

    public HandlerMapper(ResourceProvider resourceProvider) {
        enrollHandler();
        this.resourceProvider = resourceProvider;
    }

    private void enrollHandler() {
        controllerByMapper.put(
            request -> "/login".equals(request.getRequestLine().getPath()) &&
                HttpMethod.POST.equals(request.getRequestLine().getMethod()),
            new LoginController(new LoginService()));

        controllerByMapper.put(
            request -> "/register".equals(request.getRequestLine().getPath()) &&
                HttpMethod.POST.equals(request.getRequestLine().getMethod()),
            new SignUpController(new LoginService()));

        controllerByMapper.put(
            request -> "/login".equals(request.getRequestLine().getPath()) &&
                HttpMethod.GET.equals(request.getRequestLine().getMethod()),
            new LoginViewController());

        controllerByMapper.put(
            request -> "/register".equals(request.getRequestLine().getPath()) &&
                HttpMethod.GET.equals(request.getRequestLine().getMethod()),
            new SignUpViewController());
    }

    public boolean haveAvailableHandler(HttpRequest httpRequest) {
        return controllerByMapper.keySet().stream()
            .anyMatch(mapper -> mapper.canHandle(httpRequest));
    }

    private Controller getHandler(HttpRequest httpRequest) {
        Mapper mapper = controllerByMapper.keySet().stream()
            .filter(mp -> mp.canHandle(httpRequest))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("해당 요청을 해결할 수 있는 핸들러가 없습니다."));
        return controllerByMapper.get(mapper);
    }

    public String controllerResponse(HttpRequest httpRequest) {
        Controller handler = getHandler(httpRequest);
        ResponseEntity<Object> responseEntity = (ResponseEntity<Object>) handler.handle(httpRequest);
        return makeResponse(responseEntity);
    }

    private String makeResponse(ResponseEntity<Object> responseEntity) {
        StringBuilder response = new StringBuilder();
        response.append(requestLine(responseEntity));
        Optional<String> body = bodyOf(responseEntity);
        if (body.isPresent()) {
            return response.append(responseWithBody(responseEntity, body.get())).toString();
        }
        String str = responseWithoutBody(responseEntity);
        response.append("\r\n");
        return response.append(str).toString();
    }


    private String requestLine(ResponseEntity<Object> responseEntity) {
        HttpStatusCode httpStatusCode = HttpStatusCode.of(responseEntity.getStatusCode());
        return "HTTP/1.1 " + httpStatusCode.getStatusCode() + " " + httpStatusCode.name() + " ";
    }

    private Optional<String> bodyOf(ResponseEntity<Object> responseEntity) {
        if (responseEntity.isViewResponse()) {
            return Optional.of(resourceProvider.resourceBodyOf(responseEntity.getViewPath()));
        }
        return Optional.empty();
    }

    private String responseWithBody(ResponseEntity<Object> responseEntity, String body) {
        Map<String, String> headers = responseEntity.getHeaders();
        StringJoiner stringJoiner = new StringJoiner("\r\n");
        stringJoiner.add(headerResponse(headers));
        stringJoiner.add(resourceProvider.contentTypeOf(responseEntity.getViewPath()));
        stringJoiner.add("Content-Length: " + body.getBytes().length + " ");
        stringJoiner.add("");
        stringJoiner.add(body);
        return stringJoiner.toString();
    }

    private String responseWithoutBody(ResponseEntity<Object> responseEntity) {
        Map<String, String> headers = responseEntity.getHeaders();
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
