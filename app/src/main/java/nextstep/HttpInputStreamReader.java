package nextstep;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
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
        String startLine = httpRequestData.get(0);
        String[] splitStartLine = startLine.split(" ");

        String httpMethod = splitStartLine[0];
        String uri = splitStartLine[1];
        String httpVersion = splitStartLine[2];

        return new HttpRequest(httpMethod, uri, httpVersion);
    }
}
