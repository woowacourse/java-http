package handler;

public interface RequestHandler {

    Controller getHandler(final String requestUri);
}
