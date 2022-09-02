package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.message.common.ContentType;
import org.apache.coyote.http11.message.common.HttpHeaders;
import org.apache.coyote.http11.message.common.HttpVersion;
import org.apache.coyote.http11.message.request.RequestMessage;
import org.apache.coyote.http11.message.request.RequestUri;
import org.apache.coyote.http11.message.response.HttpStatus;
import org.apache.coyote.http11.message.response.ResponseMessage;
import org.apache.coyote.http11.util.StaticFileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String NEW_LINE = "\r\n";

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            String httpRequest = readHttpRequest(inputStream);
            RequestMessage requestMessage = new RequestMessage(httpRequest);

            if (requestMessage.getRequestUri().hasExtension()) { // 정적 파일 서빙
                ResponseMessage responseMessage = generateResponseMessage(requestMessage.getRequestUri());
                writeHttpResponseMessage(outputStream, responseMessage);
            }

            if (requestMessage.getRequestUri().getPath().equals("/")) {
                ResponseMessage responseMessage = generateResponseMessage(new RequestUri("/index.html"));
                writeHttpResponseMessage(outputStream, responseMessage);
            }

            if (requestMessage.getRequestUri().getPath().equals("/login")) {
                ResponseMessage responseMessage = generateResponseMessage(new RequestUri("/login.html"));
                writeHttpResponseMessage(outputStream, responseMessage);
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readHttpRequest(final InputStream inputStream) throws IOException {
        Reader reader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(reader);

        StringBuilder httpRequest = new StringBuilder();
        while (bufferedReader.ready()) {
            String line = bufferedReader.readLine();
            httpRequest.append(line).append(NEW_LINE);
        }

        return httpRequest.toString();
    }

    private ResponseMessage generateResponseMessage(final RequestUri requestUri) {
        String body = getStaticFile(requestUri);
        String fileExtension = requestUri.getExtension();
        HttpHeaders headers = generateResponseHeader(fileExtension, body);

        return new ResponseMessage(HttpVersion.HTTP11, HttpStatus.OK, headers, body);
    }

    private String getStaticFile(final RequestUri requestUri) {
        String staticFilePath = requestUri.getPath();
        return StaticFileUtil.readFile(staticFilePath);
    }

    private HttpHeaders generateResponseHeader(final String fileExtension, final String responseBody) {
        ContentType contentType = ContentType.from(fileExtension);

        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Content-Type", contentType.getValue() + ";charset=utf-8");
        responseHeaders.put("Content-Length", String.valueOf(responseBody.getBytes().length));

        return new HttpHeaders(responseHeaders);
    }

    private void writeHttpResponseMessage(final OutputStream outputStream, final ResponseMessage responseMessage)
            throws IOException {
        outputStream.write(responseMessage.generateMessage().getBytes());
        outputStream.flush();
    }
}
