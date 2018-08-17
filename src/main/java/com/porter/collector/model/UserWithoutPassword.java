package com.porter.collector.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.mindrot.jbcrypt.BCrypt;

@Value.Immutable
@JsonSerialize(as = ImmutableUserWithoutPassword.class)
@JsonDeserialize(as = ImmutableUserWithoutPassword.class)
public abstract class UserWithoutPassword extends SimpleUser { }
