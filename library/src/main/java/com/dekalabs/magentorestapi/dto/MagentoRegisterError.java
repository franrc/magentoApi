package com.dekalabs.magentorestapi.dto;

import java.util.List;
import java.util.Map;

public class MagentoRegisterError {

    private String message;

    private List<String> parameters;

//    public String getMessage() {
//        return message;
//    }

    public void setMessage(String message) {
        this.message = message;
    }

//    public Map<String, String> getParameters() {
//        return parameters;
//    }


    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    public String getError() {
        if(message == null || message.isEmpty()) return "";

        if(parameters == null) return message;

        for(int i = 1; i <= parameters.size(); i++) {
            message = message.replace("%"+i, parameters.get(i-1));
        }

        return message;
    }
}