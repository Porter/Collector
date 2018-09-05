package com.porter.collector.parser;

import com.porter.collector.model.Values.ValueType;

public interface Function {

    ValueType exec(Args args);
}
