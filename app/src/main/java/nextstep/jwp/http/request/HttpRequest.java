package nextstep.jwp.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * GET /index.html HTTP/1.1        // Request Line
 * Host: localhost:8000           // Request Headers Connection:
 * keep-alive Upgrade-Insecure-Request: 1
 * Content-Type: text/html
 * Content-Length: 345
 *
 * something1=123&something2=123   // Request Message Body
 */

/**
 * GET /login?something1=123&something2=123  HTTP/1.1        // Request Line
 * Host: localhost:8000                                      // Request Headers Connection:
 * keep-alive Upgrade-Insecure-Request: 1
 * Content-Type: text/html
 * Content-Length: 345
 */

public class HttpRequest {

    private final RequestLine requestLine;
    private final List<String> requestHeaders;
    private final List<String> requestBody;

    public HttpRequest(RequestLine requestLine, List<String> requestHeaders, List<String> requestBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    public HttpRequest(RequestLine requestLine, List<String> requestHeaders) {
        this(requestLine, requestHeaders, Collections.emptyList());
    }

    public static HttpRequest of(InputStream inputStream) throws IOException {
        List<String> lines = readLines(inputStream);

        RequestLine requestLine = RequestLine.of(lines.get(0));
        List<String> headers = findHeaders(lines);
        List<String> requestBody = findBody(lines);

        return new HttpRequest(requestLine, headers, requestBody);
    }

    private static List<String> readLines(InputStream inputStream) throws IOException {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        final List<String> lines = new ArrayList<>();
        String tempLine;
        while (!(tempLine = bufferedReader.readLine()).equals("")) {
            lines.add(tempLine);
        }

        System.out.println("======LOG======");
        System.out.println(String.join("\n", lines));

        return new ArrayList<>(lines);
    }

    private static List<String> findHeaders(List<String> lines){
        List<String> headers = new ArrayList<>();
        for(String line : lines.subList(1, lines.size())){
            if(line.isEmpty()){
                break;
            }
            headers.add(line);
        }
        return headers;
    }

    private static List<String> findBody(List<String> lines){
        int index =0;
        while(!lines.get(index).isEmpty()){
            index++;
        }
        return lines.subList(index+1, lines.size());
    }

//    private static void a(){
//        int contentLength = Integer.parseInt(httpRequestHeaders.get("Content-Length"));
//        char[] buffer = new char[contentLength];
//        reader.read(buffer, 0, contentLength);
//        String requestBody = new String(buffer);
//    }

    public RequestLine getRequestLine() {
        return requestLine;
    }
}
