package org.apache.coyote.http11.serdes;

import java.util.StringJoiner;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;

public class ResponseSerializer implements Serializer<HttpResponse> {

    private  final ResponseHeaderSerializer headerSerializer;

    public ResponseSerializer(ResponseHeaderSerializer headerSerializer) {
        this.headerSerializer = headerSerializer;
    }

    private static final String RESPONSE_DELIMITER = "\r\n";
    private static final String BLANK = "";

    @Override
    public String serialize(HttpResponse response) {

        StringJoiner joiner = new StringJoiner(RESPONSE_DELIMITER);

        String headerResponse = headerSerializer.serialize(response.getHeaders());
        joiner.add(headerResponse);
        joiner.add(BLANK);

        if (response.hasResponseBody()) {
            ResponseBody responseBody = response.getResponseBody();
            joiner.add(responseBody.getValue());
        }

        return joiner.toString();
    }

}
