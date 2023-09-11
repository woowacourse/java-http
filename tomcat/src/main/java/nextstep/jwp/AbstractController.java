package nextstep.jwp;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class AbstractController implements Controller {

    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
    }

    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
    }


    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        // http method 분기문
    }
}
