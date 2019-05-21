package com.gutsol.da;

import com.gutsol.da.parser.JavaScriptParser;
import com.gutsol.da.parser.JavaScriptParser.VariableStatementContext;

public class VariableStatement {
    private final String identifier;
    private final int index;
    private final String arrayName;
    private final VariableStatementContext underlying;

    public VariableStatement(VariableStatementContext ctx) {
        this.identifier = findIdentifier(ctx);
        this.index = findIndex(ctx);
        this.arrayName = findArrayName(ctx);
        this.underlying = ctx;
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getIndex() {
        return index;
    }

    public String getArrayName() {
        return arrayName;
    }

    private static String findIdentifier(VariableStatementContext ctx) {
        return ctx.variableDeclarationList().variableDeclaration(0).Identifier().getText();
    }

    private static int findIndex(VariableStatementContext ctx) {
        JavaScriptParser.SingleExpressionContext expressionContext =
                ctx.variableDeclarationList().variableDeclaration(0).singleExpression();
        JavaScriptParser.MemberIndexExpressionContext indexContext = (JavaScriptParser.MemberIndexExpressionContext) expressionContext;
        return Integer.valueOf(indexContext.expressionSequence().getText());
    }

    private static String findArrayName(VariableStatementContext ctx) {
        JavaScriptParser.SingleExpressionContext expressionContext =
                ctx.variableDeclarationList().variableDeclaration(0).singleExpression();
        JavaScriptParser.MemberIndexExpressionContext indexContext = (JavaScriptParser.MemberIndexExpressionContext) expressionContext;
        return indexContext.singleExpression().getText();
    }

    public VariableStatementContext getUnderlying() {
        return underlying;
    }
}
