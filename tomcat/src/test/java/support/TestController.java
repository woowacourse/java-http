package support;

import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class TestController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        try {
            Thread.sleep(8000);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
