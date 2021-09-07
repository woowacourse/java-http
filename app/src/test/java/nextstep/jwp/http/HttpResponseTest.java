package nextstep.jwp.http;

import nextstep.jwp.http.entity.HttpStatus;
import org.junit.jupiter.api.Test;

class HttpResponseTest {
    @Test
    void name() {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setHttpStatus(HttpStatus.FOUND);
        httpResponse.setLocation("/index.html");
    }
}
