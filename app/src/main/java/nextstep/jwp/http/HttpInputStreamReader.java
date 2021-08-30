package nextstep.jwp.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpInputStreamReader {

    private final BufferedReader bufferedReader;

    public HttpInputStreamReader(InputStream inputStream) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        this.bufferedReader = new BufferedReader(inputStreamReader);
    }

    public HttpRequest readHttpRequest() throws IOException {
        Map<String, String> httpRequestHeaders = new HashMap<>();

        String startLine = bufferedReader.readLine();
        String[] splitStartLine = startLine.split(" ");

        String httpMethod = splitStartLine[0];
        String uri = splitStartLine[1];
        String httpVersion = splitStartLine[2];

        while (bufferedReader.ready()) {
            String line = bufferedReader.readLine();
            if (line.length() == 0) {
                break;
            }

            String[] splitHeaderLine = line.split(": ");
            httpRequestHeaders.put(splitHeaderLine[0], splitHeaderLine[1]);
        }

        if (!httpRequestHeaders.containsKey("Content-Length")) {
            return new HttpRequest(httpMethod, uri, httpVersion, httpRequestHeaders, parseParameters(uri), "");
        }

        int contentLength = Integer.parseInt(httpRequestHeaders.get("Content-Length"));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);

        return new HttpRequest(httpMethod, uri, httpVersion, httpRequestHeaders, parseParameters(uri), requestBody);
    }

    private Map<String, String> parseParameters(String uri) {
        int index = uri.indexOf("?");
        if (index == -1) {
            return new HashMap<>();
        }
        String queryString = uri.substring(index + 1);
        List<String> split = Arrays.asList(queryString.split("&"));
        Map<String, String> params = new HashMap<>();

        split.stream().forEach(it -> {
            String[] split1 = it.split("=");
            params.put(split1[0], split1[1]);
        });

        return params;
    }
}
