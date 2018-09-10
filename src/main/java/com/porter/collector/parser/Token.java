package com.porter.collector.parser;

public class Token {



    public enum Type {
        FUNCTION, COMMA, RIGHT_PAREN, FLOAT, INT, STRING
    }
    private final Type type;

    private final Function function;
    private final int intVal;
    private final float floatVal;
    private final String stringVal;

    public Token(Type type) {
        this(type, null, 0, 0, null);
    }

    public Token(Function function) {
        this(Type.FUNCTION, function, 0, 0, null);
    }

    public Token(int val) {
        this(Type.INT, null, val, 0, null);
    }

    public Token(String val) {
        this(Type.STRING, null, 0, 0, val);
    }

    public Token(float val) {
        this(Type.FLOAT, null, 0, val, null);
    }

    private Token(Type type, Function function, int intVal, float floatVal, String stringVal) {
        this.type = type;
        this.function = function;
        this.intVal = intVal;
        this.floatVal = floatVal;
        this.stringVal = stringVal;
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

    public String getStringVal() {
        return stringVal;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", function=" + function +
                ", intVal=" + intVal +
                ", floatVal=" + floatVal +
                ", stringVal='" + stringVal + '\'' +
                '}';
    }
}
