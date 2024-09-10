package org.apache.coyote.http;

import org.apache.coyote.http.request.HttpMethod;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.HttpStatus;
import org.apache.coyote.http.response.ResponseHeader;
import org.apache.coyote.http.response.StatusLine;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.apache.coyote.util.Constants.STATIC_RESOURCE_LOCATION;

public class HttpMessageGenerator {

    private HttpMessageGenerator() {
    }

    public static HttpRequest generateRequest(final BufferedReader reader) throws IOException {
        List<String> headerLines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            headerLines.add(line);
        }

        HttpRequest request = HttpRequest.of(headerLines);
        getRequestBody(reader, request);

        return request;
    }

    private static void getRequestBody(BufferedReader reader, HttpRequest request) throws IOException {
        if (request.getMethod().equals(HttpMethod.POST)) {
            int contentLength = request.getContentLength();
            char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            String requestBody = new String(buffer);
            request.setBody(requestBody);
        }
    }

    public static void generateStaticResponse(String path, HttpStatus status, HttpResponse response) throws IOException, NullPointerException {
        final URL resource = HttpResponse.class.getClassLoader().getResource(STATIC_RESOURCE_LOCATION + path);
        final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        StatusLine statusLine = new StatusLine(status);

        ResponseHeader header = new ResponseHeader();
        header.setContentType(MimeType.getContentTypeFromExtension(path));
        header.setContentLength(responseBody.getBytes().length);

        response.setResponse(statusLine, header, responseBody);
    }
}
