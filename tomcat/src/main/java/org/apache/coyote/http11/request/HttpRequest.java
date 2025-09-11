package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class HttpRequest {

    private final MappingLine mappingLine;
    private final Map<String, String> headers;
    private final byte[] body;

    private HttpRequest(MappingLine mappingLine, Map<String, String> headers, byte[] body) {
        this.mappingLine = mappingLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        MappingLine mappingLine = new MappingLine(bufferedReader);
        Map<String, String> headers = parsedHeaders(bufferedReader);
        byte[] body = parsedBody(headers, bufferedReader);

        return new HttpRequest(mappingLine, headers, body);
    }

    private static Map<String, String> parsedHeaders(BufferedReader bufferedReader) throws IOException {
        String line;
        Map<String, String> headers = new LinkedHashMap<>();
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            int idx = line.indexOf(':');
            if (idx <= 0) {
                throw new IllegalArgumentException();
            }
            String name = line.substring(0, idx).trim();
            String value = line.substring(idx + 1).trim();
            headers.put(name, value);
        }
        return headers;
    }

    private static byte[] parsedBody(Map<String, String> headers, BufferedReader bufferedReader) throws IOException {
        String contentLength = headers.get("Content-Length");
        if (contentLength == null) {
            return new byte[0];
        }

        int length = Integer.parseInt(contentLength);
        if (length <= 0) {
            return new byte[0];
        }

        char[] cbuf = new char[length];
        int off = 0;
        while (off < length) {
            int n = bufferedReader.read(cbuf, off, length - off);

            if (n == -1) {
                break;
            }
            off += n;
        }

        String bodyString = new String(cbuf, 0, off);
        return bodyString.getBytes();
    }

    public MappingLine getMappingLine() {
        return mappingLine;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] toBytes() {
        StringBuilder sb = new StringBuilder();

        sb.append(mappingLine.toString()).append("\r\n");

        for (Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }

        sb.append("\r\n");

        byte[] headerBytes = sb.toString().getBytes(StandardCharsets.UTF_8);

        if (body == null) {
            return headerBytes;
        }

        byte[] result = new byte[headerBytes.length + body.length];
        System.arraycopy(headerBytes, 0, result, 0, headerBytes.length);
        System.arraycopy(body, 0, result, headerBytes.length, body.length);

        return result;
    }

    public byte[] getBody() {
        return body;
    }

    public Map<String, String> getBodyMap() {
        Map<String, String> map = new HashMap<>();
        String bodyStr = new String(body, StandardCharsets.UTF_8);

        String[] parsedBodies = bodyStr.split("&");
        for (String parsedBody : parsedBodies) {
            String[] split = parsedBody.split("=");
            if (split.length != 2) {
                throw new IllegalArgumentException("invalid body format like map : " + split.length);
            }
            map.put(split[0], split[1]);
        }

        return map;
    }
}
