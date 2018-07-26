package com.dekalabs.magentorestapi.dto;

import com.dekalabs.magentorestapi.utils.ServiceUtils;

import java.util.Map;

public class MagentoError {

    private String message;

    private Map<String, String> parameters;

//    public String getMessage() {
//        return message;
//    }

    public void setMessage(String message) {
        this.message = message;
    }

//    public Map<String, String> getParameters() {
//        return parameters;
//    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getError() {
        if(message == null || message.isEmpty()) return "";

        if(parameters == null) return message;

        for(String param : parameters.keySet()) {
            if(message.contains(("%" + param))) {
                message = message.replaceFirst("%" + param, parameters.get(param));
            }
        }

        return message;
    }
}