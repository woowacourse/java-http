package kokodak;

import java.io.IOException;

public interface Handler {

    HttpResponse handle(final HttpRequest httpRequest) throws IOException;
}
