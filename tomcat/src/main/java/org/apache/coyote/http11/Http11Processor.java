package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.parser.Http11GetProcessor;
import org.apache.coyote.http11.parser.RequestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Http11GetProcessor http11RequestProcessor;
    private final SessionManager sessionManager;

    private int contentLength = 0;

    public Http11Processor(final Socket connection, SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.http11RequestProcessor = new Http11GetProcessor();
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            ParseHttpRequest httpRequests = readRequestFromReader(bufferedReader);

            Session session = getSession(httpRequests.cookies()
                    .getSessionId());

            httpRequests = httpRequests.addSession(session);

            RequestResult requestResult = http11RequestProcessor.doRequest(httpRequests);
            byte[] parsedContent = requestResult.getParseContent();

            byte[] response = String.join(
                            "\r\n",
                            requestResult.getHttpResponseStatus(),
                            requestResult.getAdditionalResponse(),
                            httpRequests.cookies()
                                    .getCookieResponse(),
                            "Content-Length: " + parsedContent.length + " ",
                            "",
                            new String(parsedContent)
                    )
                    .getBytes();

            outputStream.write(response);
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Session getSession(String sessionId) throws IOException {
        Session session = sessionManager.findSession(sessionId);
        if (session == null) {
            sessionManager.add(new Session(sessionId));
            return sessionManager.findSession(sessionId);
        }
        return session;
    }

    private ParseHttpRequest readRequestFromReader(final BufferedReader bufferedReader) throws IOException {
        String buffer = bufferedReader.readLine();
        if (isReqeustExist(buffer)) {
            ParseHttpRequest parseHttpRequest = new ParseHttpRequest(
                    parseMethod(buffer),
                    parseContentPath(buffer),
                    new HashMap<>(),
                    new HttpCookies(new HashMap<>()),
                    new Session(null)
            );
            Map<String, String> cookies = parseCookie(bufferedReader);
            parseHttpRequest = parseHttpRequest.addCookies(cookies);

            if (AcceptableRequest.isPost(parseHttpRequest.method())) {
                Map<String, String> requestBody = parseBody(bufferedReader);
                return parseHttpRequest.addRequestBody(requestBody);
            }

            return parseHttpRequest;
        }
        throw new IllegalArgumentException("유효하지 않은 HTTP 요청입니다.");
    }

    private Map<String, String> parseCookie(BufferedReader bufferedReader) throws IOException {
        String buffer;
        while (!(buffer = bufferedReader.readLine()).isEmpty()) {
            if (buffer.split("Content-Length: ").length != 1) {
                contentLength = Integer.parseInt(buffer.split("Content-Length: ")[1]);
            }

            if (!buffer.contains("Cookie: ")) {
                continue;
            }

            String[] cookieKeyValues = buffer.replace("Cookie: ", "")
                    .replace(" ", "")
                    .split(";");

            return createCookieKeyValue(cookieKeyValues);
        }

        return new HashMap<>();
    }

    private Map<String, String> createCookieKeyValue(String[] cookieKeyValues) {
        Map<String, String> cookieKeyValueMap = new HashMap<>();

        for (String cookieKeyValue : cookieKeyValues) {
            String[] splitedKeyValue = cookieKeyValue.split("=");
            if (splitedKeyValue.length == 2) {
                cookieKeyValueMap.put(splitedKeyValue[0], splitedKeyValue[1]);
            }
        }

        return cookieKeyValueMap;
    }

    private Map<String, String> parseBody(BufferedReader bufferedReader) throws IOException {
        String buffer;
        Map<String, String> map = new HashMap<>();
        while (!(buffer = bufferedReader.readLine()).isEmpty()) {
            if (buffer.split("Content-Length: ").length != 1) {
                contentLength = Integer.parseInt(buffer.split("Content-Length: ")[1]);
            }
        }

        char[] contents = new char[contentLength];
        bufferedReader.read(contents, 0, contentLength);

        String requestBody = new String(contents);
        return createRequestBody(requestBody, map);
    }

    private Map<String, String> createRequestBody(String requestBody, Map<String, String> map) {
        String[] requestBodies = requestBody.split("&");

        for (String s : requestBodies) {
            if (s.isEmpty()) {
                continue;
            }
            String[] keyValues = s.split("=");
            map.put(keyValues[0], keyValues[1]);
        }

        return map;
    }

    private boolean isReqeustExist(String buffer) {
        return AcceptableRequest.isRequestExist(buffer);
    }

    private String parseContentPath(String buffer) {
        return buffer.split(" ")[1];
    }

    private String parseMethod(String buffer) {
        return buffer.split(" ")[0];
    }
}
