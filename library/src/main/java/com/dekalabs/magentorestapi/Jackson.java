package com.dekalabs.magentorestapi;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by dekalabs
 */
public class Jackson {


    public static class YesNoBooleanDeserializer extends JsonDeserializer<Boolean> {

        protected static final String NO = "no";
        protected static final String YES = "yes";

        @Override
        public Boolean deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            JsonToken currentToken = jp.getCurrentToken();

            if (currentToken.equals(JsonToken.VALUE_STRING)) {
                String text = jp.getText().trim();

                if (YES.equalsIgnoreCase(text) || "1".equals(text)) {
                    return Boolean.TRUE;
                } else if (NO.equalsIgnoreCase(text) || "0".equals(text)) {
                    return Boolean.FALSE;
                }

                throw ctxt.weirdStringException(text, Boolean.class,
                        "Only \"" + YES + "\" or \"" + NO + "\" values supported");
            } else if(currentToken.equals(JsonToken.VALUE_NUMBER_INT)) {
                int value = Integer.valueOf(jp.getText());
                return value == 1 ? Boolean.TRUE : Boolean.FALSE;
            }
            else if(currentToken.equals(JsonToken.VALUE_FALSE)) {
                return Boolean.FALSE;
            }
            else if(currentToken.equals(JsonToken.VALUE_TRUE)) {
                return Boolean.TRUE;
            }
            else if (currentToken.equals(JsonToken.VALUE_NULL)) {
                return Boolean.FALSE;
            }

            ctxt.handleUnexpectedToken(Boolean.class, jp);

            return null;
        }
    }


    public static ObjectMapper DEFAULT_MAPPER = new ObjectMapper()
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()))
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
            .registerModule(new SimpleModule()
                    .addDeserializer(Boolean.class, new YesNoBooleanDeserializer()));

    public static ObjectMapper EMPTY_MAPPER = new ObjectMapper()
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
}
