package com.porter.collector.util;

import org.apache.commons.validator.routines.EmailValidator;

public class Email {

    public static Boolean isValidAddress(String email) {
        return EmailValidator.getInstance().isValid(email);
    }
}
