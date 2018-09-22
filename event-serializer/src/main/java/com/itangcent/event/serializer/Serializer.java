package com.itangcent.event.serializer;

public interface Serializer {

    /**
     * Serialize the given object to binary data.
     *
     * @param bean object to serialize. Can be {@literal null}.
     * @return the equivalent binary data. Can be {@literal null}.
     */
    byte[] serialize(Object bean);

    /**
     * Deserialize an object from the given binary data.
     *
     * @param bytes object binary representation. Can be {@literal null}.
     * @return the equivalent object instance. Can be {@literal null}.
     */
    Object deserialize(byte[] bytes);
}
