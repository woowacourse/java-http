package nextstep;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import nextstep.jwp.HttpRequest;

public class HttpInputStreamReader {

    private BufferedReader bufferedReader;

    public HttpInputStreamReader(InputStream inputStream) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        this.bufferedReader = new BufferedReader(inputStreamReader);
    }

    public HttpRequest readHttpRequest() throws IOException {
        List<String> httpRequestData = new LinkedList<>();

        while (bufferedReader.ready()) {
            httpRequestData.add(bufferedReader.readLine());
        }

        return parseHttpRequest(httpRequestData);
    }

    private HttpRequest parseHttpRequest(List<String> httpRequestData) {
        if(httpRequestData.isEmpty())
            return null;
        String startLine = httpRequestData.get(0);
        String[] splitStartLine = startLine.split(" ");

        String httpMethod = splitStartLine[0];
        String uri = splitStartLine[1];
        String httpVersion = splitStartLine[2];

        return new HttpRequest(httpMethod, uri, httpVersion, parseParameters(uri));
    }

    private Map<String, String> parseParameters(String uri){
        int index = uri.indexOf("?");
        if(index == -1){
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
