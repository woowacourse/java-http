package nextstep;

import com.google.common.base.Splitter;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.http.HttpHeader;
import nextstep.jwp.http.Protocol;
import nextstep.jwp.http.request.HttpMethod;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.QueryStringTypeRequestBody;
import nextstep.jwp.http.request.QueryStrings;
import nextstep.jwp.http.request.URI;

public class TestUtil {

    public static HttpRequest createRequest(String startLine, Map<String, String> requestBody) {
        String[] elements = startLine.split(" ");

        HttpMethod httpMethod = HttpMethod.of(elements[0]);

        Protocol protocol = new Protocol(elements[2]);

        String[] uriValues = elements[1].split("\\?", 2);
        QueryStrings queryStrings = new QueryStrings();
        if (uriValues.length == 2) {
            queryStrings = new QueryStrings(
                Splitter.on("&")
                    .withKeyValueSeparator("=")
                    .split(uriValues[1])
            );
        }
        URI uri = new URI(uriValues[0], queryStrings);

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Host", "localhost:8080");
        headerMap.put("Connection", "keep-alive");
        headerMap.put("Accept", "*/*");

        HttpHeader headers = new HttpHeader(headerMap);

        return new HttpRequest(httpMethod, uri, protocol, headers, new QueryStringTypeRequestBody(requestBody));
    }

    public static HttpRequest createRequest(String startLine) {
        return createRequest(startLine, new HashMap<>());
    }

}
