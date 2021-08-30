package nextstep.jwp.adapter;

import java.util.ArrayList;
import java.util.List;

public class HandlerAdapters {
    private final List<HandlerAdapter> adapters = new ArrayList<>();

    public HandlerAdapters() {
        initAdapters();
    }

    private void initAdapters() {
        adapters.add(new MainControllerHandlerAdapter());
        adapters.add(new DefaultControllerHandlerAdapter());
        adapters.add(new LoginControllerHandlerAdapter());
        adapters.add(new RegisterControllerHandlerAdapter());
    }

    public HandlerAdapter getHandlerAdapter(Object handler) {
        for (HandlerAdapter adapter : adapters) {
            if (adapter.supports(handler)) {
                return adapter;
            }
        }
        throw new IllegalArgumentException("handler adapter not found!");
    }
}
