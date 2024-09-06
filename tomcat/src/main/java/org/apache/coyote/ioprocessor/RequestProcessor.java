package org.apache.coyote.ioprocessor;

import com.techcourse.infrastructure.PresentationResolver;
import http.HttpRequestBody;
import http.HttpRequestHeaders;
import http.HttpRequestLine;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.ioprocessor.parser.HttpRequest;
import org.apache.coyote.ioprocessor.parser.HttpResponse;

public class RequestProcessor {

    private static final String REQUEST_PART_DELIMITER = "\r\n\r\n";
    private static final String REQUEST_HEADER_DELIMITER = "\r\n";


    private final HttpRequest httpRequest;
    private final PresentationResolver resolver;

    public RequestProcessor(InputStream inputStream) throws IOException {
        this.httpRequest = readRequestFromInputStream(inputStream);
        this.resolver = new PresentationResolver();
    }

    private HttpRequest readRequestFromInputStream(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequestLine requestLine = parseRequestLine(reader);
        HttpRequestHeaders headers = parseReqeustHeaders(reader);
        HttpRequestBody requestBody = parseRequestBody(headers, reader);

        return new HttpRequest(requestLine, headers, requestBody);
    }

    private HttpRequestLine parseRequestLine(BufferedReader reader) throws IOException {
        String requestLine = reader.readLine();
        return new HttpRequestLine(requestLine);
    }

    private HttpRequestHeaders parseReqeustHeaders(BufferedReader reader) throws IOException {
        List<String> headers = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                break;
            }
            headers.add(line);
        }
        return new HttpRequestHeaders(headers);
    }

    private HttpRequestBody parseRequestBody(HttpRequestHeaders headers, BufferedReader reader) throws IOException {
        int contentLength = headers.getContentLength();
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);
        return new HttpRequestBody(requestBody);
    }

    public String processHttpResponse() throws URISyntaxException, IOException {
        HttpResponse response = resolver.resolve(httpRequest);
        return response.buildResponse();
    }
}
