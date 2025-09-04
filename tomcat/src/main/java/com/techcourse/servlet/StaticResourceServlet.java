package com.techcourse.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.catalina.Servlet;
import org.apache.coyote.http11.ContentTypeMapper;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticResourceServlet implements Servlet {

    private static final Logger log = LoggerFactory.getLogger(StaticResourceServlet.class);

    @Override
    public void init() {
        log.info("StaticResourceServlet initialized");
    }

    @Override
    public void service(final HttpRequest request, final HttpResponse response) {
        if (!"GET".equals(request.getMethod())) {
            response.setStatus(405);
            response.write("<html><body><h1>405 Method Not Allowed</h1></body></html>");
            return;
        }

        String uri = request.getUri();

        // 루트 경로면 index.html로
        if ("/".equals(uri)) {
            uri = "/index.html";
        }

        final String content = readStaticFile(uri);
        final String contentType = ContentTypeMapper.get(uri);

        response.setContentType(contentType);
        response.write(content);
    }

    private String readStaticFile(final String uri) {
        try (final InputStream inputStream = getClass().getClassLoader().getResourceAsStream("static" + uri)) {
            if (inputStream == null) {
                log.warn("Static file not found: {}", uri);
                return createErrorPage("File Not Found", 404);
            }
            
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (final IOException e) {
            log.error("Failed to read static file: {}", uri, e);
            return createErrorPage("Error loading file", 500);
        }
    }

    private String createErrorPage(final String message, final int statusCode) {
        return String.format("""
            <html>
            <head><title>Error %d</title></head>
            <body>
                <h1>%s</h1>
                <p>Status Code: %d</p>
            </body>
            </html>
            """, statusCode, message, statusCode);
    }

    @Override
    public void destroy() {
        log.info("StaticResourceServlet destroyed");
    }
}
