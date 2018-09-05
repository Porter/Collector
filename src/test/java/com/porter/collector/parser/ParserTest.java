package com.porter.collector.parser;

import com.google.common.collect.ImmutableList;
import com.porter.collector.model.Values.MyFloat;
import com.porter.collector.model.Values.MyInteger;
import org.junit.Test;

import static org.junit.Assert.*;

public class ParserTest {

    @Test
    public void testBasic() throws Exception {
        Parser p = new Parser();
        FunctionTree tree = p.parse("SUM(1, 2)");

        FunctionTree expected = new FunctionTree(new Sum(), ImmutableList.of(
                new FunctionTree(new Constant(new MyInteger(1)), ImmutableList.of()),
                new FunctionTree(new Constant(new MyInteger(2)), ImmutableList.of())
        ));

        assertEquals(expected, tree);
    }

    @Test
    public void testBasic2() throws Exception {
        FunctionTree tree = new FunctionTree(new Sum(), ImmutableList.of(
                new FunctionTree(new Constant(new MyInteger(1)), ImmutableList.of()),
                new FunctionTree(new Constant(new MyInteger(2)), ImmutableList.of())
        ));

        assertEquals(new MyInteger(3), tree.execute());
    }

    @Test
    public void testEmbedded() throws Exception {
        Parser p = new Parser();
        FunctionTree tree = p.parse("SUM(sUm(1, 4), 2)");

        FunctionTree expected = new FunctionTree(new Sum(), ImmutableList.of(
                new FunctionTree(new Sum(), ImmutableList.of(
                        new FunctionTree(new Constant(new MyInteger(1)), ImmutableList.of()),
                        new FunctionTree(new Constant(new MyInteger(4)), ImmutableList.of())
                )
                ),
                new FunctionTree(new Constant(new MyInteger(2)), ImmutableList.of())
        ));

        assertEquals(expected, tree);
    }

    @Test
    public void testEmbedded2() throws Exception {
        FunctionTree tree = new FunctionTree(new Sum(), ImmutableList.of(
                new FunctionTree(new Sum(), ImmutableList.of(
                        new FunctionTree(new Constant(new MyInteger(1)), ImmutableList.of()),
                        new FunctionTree(new Constant(new MyInteger(4)), ImmutableList.of())
                )
                ),
                new FunctionTree(new Constant(new MyInteger(2)), ImmutableList.of())
        ));

        assertEquals(new MyInteger(7), tree.execute());
    }

    @Test
    public void testFloat() throws Exception {
        Parser p = new Parser();
        FunctionTree tree = p.parse("SUM(sUm(1, 4.2), 2)");

        FunctionTree expected = new FunctionTree(new Sum(), ImmutableList.of(
                new FunctionTree(new Sum(), ImmutableList.of(
                        new FunctionTree(new Constant(new MyInteger(1)), ImmutableList.of()),
                        new FunctionTree(new Constant(new MyFloat(4.2f)), ImmutableList.of())
                )
                ),
                new FunctionTree(new Constant(new MyInteger(2)), ImmutableList.of())
        ));

        assertEquals(expected, tree);
    }
}