package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.coyote.http11.common.Headers;

public class RequestBody {

    private static final String HEADER_FIELD_FOR_BODY = "Content-Length";
    public static final String PARAMETER_SEPARATOR = "&";
    public static final String NAME_VALUE_SEPARATOR = "=";
    public static final int NAME_INDEX = 0;
    public static final int VALUE_INDEX = 1;

    private final Map<String, String> query;

    public RequestBody(final Map<String, String> query) {
        this.query = query;
    }

    public static RequestBody of(final BufferedReader bufferedReader, final Headers headers) throws IOException {
        final String bodyLine = readBodyLine(bufferedReader, headers);
        if (bodyLine == null) {
            return new RequestBody(null);
        }
        final Map<String, String> form = new HashMap<>();
        for (String parameterLine : bodyLine.split(PARAMETER_SEPARATOR)) {
            String[] parameter = parameterLine.split(NAME_VALUE_SEPARATOR);
            form.put(parameter[NAME_INDEX], parameter[VALUE_INDEX]);
        }
        return new RequestBody(form);
    }

    private static String readBodyLine(final BufferedReader bufferedReader, final Headers headers) throws IOException {
        if (headers.containsField(HEADER_FIELD_FOR_BODY)) {
            final int expectLength = Integer.parseInt(headers.getByField(HEADER_FIELD_FOR_BODY));
            final char[] buffer = new char[expectLength];
            final int actualLength = bufferedReader.read(buffer, 0, expectLength);
            if (expectLength > actualLength) {
                return new String(buffer).substring(0, actualLength);
            }
            return new String(buffer);
        }
        return null;
    }

    public String getByName(final String name) {
        if (query.containsKey(name)) {
            return query.get(name);
        }
        throw new NoSuchElementException("요청 바디에 찾고자 하는 파라미터 이름이 존재하지 않습니다.");
    }
}
