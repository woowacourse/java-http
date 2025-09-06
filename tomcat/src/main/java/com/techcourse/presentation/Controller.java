package com.techcourse.presentation;

public interface Controller {

    ResponseWithType getResource(final ParsedResourcePath request);

}
