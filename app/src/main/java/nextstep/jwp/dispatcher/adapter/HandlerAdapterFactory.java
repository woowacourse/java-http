package nextstep.jwp.dispatcher.adapter;

import java.util.List;

public class HandlerAdapterFactory {

    private HandlerAdapterFactory() {
    }

    public static HandlerAdapter fileAccessHandlerAdapter() {
        return new FileAccessHandlerAdapter();
    }

    public static HandlerAdapter httpRequestHandlerAdapter() {
        return new HttpRequestHandlerAdapter();
    }

    public static List<HandlerAdapter> createAllHandlerAdapter() {
        return List.of(fileAccessHandlerAdapter(), httpRequestHandlerAdapter());
    }
}
