package com.porter.collector.parser;

import com.porter.collector.values.ValueType;

public interface Function {

    ValueType exec(Args args);
}
