package kokodak.handler;

import java.io.IOException;
import kokodak.http.HttpRequest;
import kokodak.http.HttpResponse;

public interface Handler {

    HttpResponse handle(final HttpRequest httpRequest) throws IOException;
}
