package support;

import org.apache.coyote.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.util.concurrent.atomic.AtomicInteger;

public class TestController extends AbstractController {

    AtomicInteger count =  new AtomicInteger(0);

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        try {
            Thread.sleep(8000);
            count.incrementAndGet();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
