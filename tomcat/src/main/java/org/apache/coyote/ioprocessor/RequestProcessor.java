package org.apache.coyote.ioprocessor;

import com.techcourse.infrastructure.PresentationResolver;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import org.apache.coyote.ioprocessor.parser.HttpRequest;
import org.apache.coyote.ioprocessor.parser.HttpResponse;

public class RequestProcessor {

    private final HttpRequest httpRequest;
    private final PresentationResolver resolver;

    public RequestProcessor(InputStream inputStream) throws IOException {
        String httpRequest = readRequestFromInputStream(inputStream);
        this.httpRequest = new HttpRequest(httpRequest);
        this.resolver = new PresentationResolver();
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
        HttpResponse response = resolver.resolve(httpRequest);
        return response.buildResponse();
    }
}
