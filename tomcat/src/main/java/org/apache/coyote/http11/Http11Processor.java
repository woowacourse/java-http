package org.apache.coyote.http11;

import org.apache.coyote.dto.RequestInfo;
import org.apache.coyote.router.RequestRouter;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Map;
import org.apache.coyote.Processor;
import org.apache.coyote.config.AppConfig;
import org.apache.coyote.cookie.HttpCookie;
import org.apache.coyote.dto.RequestInfo;
import org.apache.coyote.router.RequestRouter;
import org.apache.coyote.util.HeaderParser;
import org.apache.coyote.util.PostBodyParser;
import org.apache.coyote.util.RequestLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestRouter requestRouter;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.requestRouter = AppConfig.getInstance().getRequestRouter();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final var reader = new BufferedReader(new InputStreamReader(inputStream))) {

            final String response = createResponse(reader);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String createResponse(final BufferedReader reader) throws IOException {
        RequestInfo requestInfo = getRequestLine(reader);

        Map<String, String> header = HeaderParser.parseHeader(reader);

        String cookieHeader = header.get("Cookie");
        HttpCookie httpCookie = new HttpCookie(cookieHeader);

        if (requestInfo.method().equals("POST")) {
            requestInfo = getPostRequestInfo(reader, header, requestInfo);
        }

        return requestRouter.handleRoute(
                requestInfo.method(),
                requestInfo.path(),
                requestInfo.queryParams(),
                httpCookie
        );
    }

    private RequestInfo getRequestLine(BufferedReader reader) throws IOException {
        final String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            return null;
        }

        return RequestLineParser.parse(requestLine);
    }

    private RequestInfo getPostRequestInfo(BufferedReader reader, Map<String, String> header, RequestInfo requestInfo)
            throws IOException {
        int contentLength = Integer.parseInt(header.get("Content-Length"));
        Map<String, String> postParams = PostBodyParser.parse(reader, contentLength);
        requestInfo = new RequestInfo(requestInfo.method(), requestInfo.path(), postParams);
        return requestInfo;
    }
}