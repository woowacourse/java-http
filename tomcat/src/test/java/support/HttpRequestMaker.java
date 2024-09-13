package support;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httprequest.HttpRequestConvertor;

public class HttpRequestMaker {

    public static HttpRequest makeHttpRequest(String httpRequest) {
        try {
            InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            return HttpRequestConvertor.convertHttpRequest(bufferedReader);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
