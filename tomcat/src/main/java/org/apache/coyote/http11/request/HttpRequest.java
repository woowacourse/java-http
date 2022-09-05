package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.request.headers.RequestHeader;
import org.apache.exception.TempException;
import org.apache.util.NumberUtil;

public class HttpRequest {

    private final RequestGeneral requestGeneral;
    private final RequestHeaders requestHeaders;
    private final RequestBody requestBody;

    private HttpRequest(RequestGeneral requestGeneral, RequestHeaders requestHeaders, RequestBody requestBody) {
        this.requestGeneral = requestGeneral;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    public static HttpRequest parse(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        RequestGeneral general = readGeneralLine(reader);
        RequestHeaders headers = readHeaderLines(reader);
        RequestBody body = readBodyLines(reader, headers);

        return new HttpRequest(general, headers, body);
    }

    private static RequestGeneral readGeneralLine(BufferedReader reader) {
        return RequestGeneral.parse(readOneLine(reader));
    }

    private static RequestHeaders readHeaderLines(BufferedReader reader) {
        List<String> lines = new ArrayList<>();
        String line;
        while (!"".equals(line = readOneLine(reader))) {
            lines.add(line);
        }
        return RequestHeaders.parse(lines);
    }

    private static RequestBody readBodyLines(BufferedReader reader, RequestHeaders headers) {
        int length = findContentLength(headers);
        if (length == 0) {
            return RequestBody.empty();
        }
        return readActualBody(reader, length);
    }

    private static int findContentLength(RequestHeaders headers) {
        try {
            return NumberUtil.parseIntSafe(headers.findValueByField("Content-Length"));
        } catch (IllegalArgumentException e) {
            return 0;
        }
    }

    private static RequestBody readActualBody(BufferedReader reader, int length) {
        char[] buffer = new char[length];
        try {
            reader.read(buffer, 0, length);
            return new RequestBody(new String(buffer));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String readOneLine(BufferedReader reader) {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new TempException();
        }
    }

    public RequestHeader findHeader(String field) {
        return requestHeaders.getHeader(field);
    }

    public String getPath() {
        return requestGeneral.getPath().getPath();
    }

    public RequestMethod getMethod() {
        return requestGeneral.getMethod();
    }

    public String getRequestBody() {
        return requestBody.getBody();
    }

    @Override
    public String toString() {
        return "HttpRequest{\n" +
                "requestGeneral=" + requestGeneral +
                ", requestHeaders=" + requestHeaders +
                ", requestBody=" + requestBody +
                "\n}";
    }
}
