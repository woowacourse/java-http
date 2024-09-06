package org.apache.coyote.ioprocessor;

import http.HttpRequestHeaders;
import http.HttpRequestLine;
import http.requestheader.Accept;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import org.apache.coyote.ioprocessor.parser.RequestParser;

public class RequestProcessor {

    private final RequestParser parser;

    public RequestProcessor(InputStream inputStream) throws IOException {
        String httpRequest = readRequestFromInputStream(inputStream);
        this.parser = new RequestParser(httpRequest);
    }

    private String readRequestFromInputStream(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder httpRequest = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            httpRequest.append(line).append("\r\n");
            if (line.isEmpty()) {
                httpRequest.append("\r\n");
                break;
            }
        }
        return httpRequest.toString();
    }

    public String processHttpResponse() throws URISyntaxException, IOException {
        String responseBody = processResponseBody();
        String contentMediaType = getContentMediaType();
        String httpResponse = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentMediaType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody
        );
        return httpResponse;
    }

    private String processResponseBody() throws IOException, URISyntaxException {
        if (parser.isRootUri()) {
            return "Hello world!";
        }
        return parser.readResource();
    }

    private String getContentMediaType() {
        HttpRequestHeaders requestHeaders = parser.getRequestHeaders();
        HttpRequestLine requestLine = parser.getRequestLine();
        Accept accept = requestHeaders.getAcceptValue();
        return accept.processContentType(requestLine.getUri());
    }
}
