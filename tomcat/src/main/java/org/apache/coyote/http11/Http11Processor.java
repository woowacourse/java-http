package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.common.HttpResponseUtil;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.common.ResponseEntity;
import org.apache.coyote.http11.request.body.RequestBody;
import org.apache.coyote.http11.request.headers.RequestHeaders;
import org.apache.coyote.http11.request.line.RequestLine;
import org.apache.coyote.http11.request.line.vo.QueryString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger LOG = LoggerFactory.getLogger(Http11Processor.class);
    private static final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
    private static final String INDEX_PAGE = "/index.html";
    private static final String REGISTER_PAGE = "/register.html";
    private static final String LOGIN_PAGE = "/login.html";

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        LOG.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            final String firstLine = bufferedReader.readLine();
            if (firstLine == null) {
                return;
            }
            final RequestLine requestLine = RequestLine.from(firstLine);
            final RequestHeaders requestHeader = getHeaders(bufferedReader);
            final RequestBody requestBody = getBody(bufferedReader, requestHeader);
            String response = getResponse(requestLine);

//            final ResponseEntity responseEntity = handleRequest(requestLine, requestHeader, requestBody);
//            final String response = httpResponseGenerator.generate(responseEntity);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private String getResponse(final RequestLine requestLine) throws IOException {
        final String path = requestLine.path().defaultPath();
        final Map<String, String> queryString = requestLine.path().queryString();

        if (path.equals("/")) {
            final String responseBody = "Hello world!";
            return HttpResponseUtil.generate(path, responseBody);
        }
        if (path.equals("/login")) {
            final User account = InMemoryUserRepository.findByAccount(queryString.get("account"))
                    .orElseThrow();
            LOG.info("login: {}", account);
            final URL resource = classLoader.getResource("static" + path + ".html");
            final File file = new File(resource.getFile());
            final String responseBody = new String(Files.readAllBytes(file.toPath()));
            return HttpResponseUtil.generate(path, responseBody);
        }
        final URL resource = classLoader.getResource("static" + path);
        final File file = new File(resource.getFile());
        final String responseBody = new String(Files.readAllBytes(file.toPath()));
        return HttpResponseUtil.generate(path, responseBody);
    }

    private RequestHeaders getHeaders(final BufferedReader bufferedReader) throws IOException {
        List<String> requestHeaders = new ArrayList<>();
        for (String line = bufferedReader.readLine(); !"".equals(line); line = bufferedReader.readLine()) {
            requestHeaders.add(line);
        }
        return RequestHeaders.from(requestHeaders);
    }

    private List<String> readRequestHeaders(final BufferedReader bufferedReader) throws IOException {
        final List<String> requestHeaders = new ArrayList<>();
        String line;

        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            requestHeaders.add(line);
        }
        return requestHeaders;
    }

    private RequestBody getBody(final BufferedReader bufferedReader, final RequestHeaders requestHeaders)
            throws IOException {
        List<String> contentLengths = requestHeaders.headers().get("Content-Length");
        if (contentLengths == null) {
            return RequestBody.from(null);
        }
        int contentLength = Integer.parseInt(contentLengths.get(0));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return RequestBody.from(new String(buffer));
    }

    private ResponseEntity handleRequest(
            final RequestLine requestLine,
            final RequestHeaders requestHeaders,
            final RequestBody requestBody
    ) {
        final String path = requestLine.path().defaultPath();
        if (path.equals("/login")) {
            return null;
        }
        if (path.equals("/register")) {
            return null;
        }
        return new ResponseEntity(HttpStatus.OK, path);
    }

}
