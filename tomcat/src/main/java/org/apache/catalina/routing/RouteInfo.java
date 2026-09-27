package org.apache.catalina.routing;

@FunctionalInterface
public interface RouteInfo {
    RouteKey getRouteKey();
}
