package org.apache.coyote;

import static com.techcourse.exception.ErrorMessage.INVALID_HTTP_REQUEST_FORMAT;
import static com.techcourse.exception.ErrorMessage.INVALID_REQUEST_LINE;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Request {

    private static final Logger log = LoggerFactory.getLogger(Request.class);
    private String httpMethod = "";

    private String url = "";

    private String protocolVersion = "";

    private Map<String, String> headers;

    private String body = "";

    public Request(BufferedReader br) {
        headers = new LinkedHashMap<>();
        try {
            String[] requestLineTokens = parseRequestLine(br);
            httpMethod = requestLineTokens[0];
            url = requestLineTokens[1];
            protocolVersion = requestLineTokens[2];
            parseRequestHeaders(br);
            if (httpMethod.equalsIgnoreCase("post")){
               parseBody(br);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void parseBody(BufferedReader br) throws IOException {
        int contentLength = Integer.parseInt(headers.get("Content-Length").trim());
        char[] buffer = new char[contentLength];
        br.read(buffer, 0, contentLength);
        body = new String(buffer);
    }

    private String[] parseRequestLine(BufferedReader br) throws IOException {
        String requestLine = br.readLine();

        if (requestLine == null || requestLine.isEmpty()) {
            throw new IllegalArgumentException(INVALID_REQUEST_LINE.getMessage());
        }
        String[] parts = requestLine.split(" ");
        if (parts.length < 3) {
            throw new IllegalArgumentException(INVALID_HTTP_REQUEST_FORMAT.getMessage());
        }
        return parts;
    }

    private void parseRequestHeaders(BufferedReader br) throws IOException {
        String line;
        while((line = br.readLine()) != null && !line.isEmpty()) {
            int delimiter = line.indexOf(":");
            addHeader(line.substring(0, delimiter), line.substring(delimiter+1));
        }
    }

    public void addHeader(String key, String val) {
        headers.put(key, val);
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getUrl() {
        return url;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

}
