package com.porter.collector.model.Values;

public interface ValueType<E extends ValueType> {
    E parse(String value) throws Exception;

    String stringify();

    default boolean isValid(String value) {
        try {
            return parse(value) != null;
        } catch (Exception e) {
            return false;
        }
    }
}
