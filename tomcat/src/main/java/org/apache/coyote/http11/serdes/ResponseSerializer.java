package org.apache.coyote.http11.serdes;

import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import org.apache.coyote.http11.HttpRequestHeaders;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;

public class ResponseSerializer implements Serializer<HttpResponse> {
    private static String PROTOCOL_AND_VERSION = "HTTP/" + HttpResponse.DEFAULT_VERSION.getVersion() + " ";
    private static String RESPONSE_DELIMITER = "\r\n";
    private static String BLANK = "";

    @Override
    public String serialize(HttpResponse response) {
        ResponseBody responseBody = response.getResponseBody();
        String firstLine = resolveFirstLine(response.getStatusCode(), response.getStatusMessage());
        String header = serializeHeader(response.getHeaders());

        StringJoiner joiner = new StringJoiner(RESPONSE_DELIMITER);

        joiner.add(firstLine);
        joiner.add(header);
        joiner.add(BLANK);
        joiner.add(responseBody.getValue());

        return joiner.toString();
    }

    private String resolveFirstLine(int statusCode, String statusMessage) {
        StringJoiner joiner = new StringJoiner(" ", PROTOCOL_AND_VERSION, " ");
        joiner.add(String.valueOf(statusCode));
        joiner.add(statusMessage);
        return joiner.toString();
    }

    private String serializeHeader(HttpRequestHeaders headers) {
        StringJoiner joiner = new StringJoiner(" \r\n", BLANK, " ");
        Map<String, String> payLoads = headers.getPayLoads();

        payLoads.keySet().stream()
                .forEach(key -> joiner.add(key + ": " + payLoads.get(key)));

        return joiner.toString();
    }
}
