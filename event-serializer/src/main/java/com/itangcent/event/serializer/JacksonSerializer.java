package com.itangcent.event.serializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class JacksonSerializer implements Serializer {
    private ObjectMapper objectMapper;
    private JavaType javaType = TypeFactory.defaultInstance().constructType(Object.class);

    static final byte[] EMPTY_ARRAY = new byte[0];

    public JacksonSerializer() {
        objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
    }

    @SuppressWarnings("unchecked")
    public Object deserialize(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            return this.objectMapper.readValue(bytes, 0, bytes.length, javaType);
        } catch (Exception ex) {
            throw new SerializationException("Could not deserialize: " + ex.getMessage(), ex);
        }
    }

    @Override
    public byte[] serialize(Object t) {

        if (t == null) {
            return EMPTY_ARRAY;
        }
        try {
            return this.objectMapper.writeValueAsBytes(t);
        } catch (Exception ex) {
            throw new SerializationException("Could not serialize: " + ex.getMessage(), ex);
        }
    }
}
