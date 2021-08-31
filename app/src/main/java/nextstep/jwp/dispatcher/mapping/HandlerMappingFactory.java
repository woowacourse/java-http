package nextstep.jwp.dispatcher.mapping;

import java.util.List;

public class HandlerMappingFactory {

    private HandlerMappingFactory() {
    }

    public static HandlerMapping fileAccessHandlerMapping() {
        return new FileAccessHandlerMapping();
    }

    public static HandlerMapping httpRequestHandlerMapping() {
        return new HttpRequestHandlerMapping();
    }

    public static List<HandlerMapping> createAllHandlerMapping() {
        return List.of(fileAccessHandlerMapping(), httpRequestHandlerMapping());
    }
}
