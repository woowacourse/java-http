package nextstep.jwp.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class RequestParser {

    private static final Logger log = LoggerFactory.getLogger(RequestParser.class);
    private static final String NEWLINE = "\r\n";
    private static final String HEADER_BODY_SEPARATOR = "\r\n\r\n";

    private BufferedReader bufferedReader;

    public RequestParser(InputStream inputStream) {
        this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public ClientRequest extractClientRequest() throws IOException {
        final StringBuilder header = new StringBuilder();
        String line;
        while (true) {
            line = bufferedReader.readLine();
            if (line == null || line.length() == 0) {
                break;
            }
            header.append(line).append("\r\n");
        }
        return parseClientRequest(header.toString());
    }

    public ClientRequest parseClientRequest(String header) throws IOException {
        log.info("Client Header \r\n{}", header);
        final String requestInfo = parseRequestInfo(header);
        final Map<String, String> requestHttpHeader = parseRequestHttpHeader(header);

        if (requestHttpHeader.containsKey("Content-Length")) {
            final int contentLength = Integer.parseInt(requestHttpHeader.get("Content-Length").trim());
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            final String requestBody = new String(buffer);
            return ClientRequest.from(requestInfo, requestHttpHeader, requestBody);
        }
        return ClientRequest.from(requestInfo, requestHttpHeader, "");
    }

    private String parseRequestInfo(String request) {
        final int firstLineIndex = request.indexOf(NEWLINE);
        return request.substring(0, firstLineIndex);
    }

    private Map<String, String> parseRequestHttpHeader(String request) {
        final int firstLineIndex = request.indexOf(NEWLINE);
        final int headerBodyIndex = request.indexOf(HEADER_BODY_SEPARATOR);
        if (headerBodyIndex == -1) {
            return parseHttpHeaders(request.substring(firstLineIndex + NEWLINE.length()));
        }
        return parseHttpHeaders(request.substring(firstLineIndex + NEWLINE.length(), headerBodyIndex));
    }

    private Map<String, String> parseHttpHeaders(String headerRequest) {
        final Map<String, String> httpHeader = new HashMap<>();
        final String[] headers = headerRequest.split(NEWLINE);
        for (String header : headers) {
            final String[] headerKeyAndValue = header.split(":");
            httpHeader.put(headerKeyAndValue[0], headerKeyAndValue[1]);
        }
        return httpHeader;
    }
//
//    private String parseRequestBody(String request) {
//        final int headerBodyIndex = request.indexOf(HEADER_BODY_SEPARATOR);
//        if (headerBodyIndex == -1) {
//            return "";
//        }
//        return request.substring(headerBodyIndex + HEADER_BODY_SEPARATOR.length());
//    }
}
