package org.apache.coyote.http11;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HttpRequest {

    private static final String CONTENT_LENGTH = "Content-Length";

    private final RequestLine requestLine;
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> queryParameters = new HashMap<>();
    private final Map<String, String> formParameters = new HashMap<>();
    private final byte[] body;

    private Session session;

    public HttpRequest(InputStream inputStream) throws IOException {
        BufferedInputStream input = new BufferedInputStream(inputStream);

        requestLine = RequestLine.parse(readLine(input));
        readHeaders(input);

        queryParameters.putAll(parseParameters(requestLine.getQueryString()));
        body = readBody(input);

        String contentType = getHeader("Content-Type");
        if (contentType != null && contentType.split(";", 2)[0].trim()
                .equalsIgnoreCase("application/x-www-form-urlencoded")) {
            formParameters.putAll(parseParameters(new String(body, UTF_8)));
        }
    }

    private void readHeaders(BufferedInputStream input) throws IOException {
        while (true) {
            String line = readLine(input);

            if (line == null) {
                throw new EOFException("헤더를 읽는 중 연결이 끝났습니다.");
            }
            if (line.isEmpty()) {
                return; // 빈 줄: 헤더가 끝나고 본문 시작
            }

            int colon = line.indexOf(':');
            if (colon <= 0) {
                throw new IOException("잘못된 헤더: " + line);
            }

            String name = line.substring(0, colon).trim().toLowerCase(Locale.ROOT);
            String value = line.substring(colon + 1).trim();
            headers.put(name, value);
        }
    }

    private byte[] readBody(BufferedInputStream input) throws IOException {
        String contentLength = getHeader(CONTENT_LENGTH);

        if (contentLength == null) {
            return new byte[0];
        }

        final int length;
        try {
            length = Integer.parseInt(contentLength);
        } catch (NumberFormatException e) {
            throw new IOException("잘못된 Content-Length: " + contentLength, e);
        }

        if (length < 0) {
            throw new IOException("Content-Length는 음수일 수 없습니다");
        }

        byte[] bytes = input.readNBytes(length);
        if (bytes.length != length) {
            throw new EOFException("본문이 Content-Length보다 짧습니다.");
        }
        return bytes;
    }

    private static String readLine(BufferedInputStream input) throws IOException {
        ByteArrayOutputStream line = new ByteArrayOutputStream();

        while (true) {
            int current = input.read();

            if (current == -1) {
                if (line.size() == 0) {
                    return null;
                }
                throw new EOFException("줄을 읽는 중 연결이 끊겼습니다.");
            }

            if (current == '\r') {
                if (input.read() != '\n') {
                    throw new IOException("CR 뒤에 LF가 없습니다.");
                }
                return line.toString(ISO_8859_1);
            }

            if (current == '\n') {
                throw new IOException("LF 앞에 CR이 없습니다.");
            }

            line.write(current);
        }
    }

    private Map<String, String> parseParameters(String encoded) throws IOException {
        Map<String, String> parameters = new HashMap<>();

        if (encoded == null || encoded.isEmpty()) {
            return parameters;
        }

        try {
            for (String pair : encoded.split("&")) {
                if (pair.isEmpty()) {
                    continue;
                }

                String[] keyValue = pair.split("=", 2);
                String key = URLDecoder.decode(keyValue[0], UTF_8);
                String value = keyValue.length > 1 ? URLDecoder.decode(keyValue[1], UTF_8) : "";

                parameters.put(key, value);
            }
        } catch (IllegalArgumentException e) {
            throw new IOException("잘못 인코딩된 파라미터입니다.", e);
        }

        return parameters;
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getHttpVersion() {
        return requestLine.getHttpVersion();
    }

    public String getHeader(String name) {
        return headers.get(name.toLowerCase(Locale.ROOT));
    }

    public String getParameter(String name) {
        if (formParameters.containsKey(name)) {
            return formParameters.get(name);
        }
        return queryParameters.get(name);
    }

    public byte[] getBody() {
        return body.clone();
    }

    public Session getSession() {
        return session;
    }

    void setSession(Session session) {
        this.session = session;
    }
}
