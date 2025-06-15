package com.example.pcbuilderguideapp.utils;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US);

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            String dateStr = json.getAsString();
            if (dateStr == null || dateStr.isEmpty()) {
                return null;
            }
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            throw new JsonParseException("Failed to parse date: " + json.getAsString(), e);
        }
    }

    @Override
    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(dateFormat.format(src));
    }
} 