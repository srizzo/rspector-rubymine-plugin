package io.github.srizzo.rspector.util;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.RSymbol;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RListOfExpressions;
import org.jetbrains.plugins.ruby.ruby.lang.psi.iterators.RBlockCall;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RFName;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RIdentifier;
import org.jetbrains.plugins.ruby.testing.rspec.codeInsight.RSpecSymbolDefiningCallType;

public class LetRCallPsiUtil {
    @Nullable
    public static RCall getEnclosingLetRCall(@Nullable PsiElement candidate) {
        if (candidate instanceof RFName) return getEnclosingLetRCall((RFName) candidate);
        if (candidate instanceof RCall) return getEnclosingLetRCall((RCall) candidate);
        if (candidate instanceof RBlockCall) return getEnclosingLetRCall((RBlockCall) candidate);
        if (candidate instanceof RSymbol) return getEnclosingLetRCall((RSymbol) candidate);
        if (candidate instanceof RIdentifier) return getEnclosingLetRCall((RIdentifier) candidate);
        if (candidate instanceof RListOfExpressions) return getEnclosingLetRCall((RListOfExpressions) candidate);
        return null;
    }

    @Nullable
    static RCall getEnclosingLetRCall(@Nullable RCall candidate) {
        if (candidate == null) return null;
        return candidate.getCallType() instanceof RSpecSymbolDefiningCallType ? candidate : null;
    }

    @Nullable
    private static RCall getEnclosingLetRCall(RBlockCall candidate) {
        if (!LetDeclarationPsiUtil.CALL_DEFINING_NAMES.contains(candidate.getCommand())) return null;

        if (candidate.getCall() instanceof RIdentifier) return getEnclosingLetRCall(candidate.getParent());
        if (candidate.getCall() instanceof RCall) return getEnclosingLetRCall(candidate.getCall());

        return null;
    }

    @Nullable
    private static RCall getEnclosingLetRCall(RSymbol candidate) {
        if (!(candidate.getParent().getParent() instanceof RCall)) return null;
        RCall call = (RCall) candidate.getParent().getParent();
        return getEnclosingLetRCall(call);
    }

    @Nullable
    static RCall getEnclosingLetRCall(RListOfExpressions candidate) {
        if (!(candidate.getParent() instanceof RCall)) return null;
        RCall call = (RCall) candidate.getParent();
        return getEnclosingLetRCall(call);
    }

    @Nullable
    private static RCall getEnclosingLetRCall(RIdentifier candidate) {
        return getEnclosingLetRCall(candidate.getParent());
    }

    @Nullable
    private static RCall getEnclosingLetRCall(RFName candidate) {
        if (candidate.getParent() instanceof RSymbol) return getEnclosingLetRCall(candidate.getParent());
        return null;
    }
}
