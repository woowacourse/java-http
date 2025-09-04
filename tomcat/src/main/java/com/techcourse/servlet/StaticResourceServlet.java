package com.techcourse.servlet;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
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
        try {
            final URL resource = getClass().getClassLoader().getResource("static" + uri);
            if (resource == null) {
                return "<html><body><h1>404 File Not Found</h1></body></html>";
            }
            return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        } catch (final Exception e) {
            log.error("Failed to read static file: " + uri, e);
            return "<html><body><h1>Error loading file</h1></body></html>";
        }
    }

    @Override
    public void destroy() {
        log.info("StaticResourceServlet destroyed");
    }
}
