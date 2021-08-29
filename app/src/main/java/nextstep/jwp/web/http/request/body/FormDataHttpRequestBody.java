package nextstep.jwp.web.http.request.body;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import nextstep.jwp.web.http.util.QueryParser;

public class FormDataHttpRequestBody implements HttpRequestBody {

    private final HashMap<String, String> formData;
    private final String body;

    public FormDataHttpRequestBody(String body) {
        this.formData = parseBody(body);
        this.body = body;
    }

    @Override
    public Object getAttribute(String key) {
        return formData.get(key);
    }

    @Override
    public String getBody() {
        return body;
    }

    private HashMap<String, String> parseBody(String body) {
        String decode = URLDecoder.decode(body, StandardCharsets.UTF_8);
        QueryParser queryParser = new QueryParser(decode);
        return new HashMap<>(queryParser.queryParams().map());
    }
}
