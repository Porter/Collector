package com.porter.collector.values;

public interface ValueType<E extends ValueType> {
    E parse(String value) throws Exception;

    E combine(E other);

    String stringify();

    E zero();

    default boolean isValid(String value) {
        try {
            return parse(value) != null;
        } catch (Exception e) {
            return false;
        }
    }
}
