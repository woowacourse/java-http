package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.requests.HttpCookie;
import org.apache.coyote.requests.RequestBody;
import org.apache.coyote.requests.RequestHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Map;
import java.util.StringJoiner;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String HTTP_11 = "HTTP/1.1";

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
             BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream))) {
            String line = bufferedReader.readLine();
            if (line == null) {
                return;
            }

            String httpMethod = Arrays.stream(line.split(" "))
                    .findFirst()
                    .orElseGet(() -> "");

            String uri = Arrays.stream(line.split(" "))
                    .filter(it -> it.startsWith("/"))
                    .findAny()
                    .orElseGet(() -> "");

            RequestHeader requestHeader = readHeader(bufferedReader);
            RequestBody requestBody = readBody(bufferedReader, requestHeader);

            HttpResponse httpResponse = routeByHttpMethod(uri, httpMethod, requestHeader, requestBody);
            String header = createHeader(httpResponse);

            bufferedWriter.write(header + httpResponse.getBody());
            bufferedWriter.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse routeByHttpMethod(String uri, String httpMethod, RequestHeader requestHeader, RequestBody requestBody) throws IOException {
        if (httpMethod.equals("POST")) {
            return routePost(uri, requestHeader, requestBody);
        }

        return createHttpResponse(uri);
    }

    private HttpResponse routePost(String uri, RequestHeader requestHeader, RequestBody requestBody) throws IOException {
        switch (uri) {
            case "/register":
                return LoginProcessor.doRegister(requestBody);
            case "/login":
                return LoginProcessor.doLogin(requestHeader, requestBody);
            default:
                return createHttpResponse(uri);
        }
    }

    private RequestHeader readHeader(BufferedReader bufferedReader) throws IOException {
        final StringBuilder stringBuilder = new StringBuilder();
        for (String line = bufferedReader.readLine();
             !"".equals(line); line = bufferedReader.readLine()) {
            stringBuilder.append(line).append("\r\n");
        }
        return RequestHeader.from(stringBuilder.toString());
    }

    private RequestBody readBody(BufferedReader bufferedReader, RequestHeader requestHeader) throws IOException {
        final String contentLength = requestHeader.get("Content-Length");
        if (contentLength == null) {
            return new RequestBody();
        }
        final int length = Integer.parseInt(contentLength);
        char[] buffer = new char[length];
        bufferedReader.read(buffer, 0, length);
        return new RequestBody(new String(buffer));
    }

    private HttpResponse createHttpResponse(String uri) throws IOException {
        int index = uri.indexOf("?");
        String path = uri;
        String query = "";
        if (index != -1) {
            path = uri.substring(0, index);
            query = uri.substring(index + 1);
        }

        return ViewResolver.routePath(path, query);
    }

    private String createHeader(HttpResponse response) {
        StringJoiner stringJoiner = new StringJoiner("\r\n");
        HttpStatus httpStatus = response.getHttpStatus();

        stringJoiner.add(String.format("%s %d %s ", HTTP_11, httpStatus.getCode(), httpStatus.getMessage()));
        for (Map.Entry<String, String> entry : response.getHeaders().entrySet()) {
            stringJoiner.add(String.format("%s: %s ", entry.getKey(), entry.getValue()));
        }
        if (httpStatus.equals(HttpStatus.FOUND)) {
            stringJoiner.add(String.format("%s %s ", "Location:", response.getBody()));
            stringJoiner.add("\r\n");
            return stringJoiner.toString();
        }
        stringJoiner.add(String.format("%s %s ", "Content-Type:", response.getContentType()));
        stringJoiner.add(String.format("%s %s ", "Content-Length:", response.getBody().getBytes().length));
        stringJoiner.add("\r\n");
        return stringJoiner.toString();
    }
}
