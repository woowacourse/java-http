package com.techcourse.controller;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.exception.HttpException;

final class FormDataParser {

    private FormDataParser() {
    }

    static Map<String, String> parse(final String requestBody) {
        final Map<String, String> formData = new HashMap<>();
        final String[] formFields = requestBody.split("&");

        for (String formField : formFields) {
            addFormField(formData, formField);
        }
        return formData;
    }

    private static void addFormField(final Map<String, String> formData, final String formField) {
        final String[] keyValue = formField.split("=", 2);
        if (keyValue.length < 2) {
            return;
        }
        try {
            formData.put(keyValue[0], URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8));
        } catch (IllegalArgumentException e) {
            throw new HttpException(HttpException.Status.BAD_REQUEST, "잘못된 폼 데이터입니다.", e);
        }
    }
}
