package org.apache.coyote.routing;

@FunctionalInterface
public interface RouteInfo {
    RouteKey getRouteKey();
}
