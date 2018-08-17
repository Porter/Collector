package com.porter.collector.model;

import org.immutables.value.Value;

import java.security.Principal;


public abstract class SimpleUser implements Principal {
    public abstract long id();
    public abstract String userName();
    public abstract String email();

    @Override
    public String getName() {
        return "";
    }
}
