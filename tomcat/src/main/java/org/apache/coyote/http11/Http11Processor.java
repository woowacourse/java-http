package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Map;
import org.apache.coyote.Processor;
import org.apache.coyote.config.AppConfig;
import org.apache.coyote.cookie.HttpCookie;
import org.apache.coyote.dto.RequestLine;
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
        RequestLine requestLine = getRequestLine(reader);

        Map<String, String> header = HeaderParser.parseHeader(reader);

        String cookieHeader = header.get("Cookie");
        HttpCookie httpCookie = new HttpCookie(cookieHeader);

        if (requestLine.method().equals("POST")) {
            requestLine = getPostRequestInfo(reader, header, requestLine);
        }

        return requestRouter.handleRoute(
                requestLine.method(),
                requestLine.path(),
                requestLine.queryParams(),
                httpCookie
        );
    }

    private RequestLine getRequestLine(BufferedReader reader) throws IOException {
        final String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            return null;
        }

        return RequestLineParser.parse(requestLine);
    }

    private RequestLine getPostRequestInfo(BufferedReader reader, Map<String, String> header, RequestLine requestInfo)
            throws IOException {
        int contentLength = Integer.parseInt(header.get("Content-Length"));
        Map<String, String> postParams = PostBodyParser.parse(reader, contentLength);
        requestInfo = new RequestLine(requestInfo.method(), requestInfo.path(), postParams,requestInfo.version());
        return requestInfo;
    }
}