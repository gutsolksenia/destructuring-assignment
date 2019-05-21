package com.gutsol.da;

import com.gutsol.da.parser.JavaScriptParser.VariableStatementContext;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InlineDictionary {
    private final List<MergableStatements> mergableStatements = new ArrayList<>();
    private final CommonTokenStream tokenStream;

    public InlineDictionary(CommonTokenStream tokenStream) {
        this.tokenStream = tokenStream;
    }

    public void add(List<VariableStatement> statements) {
        String text = generateText(statements);
        VariableStatementContext startPoint = statements.get(0).getUnderlying();
        VariableStatementContext endPoint = statements.get(statements.size() - 1).getUnderlying();
        mergableStatements.add(new MergableStatements(startPoint, endPoint, text));
    }

    private String generateText(List<VariableStatement> statements) {
        String identifiersList = statements.stream()
                .map(VariableStatement::getIdentifier)
                .collect(Collectors.joining(", "));
        return "var [" + identifiersList + "] = " + statements.get(0).getArrayName() + ";\n";
    }

    public boolean isLast(VariableStatementContext ctx) {
        return mergableStatements.stream().anyMatch(s -> s.getEndPoint() == ctx);
    }

    public String findModifiedText(VariableStatementContext ctx) {
        return mergableStatements.stream()
                .filter(s -> s.getStartingPoint() == ctx)
                .map(MergableStatements::getText)
                .findFirst()
                .orElse(null);
    }

    public String findText(ParserRuleContext ctx) {
        Interval sourceInterval = ctx.getSourceInterval();
        List<Token> hiddenTokensToRight = tokenStream.getHiddenTokensToRight(sourceInterval.b);
        String text = tokenStream.getText(sourceInterval);
        if (hiddenTokensToRight != null) {
            text += hiddenTokensToRight.stream().map(Token::getText).collect(Collectors.joining());
        }
        return text;
    }

}
