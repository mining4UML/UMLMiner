package com.uniba.mining.processfeedback.core;

public interface DomainViolationRule {

    boolean matches(ProcessViolation violation);

    InterpretedViolation interpret(ProcessViolation violation);

}