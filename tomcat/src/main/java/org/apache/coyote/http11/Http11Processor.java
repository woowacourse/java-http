package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.DefaultController;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.controller.RequestMapping;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestHeaders;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapping requestMapping;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.requestMapping = new RequestMapping(
                List.of(new LoginController(), new DefaultController(), new RegisterController()));
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(Socket connection) {
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {
            HttpRequest httpRequest = generateRequest(new BufferedReader(new InputStreamReader(inputStream)));
            HttpResponse httpResponse = new HttpResponse();

            Controller controller = requestMapping.getController(httpRequest);
            controller.service(httpRequest, httpResponse);
            String response = generateResponse(httpRequest, httpResponse);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest generateRequest(final BufferedReader bufferedReader) throws IOException {
        RequestLine requestLine = RequestLine.of(bufferedReader.readLine());
        RequestHeaders requestHeaders = generateRequestHeaders(bufferedReader);
        RequestBody requestBody = null;
        if (requestLine.getMethod().equalsIgnoreCase("POST")) {
            requestBody = generateRequestBody(bufferedReader, requestHeaders);
        }

        return new HttpRequest(requestLine, requestHeaders, requestBody);
    }

    private RequestHeaders generateRequestHeaders(BufferedReader bufferedReader) throws IOException {
        ArrayList<String> headers = new ArrayList<>();
        String read = bufferedReader.readLine();
        while (!read.isEmpty()) {
            headers.add(read);
            read = bufferedReader.readLine();
        }
        return new RequestHeaders(headers);
    }

    private RequestBody generateRequestBody(BufferedReader bufferedReader,
                                            RequestHeaders requestHeaders) throws IOException {
        int contentLength = Integer.parseInt((String) requestHeaders.get("Content-Length"));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);
        return new RequestBody(requestBody);
    }

    private String generateResponse(final HttpRequest httpRequest,
                                    final HttpResponse httpResponse) throws IOException {
        return String.join("\r\n",
                httpResponse.getStatusLine().getValue(),
                "Set-Cookie: " + httpRequest.getCookie(),
                httpRequest.getContentType(),
                "Content-Length: " + httpResponse.getResponseBody().getBytes().length + " ",
                "",
                httpResponse.getResponseBody());
    }
}
