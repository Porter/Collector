package com.porter.collector.parser;

import com.porter.collector.values.MyFloat;
import com.porter.collector.values.MyInteger;
import com.porter.collector.values.MyString;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NotThreadSafe
public class Parser {

    private int offset;
    private String toParse;

    public FunctionTree parse(String formula) {
        offset = 0;
        toParse = formula;
        return parseFunction();
    }

    private FunctionTree parseFunction() {
        Token token = nextToken();
        if (token == null) { return null; }
        if (!Token.Type.FUNCTION.equals(token.getType())) {
            throw new IllegalStateException("Unexpected token: " + token);
        }
        return _parseFunction(token);
    }

    private FunctionTree _parseFunction(Token start) {
        List<FunctionTree> args = parseList();
        return new FunctionTree(start.getFunction(), args);
    }

    private List<FunctionTree> parseList() {
        List<FunctionTree> args = new ArrayList<>();

        Token token;
        do {
            token = nextToken();
            if (Token.Type.FUNCTION.equals(token.getType())) {
                args.add(_parseFunction(token));
            }
            else if (Token.Type.INT.equals(token.getType())) {
                args.add(new FunctionTree(
                        new Constant(new MyInteger(token.getIntVal())),
                        Collections.emptyList()

                ));
            }
            else if (Token.Type.FLOAT.equals(token.getType())) {
                args.add(new FunctionTree(
                        new Constant(new MyFloat(token.getFloatVal())),
                        Collections.emptyList()

                ));
            }
            else if (Token.Type.STRING.equals(token.getType())) {
                args.add(new FunctionTree(
                        new Constant(new MyString(token.getStringVal())),
                        Collections.emptyList()
                ));
            }
            else if (Token.Type.COMMA.equals(token.getType()) || Token.Type.RIGHT_PAREN.equals(token.getType())) {
                // continue on
            }
            else {
                throw new IllegalStateException("Unexpected token: " + token);
            }
        } while (!Token.Type.RIGHT_PAREN.equals(token.getType()));

        return args;
    }

    private boolean isNumeric(char c) {
        return Character.isDigit(c) || c == '.';
    }

    private Token nextToken() {
        while (offset < toParse.length() && toParse.charAt(offset) == ' ') {
            offset++;
        }
        if (offset >= toParse.length()) { return null; }

        if (toParse.charAt(offset) == ',') {
            offset++;
            return new Token(Token.Type.COMMA);
        }

        if (toParse.charAt(offset) == ')') {
            offset++;
            return new Token(Token.Type.RIGHT_PAREN);
        }

        if (isNumeric(toParse.charAt(offset))) {
            StringBuilder stringBuilder = new StringBuilder();
            while (isNumeric(toParse.charAt(offset))) {
                stringBuilder.append(toParse.charAt(offset));
                offset++;
            }
            String val = stringBuilder.toString();
            if (val.contains(".")) { return new Token(Float.parseFloat(val)); }
            return new Token(Integer.parseInt(val));
        }

        if (toParse.charAt(offset) == '"') {
            StringBuilder stringBuilder = new StringBuilder();

            offset++;
            while (toParse.charAt(offset) != '"') {
                stringBuilder.append(toParse.charAt(offset));
                offset++;
            }
            offset++;

            String val = stringBuilder.toString();
            return new Token(val);
        }

        for (String token : Tokens.getTokens().keySet()) {
            if (toParse.toLowerCase().startsWith(token)) {
                offset += token.length();
                return new Token(Tokens.getTokens().get(token));
            }
        }

        return null;
    }

    public boolean isValid(String formula) {
        try {
            return parse(formula) != null;
        } catch (Exception e) {
            return false;
        }
    }
}
