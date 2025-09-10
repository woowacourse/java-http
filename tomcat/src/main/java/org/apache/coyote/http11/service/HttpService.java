package org.apache.coyote.http11.service;

import org.apache.coyote.http11.HttpRequests;
import org.apache.coyote.http11.parser.HttpResponse;

public interface HttpService {

    void doGet(HttpRequests httpRequests, HttpResponse httpResponse);

    void doPost(HttpRequests httpRequests, HttpResponse httpResponse);

    void doUpdate(HttpRequests httpRequests, HttpResponse httpResponse);

    void doDelete(HttpRequests httpRequests, HttpResponse httpResponse);
}
