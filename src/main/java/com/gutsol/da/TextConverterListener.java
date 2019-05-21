package com.gutsol.da;

import com.gutsol.da.parser.DefaultJavaScriptParserListener;
import com.gutsol.da.parser.JavaScriptParser;

public class TextConverterListener extends DefaultJavaScriptParserListener {
    private final InlineDictionary dictionary;
    private final StringBuilder builder = new StringBuilder();
    private boolean canPrint = true;

    public TextConverterListener(InlineDictionary dictionary) {
        this.dictionary = dictionary;
    }

    public String getText() {
        return builder.toString();
    }

    @Override
    public void exitStatement(JavaScriptParser.StatementContext ctx) {
        if (ctx.variableStatement() == null) {
            builder.append(dictionary.findText(ctx));
        }
    }

    @Override
    public void enterVariableStatement(JavaScriptParser.VariableStatementContext ctx) {
        String modified = dictionary.findModifiedText(ctx);
        if (modified != null) {
            builder.append(modified);
            canPrint = false;
        } else if (canPrint) {
            builder.append(dictionary.findText(ctx));
        }
    }

    @Override
    public void exitVariableStatement(JavaScriptParser.VariableStatementContext ctx) {
        if (dictionary.isLast(ctx)) {
            canPrint = true;
        }
    }
}
