package org.apache.coyote.http11.handler.controllerResponse;

public record StaticFileResponse(String status, String content) implements ControllerResponse {

}
