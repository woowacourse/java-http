package org.apache.coyote.controller;

import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;

public interface Controller {

	void service(HttpRequest request, HttpResponse response) throws Exception;

	void doGet(HttpRequest request, HttpResponse response) throws Exception;

	void doPost(HttpRequest request, HttpResponse response) throws Exception;
}
