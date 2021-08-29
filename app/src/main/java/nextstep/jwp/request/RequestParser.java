package nextstep.jwp.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class RequestParser {

    private static final Logger log = LoggerFactory.getLogger(RequestParser.class);
    private static final String NEWLINE = "\r\n";
    private static final String CONTENT_LENGTH_HEADER = "Content-Length";
    private static final String HTTP_HEADER_KEY_VALUE_SEPARATOR = ":";

    private final BufferedReader bufferedReader;

    public RequestParser(InputStream inputStream) {
        this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public ClientRequest extractClientRequest() throws IOException {
        final StringBuilder header = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.length() == 0) {
                break;
            }
            header.append(line).append("\r\n");
        }
        return parseClientRequest(header.toString());
    }

    public ClientRequest parseClientRequest(String header) throws IOException {
        final String requestInfo = parseRequestInfo(header);
        log.info("### Client Request Info = {}", requestInfo);

        final Map<String, String> requestHttpHeader = parseRequestHttpHeader(header);

        final String requestBody = parseRequestBody(requestHttpHeader);

        return ClientRequest.from(requestInfo, requestHttpHeader, requestBody);
    }

    private String parseRequestInfo(String request) {
        final int firstLineSeparatorIndex = request.indexOf(NEWLINE);
        return request.substring(0, firstLineSeparatorIndex);
    }

    private Map<String, String> parseRequestHttpHeader(String request) {
        final int firstLineSeparatorIndex = request.indexOf(NEWLINE);
        final String requestHeaders = request.substring(firstLineSeparatorIndex + NEWLINE.length());

        final Map<String, String> httpHeaders = new HashMap<>();
        final String[] headers = requestHeaders.split(NEWLINE);
        for (String header : headers) {
            final String[] headerKeyAndValue = header.split(HTTP_HEADER_KEY_VALUE_SEPARATOR);
            httpHeaders.put(headerKeyAndValue[0], headerKeyAndValue[1]);
        }
        return httpHeaders;
    }

    private String parseRequestBody(Map<String, String> requestHttpHeader) throws IOException {
        if (requestHttpHeader.containsKey(CONTENT_LENGTH_HEADER)) {
            final int contentLength = Integer.parseInt(requestHttpHeader.get(CONTENT_LENGTH_HEADER).trim());
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            return new String(buffer);
        }
        return null;
    }
}
