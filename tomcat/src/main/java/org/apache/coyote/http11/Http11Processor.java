package org.apache.coyote.http11;

import com.techcourse.handler.HelloController;
import com.techcourse.handler.LoginController;
import com.techcourse.handler.NotFoundController;
import com.techcourse.handler.RegisterController;
import org.apache.catalina.Manager;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Manager sessionManager;
    private final ResourceProcessor resourceProcessor;

    public Http11Processor(Socket connection, Manager sessionManager) {
        this.connection = connection;
        this.sessionManager = sessionManager;
        this.resourceProcessor = new ResourceProcessor(createRequestMapping(), new StaticResourceController());
    }

    private RequestMapping createRequestMapping() {
        Map<String, Controller> mapping = new HashMap<>();
        mapping.put("/", new HelloController());
        mapping.put("/login", new LoginController());
        mapping.put("/register", new RegisterController());

        return new RequestMapping(mapping, new NotFoundController());
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {
            HttpRequest httpRequest = createHttpRequest(inputStream);
            HttpResponse httpResponse = resourceProcessor.processResponse(httpRequest);
            outputStream.write(httpResponse.serialize());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest createHttpRequest(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String requestLine = bufferedReader.readLine();
        Header header = createHeader(bufferedReader);
        HttpBody body = createRequestBody(bufferedReader, header);

        return HttpRequest.createHttp11Request(requestLine, header, body, sessionManager);
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
        String contentLength = header.get(HttpHeaderKey.CONTENT_LENGTH.getName()).orElse("0");
        String contentType = header.get(HttpHeaderKey.CONTENT_TYPE).orElse("");
        char[] requestBody = new char[Integer.parseInt(contentLength)];
        bufferedReader.read(requestBody);

        return HttpBodyFactory.generateHttpBody(ContentType.from(contentType), requestBody);
    }
}
