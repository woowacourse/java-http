package jakarta.servlet;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public interface Controller {

	boolean canHandle(HttpRequest request);

	void service(HttpRequest request, HttpResponse response) throws Exception;
}
