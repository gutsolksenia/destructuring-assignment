package com.gutsol.da;

import com.gutsol.da.parser.DefaultJavaScriptParserListener;
import com.gutsol.da.parser.JavaScriptParser;
import com.gutsol.da.parser.JavaScriptParser.IdentifierExpressionContext;
import com.gutsol.da.parser.JavaScriptParser.MemberIndexExpressionContext;
import com.gutsol.da.parser.JavaScriptParser.SingleExpressionContext;
import com.gutsol.da.parser.JavaScriptParser.VariableStatementContext;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNumeric;

public class RepeatingAssignmentListener extends DefaultJavaScriptParserListener {
    private LastStatementType lastStatementType = LastStatementType.OTHER;
    private final List<VariableStatement> lastVariableStatements = new ArrayList<>();
    private final InlineDictionary inlineDictionary;

    public RepeatingAssignmentListener(CommonTokenStream tokenStream) {
        inlineDictionary = new InlineDictionary(tokenStream);
    }

    @Override
    public void exitStatement(JavaScriptParser.StatementContext ctx) {
        if (ctx.variableStatement() == null) {
            lastStatementType = LastStatementType.OTHER;
        }
    }

    @Override
    public void enterVariableStatement(VariableStatementContext ctx) {
        if (isVariableStatementHandled(ctx) && isVariableStatementArrayAssigned(ctx)) {
            VariableStatement statement = new VariableStatement(ctx);
            if (lastStatementType != LastStatementType.VARIABLE && statement.getIndex() == 0) {
                lastStatementType = LastStatementType.VARIABLE;
                lastVariableStatements.clear();
                lastVariableStatements.add(statement);
            } else if (canBeInlined(statement, lastVariableStatements)) {
                lastVariableStatements.add(statement);
            } else if (statement.getIndex() == 0) {
                if (lastVariableStatements.size() > 1) {
                    inlineDictionary.add(new ArrayList<>(lastVariableStatements));
                }
                lastVariableStatements.clear();
                lastVariableStatements.add(statement);
            }
        }
    }

    @Override
    public void exitProgram(JavaScriptParser.ProgramContext ctx) {
        if (lastVariableStatements.size() > 1) {
            inlineDictionary.add(new ArrayList<>(lastVariableStatements));
        }
    }

    // TODO: negative indexes
    private boolean canBeInlined(VariableStatement statement, List<VariableStatement> lastVariableStatements) {
        if (lastStatementType != LastStatementType.VARIABLE || lastVariableStatements.isEmpty()) {
            return false;
        }
        VariableStatement lastVariableStatement = lastVariableStatements.get(lastVariableStatements.size() - 1);
        return lastVariableStatement.getArrayName().equals(statement.getArrayName())
                && lastVariableStatement.getIndex() == statement.getIndex() - 1;
    }
    private boolean isVariableStatementArrayAssigned(VariableStatementContext ctx) {
        SingleExpressionContext expressionContext =
                ctx.variableDeclarationList().variableDeclaration(0).singleExpression();
        if (!(expressionContext instanceof MemberIndexExpressionContext)) {
            return false;
        }
        MemberIndexExpressionContext indexContext = (MemberIndexExpressionContext) expressionContext;
        return indexContext.singleExpression() instanceof IdentifierExpressionContext
                && indexContext.expressionSequence().singleExpression().size() == 1
                && isNumeric(indexContext.expressionSequence().singleExpression(0).getText());
    }

    private boolean isVariableStatementHandled(VariableStatementContext ctx) {
        return ctx.varModifier().Var() != null
            && ctx.variableDeclarationList().variableDeclaration().size() == 1
            && ctx.variableDeclarationList().variableDeclaration(0).Identifier() != null;
    }

    @Override
    public void exitVariableStatement(VariableStatementContext ctx) {
        super.exitVariableStatement(ctx);
    }

    public InlineDictionary getInlineDictionary() {
        return inlineDictionary;
    }

    private enum LastStatementType {
        VARIABLE,
        ASSIGNMENT,
        OTHER;
    }
}
