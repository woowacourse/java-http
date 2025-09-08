package org.apache.coyote.http11.service;

import org.apache.coyote.http11.HttpCookies;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.parser.RequestResult;

import java.io.IOException;
import java.util.Map;

public interface HttpService {

    RequestResult doGet(Map<String, String> query, HttpCookies cookies, Session session) throws IOException;

    RequestResult doPost(Map<String, String> query, HttpCookies cookies, Session session) throws IOException;
}
