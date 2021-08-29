package nextstep;

import com.google.common.base.Splitter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.http.HttpMethod;
import nextstep.jwp.http.HttpHeader;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.Protocol;
import nextstep.jwp.http.QueryStrings;
import nextstep.jwp.http.URI;

public class TestUtil {

    public static HttpRequest createRequest(String startLine) throws IOException {
        String requestMessage = startLine + System.lineSeparator() +
            "Host: localhost:8080" + System.lineSeparator() +
            "Connection: keep-alive" + System.lineSeparator() +
            "Accept: */*";

        String[] elements = startLine.split(" ");

        HttpMethod httpMethod = HttpMethod.of(elements[0]);

        Protocol protocol = new Protocol(elements[2]);

        String[] uriValues = elements[1].split("\\?", 2);
        QueryStrings queryStrings = null;
        if (uriValues.length == 2) {
            queryStrings =  new QueryStrings(
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

        return new HttpRequest(httpMethod, uri, protocol, headers);
    }

}
