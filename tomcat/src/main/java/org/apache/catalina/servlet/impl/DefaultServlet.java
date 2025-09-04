package org.apache.catalina.servlet.impl;

import java.io.IOException;
import org.apache.catalina.domain.HttpRequest;
import org.apache.catalina.domain.HttpResponse;
import org.apache.catalina.servlet.RequestServlet;
import org.apache.catalina.util.FileParser;

public class DefaultServlet implements RequestServlet {

    @Override
    public void handle(HttpRequest request, HttpResponse response) throws IOException {
        final String path = request.startLine().path();
        final byte[] responseBody = FileParser.loadStaticResource(path);

        response.setBody(responseBody);
    }
}
