package org.apache.coyote.http11.response;

import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import org.apache.coyote.http11.request.HttpHeaders;

public class ResponseSerializer {
    private static String PROTOCOL_AND_VERSION = HttpResponse.PROTOCOL + "/" + HttpResponse.version;
    private static String RESPONSE_DELIMITER = "\r\n";
    private static String BLANK = "";


    public String serialize(HttpResponse response) {
        Optional<String> responseBody = response.getResponseBody();
        String firstLine = resolveFirstLine(response.getStatusCode(), response.getStatusMessage());
        String header = serializeHeader(response.getHeaders());

        StringJoiner joiner = new StringJoiner(RESPONSE_DELIMITER);

        joiner.add(firstLine);
        joiner.add(header);
        joiner.add(BLANK);
        responseBody.ifPresent(joiner::add);

        return joiner.toString();
    }

    private String resolveFirstLine(int statusCode, String statusMessage) {
        StringBuilder builder = new StringBuilder(" ");
        builder.append(PROTOCOL_AND_VERSION);
        builder.append(statusCode);
        builder.append(statusMessage + " ");
        return builder.toString();
    }

    private String serializeHeader(HttpHeaders headers) {
        StringJoiner joiner = new StringJoiner(" ");
        Map<String, String> payLoads = headers.getPayLoads();

        payLoads.keySet().stream()
                .forEach(key -> joiner.add(key + ": " + payLoads.get(key)));

        return joiner.toString();
    }
}
