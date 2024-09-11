package org.apache.catalina.controller;

import static java.util.stream.Collectors.toMap;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;
import org.apache.catalina.exception.TomcatException;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    private final Map<HttpEndpoint, Controller> endpointControllers;

    public RequestMapping(Map<HttpEndpoint, Controller> endpointControllers) {
        this.endpointControllers = endpointControllers;
    }

    public static RequestMapping of(AbstractController... controllers) {
        return RequestMapping.of(Arrays.stream(controllers).toList());
    }

    public static RequestMapping of(List<AbstractController> controllers) {
        validateControllers(controllers);
        Map<HttpEndpoint, Controller> endpointControllers = controllers.stream()
                .flatMap(RequestMapping::mapToEndpointEntries)
                .collect(toMap(Entry::getKey, Entry::getValue));

        return new RequestMapping(endpointControllers);
    }

    private static Stream<SimpleEntry<HttpEndpoint, Controller>> mapToEndpointEntries(AbstractController controller) {
        return controller.getEndpoints()
                .stream()
                .map(endpoint -> new SimpleEntry<>(endpoint, controller));
    }

    private static void validateControllers(List<AbstractController> controllers) {
        List<HttpEndpoint> endpoints = controllers.stream()
                .map(AbstractController::getEndpoints)
                .flatMap(Collection::stream)
                .toList();
        validateEndpointsUnique(endpoints);
    }

    private static void validateEndpointsUnique(List<HttpEndpoint> endpoints) {
        Set<HttpEndpoint> uniqueEndpoints = new HashSet<>(endpoints);
        if (endpoints.size() != uniqueEndpoints.size()) {
            throw new TomcatException("HttpEndpoint 중복으로 인해 컨트롤러 등록에 실패하였습니다.");
        }
    }

    public Controller getController(HttpRequest request) {
        HttpEndpoint httpEndpoint = HttpEndpoint.of(request);
        Controller controller = endpointControllers.get(httpEndpoint);
        if (controller == null) {
            return new ResourceController();
        }
        return controller;
    }
}
