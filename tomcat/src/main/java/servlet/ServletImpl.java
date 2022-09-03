package servlet;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseMapper;

public class ServletImpl implements Servlet {

    private static final HttpResponseMapper MAPPER = new HttpResponseMapper();

    @Override
    public String doService(String input) {
        HttpRequest request = new HttpRequest(input);
        HttpResponse response = MAPPER.resolveException(request);
        return response.getResponse();
    }
}
