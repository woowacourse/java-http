package kokodak.handler;

import kokodak.http.HttpRequest;

public interface ArgumentResolver {

    Object resolve(final HttpRequest httpRequest);
}
