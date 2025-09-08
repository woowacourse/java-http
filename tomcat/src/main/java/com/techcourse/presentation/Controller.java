package com.techcourse.presentation;

public interface Controller {

    boolean isResponsible(final String path);

    ResponseWithType getResource(final ParsedResourcePath request);

}
