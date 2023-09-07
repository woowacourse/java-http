package kokodak.handler;

import java.io.IOException;
import java.util.List;
import kokodak.http.HttpRequest;
import kokodak.http.HttpResponse;

public interface Handler {

    HttpResponse handle(final HttpRequest httpRequest) throws IOException;

    List<Class<? extends Argument>> requiredArguments();

    void setArguments(final List<Argument> arguments);
}
