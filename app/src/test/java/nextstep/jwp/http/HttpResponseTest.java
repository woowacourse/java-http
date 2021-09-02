package nextstep.jwp.http;

import nextstep.jwp.http.entity.HttpStatus;
import org.junit.jupiter.api.Test;

class HttpResponseTest {
    @Test
    void name() {
        HttpResponse httpResponse = HttpResponse.empty();
        httpResponse.setHttpStatus(HttpStatus.FOUND);
        httpResponse.setLocation("/index.html");
    }
}
