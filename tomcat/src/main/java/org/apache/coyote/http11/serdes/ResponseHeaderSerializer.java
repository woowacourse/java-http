package org.apache.coyote.http11.serdes;

import java.util.Map;
import java.util.StringJoiner;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseHeader;

public class ResponseHeaderSerializer implements Serializer<HttpResponseHeader> {
    private static final String PROTOCOL_AND_VERSION = "HTTP/" + HttpResponse.DEFAULT_VERSION.getVersion() + " ";
    private static final String CRLF = "\r\n";
    public static final String HEADER_PAYLOAD_DELIMITER = ": ";
    private static final String BLANK = "";
    public static final String SPACE = " ";

    @Override
    public String serialize(HttpResponseHeader header) {
        String firstLine = resolveFirstLine(header.getStatusCode());
        String headerPayLoads = resolvePayLoads(header.getPayLoads());

        StringJoiner joiner = new StringJoiner(CRLF);

        joiner.add(firstLine);
        joiner.add(headerPayLoads);

        return joiner.toString();
    }

    private String resolveFirstLine(StatusCode statusCode) {
        StringJoiner joiner = new StringJoiner(SPACE, PROTOCOL_AND_VERSION, SPACE);
        joiner.add(String.valueOf(statusCode.getCode()));
        joiner.add(statusCode.getMessage());
        return joiner.toString();
    }

    private String resolvePayLoads(Map<String, String> payLoads) {
        StringJoiner joiner = new StringJoiner(SPACE + CRLF, BLANK, " ");

        payLoads.keySet()
                .forEach(key -> joiner.add(key + HEADER_PAYLOAD_DELIMITER + payLoads.get(key)));

        return joiner.toString();
    }
}
