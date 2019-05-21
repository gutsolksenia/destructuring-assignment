package com.gutsol.da;

import com.gutsol.da.parser.JavaScriptParser.VariableStatementContext;

public class MergableStatements {
    private final VariableStatementContext startingPoint;
    private final VariableStatementContext endPoint;
    private final String text;

    public MergableStatements(VariableStatementContext startingPoint, VariableStatementContext endPoint, String text) {
        this.startingPoint = startingPoint;
        this.endPoint = endPoint;
        this.text = text;
    }

    public VariableStatementContext getStartingPoint() {
        return startingPoint;
    }

    public VariableStatementContext getEndPoint() {
        return endPoint;
    }

    public String getText() {
        return text;
    }
}
