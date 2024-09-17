package org.apache.coyote.http11;

import jakarta.http.ContentType;
import jakarta.http.Header;
import jakarta.http.HttpBody;
import jakarta.http.HttpBodyFactory;
import jakarta.http.HttpHeaderKey;
import jakarta.http.HttpRequest;
import jakarta.http.HttpResponse;
import jakarta.http.HttpSessionWrapper;
import jakarta.http.HttpVersion;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Http11Processor implements Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String DEFAULT_CONTENT_LENGTH = "0";
    private static final String DEFAULT_CONTENT_TYPE = "";

    private final Socket connection;
    private final HttpSessionWrapper httpSessionWrapper;


    public Http11Processor(Socket connection, HttpSessionWrapper httpSessionWrapper) {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        this.connection = connection;
        this.httpSessionWrapper = httpSessionWrapper;
    }

    @Override
    public void process(HttpResponse response) throws IOException {
        OutputStream outputStream = connection.getOutputStream();
        response.setHttpVersion(HttpVersion.HTTP_1_1);
        outputStream.write(response.serialize());
        outputStream.flush();
    }

    @Override
    public HttpRequest getRequest() throws IOException {
        InputStream inputStream = connection.getInputStream();
        return createHttpRequest(inputStream);
    }

    private HttpRequest createHttpRequest(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String requestLine = bufferedReader.readLine();
        Header header = createHeader(bufferedReader);
        HttpBody body = createRequestBody(bufferedReader, header);

        return HttpRequest.createHttpRequest(requestLine, header, body, HttpVersion.HTTP_1_1, httpSessionWrapper);
    }

    private Header createHeader(BufferedReader bufferedReader) throws IOException {
        List<String> headerTokens = new ArrayList<>();
        String line = bufferedReader.readLine();
        while (!line.isBlank()) {
            line = bufferedReader.readLine();
            headerTokens.add(line);
        }

        return new Header(headerTokens);
    }

    private HttpBody createRequestBody(BufferedReader bufferedReader, Header header) throws IOException {
        String contentLength = header.get(HttpHeaderKey.CONTENT_LENGTH.getName()).orElse(DEFAULT_CONTENT_LENGTH);
        String contentType = header.get(HttpHeaderKey.CONTENT_TYPE).orElse(DEFAULT_CONTENT_TYPE);
        char[] requestBody = new char[Integer.parseInt(contentLength)];
        bufferedReader.read(requestBody);

        return HttpBodyFactory.generateHttpBody(ContentType.from(contentType), requestBody);
    }
}
