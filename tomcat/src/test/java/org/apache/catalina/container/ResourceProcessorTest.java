package org.apache.catalina.container;

import jakarta.controller.Controller;
import jakarta.controller.ResourceFinder;
import jakarta.controller.StaticResourceController;
import jakarta.http.Header;
import jakarta.http.HttpBody;
import jakarta.http.HttpRequest;
import jakarta.http.HttpSessionWrapper;
import jakarta.http.HttpVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ResourceProcessorTest {

    @Test
    @DisplayName("정적 데이터를 처리한다.")
    void processResponse() throws Exception {
        StaticResourceController resourceController = mock(StaticResourceController.class);
        ResourceFinder resourceFinder = new ResourceFinder();
        Controller controller = mock(Controller.class);
        Controller defaultController = mock(Controller.class);
        HashMap<String, Controller> map = new HashMap<>();
        map.put("/login", controller);
        RequestMapping requestMapping = new RequestMapping(map, defaultController);
        ResourceProcessor resourceProcessor = new ResourceProcessor(requestMapping, resourceFinder, resourceController);

        resourceProcessor.processResponse(createHttpRequest("GET /sample.txt HTTP/1.1"));

        verify(resourceController, atLeastOnce()).service(any(), any());
    }

    @Test
    @DisplayName("동적 데이터를 처리한다.")
    void processDynamic() throws Exception {
        StaticResourceController resourceController = mock(StaticResourceController.class);
        ResourceFinder resourceFinder = mock(ResourceFinder.class);
        Controller controller = mock(Controller.class);
        Controller defaultController = mock(Controller.class);
        HashMap<String, Controller> map = new HashMap<>();
        map.put("/login", controller);
        RequestMapping requestMapping = new RequestMapping(map, defaultController);
        ResourceProcessor resourceProcessor = new ResourceProcessor(requestMapping, resourceFinder, resourceController);

        resourceProcessor.processResponse(createHttpRequest("GET /login HTTP/1.1"));

        verify(controller, atLeastOnce()).service(any(), any());
    }

    @Test
    @DisplayName("아무런 처리도 불가능하면 기본 컨트롤러로 데이터를 처리한다.")
    void processDefault() throws Exception {
        StaticResourceController resourceController = mock(StaticResourceController.class);
        ResourceFinder resourceFinder = mock(ResourceFinder.class);
        Controller controller = mock(Controller.class);
        Controller defaultController = mock(Controller.class);
        HashMap<String, Controller> map = new HashMap<>();
        map.put("/login", controller);
        RequestMapping requestMapping = new RequestMapping(map, defaultController);
        ResourceProcessor resourceProcessor = new ResourceProcessor(requestMapping, resourceFinder, resourceController);

        resourceProcessor.processResponse(createHttpRequest("GET /unknown HTTP/1.1"));

        verify(defaultController, atLeastOnce()).service(any(), any());
    }

    private HttpRequest createHttpRequest(String requestLine) {
        return HttpRequest.createHttpRequest(requestLine,
                Header.empty(),
                mock(HttpBody.class),
                HttpVersion.HTTP_1_1,
                mock(HttpSessionWrapper.class)
        );
    }
}
