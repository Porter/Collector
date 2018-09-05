package com.porter.collector.parser;

public class Token {



    public enum Type {
        FUNCTION, COMMA, LEFT_PAREN, FLOAT, INT;
    }
    private final Type type;

    private final Function function;
    private final int intVal;
    private final float floatVal;
//    private final
    public Token(Type type) {
        this(type, null, 0, 0);
    }

    public Token(Function function) {
        this(Type.FUNCTION, function, 0, 0);
    }

    public Token(int val) {
        this(Type.INT, null, val, 0);
    }

    public Token(float val) {
        this(Type.FLOAT, null, 0, val);
    }

    private Token(Type type, Function function, int intVal, float floatVal) {
        this.type = type;
        this.function = function;
        this.intVal = intVal;
        this.floatVal = floatVal;
    }

    public Type getType() {
        return type;
    }

    public Function getFunction() {
        return function;
    }

    public int getIntVal() {
        return intVal;
    }

    public float getFloatVal() {
        return floatVal;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", function=" + function +
                ", intVal=" + intVal +
                ", floatVal=" + floatVal +
                '}';
    }
}
