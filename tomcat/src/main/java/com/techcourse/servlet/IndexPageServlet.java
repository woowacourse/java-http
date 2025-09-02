package com.techcourse.servlet;

import org.apache.coyote.HttpRequest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static org.apache.coyote.HttpRequest.HttpMethod.GET;

public class IndexPageServlet implements Servlet {

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.method() == GET && request.uri().equals("/index.html");
    }

    @Override
    public String handle(HttpRequest request) {
        String responseBody = indexHtml();

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String indexHtml() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("static/index.html")
        ) {
            if (inputStream == null) throw new IllegalStateException("파일을 찾을 수 없습니다: index.html");

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
