package io.gcat.parser;

import io.gcat.entity.JVMParameter;

import java.util.Objects;

/**
 * parsers factory
 */
public class Parsers {
    private Parsers() {
    }

    public static Parser getParser(JVMParameter jvmParameter) {
        Objects.requireNonNull(jvmParameter);

        Boolean useParNewGC = jvmParameter.is("UseParNewGC");
        Boolean useConcMarkSweepGC = jvmParameter.is("UseConcMarkSweepGC");

        if (useParNewGC && useConcMarkSweepGC) {
            return new CMSParNewParser(jvmParameter);
        } else {
            throw new IllegalStateException("can not found match parser in flags!");
        }
    }
}
