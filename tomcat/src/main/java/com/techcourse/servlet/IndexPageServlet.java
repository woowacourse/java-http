package com.techcourse.servlet;

import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static org.apache.coyote.HttpRequest.HttpMethod.GET;

public class IndexPageServlet implements Servlet {

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.method() == GET && request.uri().equals("/index.html");
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        final var responseBody = indexHtml();
        return HttpResponse.ok().html(responseBody).build();
    }

    private String indexHtml() {
        try (final var inputStream = getClass().getClassLoader().getResourceAsStream("static/index.html")
        ) {
            if (inputStream == null) throw new IllegalStateException("파일을 찾을 수 없습니다: index.html");

            final var reader = new BufferedReader(new InputStreamReader(inputStream));
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
