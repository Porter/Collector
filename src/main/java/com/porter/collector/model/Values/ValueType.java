package com.porter.collector.model.Values;

public interface ValueType<E extends Addable<E>> {
    E parse(String value) throws Exception;

    String stringify();

    default boolean isValid(String value) {
        try {
            parse(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
