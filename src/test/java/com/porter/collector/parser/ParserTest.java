package com.porter.collector.parser;

import com.google.common.collect.ImmutableList;
import com.porter.collector.db.SourceDao;
import com.porter.collector.db.ValueDao;
import com.porter.collector.helper.BaseTest;
import com.porter.collector.model.ValuesMapper;
import com.porter.collector.util.ValueValidator;
import com.porter.collector.values.MyFloat;
import com.porter.collector.values.MyInteger;
import com.porter.collector.values.MyString;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ParserTest extends BaseTest {

    private void setUp() {
        SourceDao sourceDao = getJdbi().onDemand(SourceDao.class);

        ValueValidator validator = new ValueValidator(Jackson.newObjectMapper());
        ValueDao valueDao = new ValueDao(getJdbi(), validator, new ValuesMapper());
        Tokens.register("source", new SourceAccessor(sourceDao, valueDao));
    }

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

        assertEquals(new MyInteger(3), tree.execute(null));
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

        assertEquals(new MyInteger(7), tree.execute(null));
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

    @Test
    public void testSource() throws Exception {
        setUp();
        Parser p = new Parser();

        FunctionTree expected = new FunctionTree(new SourceAccessor(null, null), ImmutableList.of(
                new FunctionTree(new Constant(new MyString("sourceName")), ImmutableList.of())
        ));

        FunctionTree actual = p.parse("Source(\"sourceName\")");

        assertEquals(expected, actual);
    }
}