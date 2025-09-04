package org.apache.catalina.servlet.impl;

import com.http.enums.HttpStatus;
import java.io.IOException;
import org.apache.catalina.domain.HttpRequest;
import org.apache.catalina.domain.HttpResponse;
import org.apache.catalina.domain.ResponseStartLine;
import org.apache.catalina.servlet.RequestServlet;
import org.apache.catalina.util.FileParser;

public class DefaultServlet implements RequestServlet {

    @Override
    public void handle(HttpRequest request, HttpResponse response) throws IOException {
        final String path = request.requestStartLine().path();

        if (!FileParser.existsFile(path)) {
            final ResponseStartLine responseStartLine = new ResponseStartLine("HTTP/1.1", HttpStatus.NOT_FOUND);
            response.setStartLine(responseStartLine);
            return;
        }

        final byte[] responseBody = FileParser.loadStaticResource(path);

        response.setBody(responseBody);
    }
}
