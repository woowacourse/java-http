package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class HttpRequest {

    private final MappingLine mappingLine;
    private final Map<String, String> headers;
    private final byte[] body;

    public HttpRequest(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        MappingLine mappingLine = new MappingLine(bufferedReader);
        Map<String, String> headers = getHeaders(bufferedReader);
        byte[] body = getBody(headers, bufferedReader);

        this.mappingLine = mappingLine;
        this.headers = headers;
        this.body = body;
    }

    private static Map<String, String> getHeaders(BufferedReader bufferedReader) throws IOException {
        String line;
        Map<String, String> headers = new LinkedHashMap<>();
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            int idx = line.indexOf(':'); // split 2
            if (idx <= 0) {
                throw new IllegalArgumentException();
            }
            String name = line.substring(0, idx).trim();
            String value = line.substring(idx + 1).trim();
            headers.put(name, value);
        }
        return headers;
    }

    private byte[] getBody(Map<String, String> headers, BufferedReader bufferedReader) throws IOException {
        String contentLength = headers.get("Content-Length");
        if (contentLength == null) {
            return null;
        }

        int length = Integer.parseInt(contentLength);
        return bufferedReader.readLine().getBytes(String.valueOf(length));
    }

    public MappingLine getMappingLine() {
        return mappingLine;
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
}
