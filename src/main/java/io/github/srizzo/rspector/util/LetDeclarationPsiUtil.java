package io.github.srizzo.rspector.util;

import com.intellij.psi.PsiElement;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.RSymbol;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RListOfExpressions;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RIdentifier;
import org.jetbrains.plugins.ruby.testing.rspec.RSpecUtil;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class LetDeclarationPsiUtil {
    public static Class<RCall> LET_DEFINITION_CLASS = RCall.class;
    public static final Set<String> CALL_DEFINING_NAMES = ContainerUtil.immutableSet("let", "let!", "subject");

    @Nullable
    public static String getLetDeclarationName(RCall letDeclaration) {
        return Optional.ofNullable(getLetIdentifyingSymbol(letDeclaration))
                .map(RSymbol::getName)
                .orElse(null);
    }

    @Nullable
    public static String getLetUsageName(RIdentifier letUsage) {
        if (letUsage instanceof RIdentifier) return letUsage.getName();
        if (letUsage instanceof RCall) return getLetDeclarationName((RCall) letUsage);
        return null;
    }

    @Nullable
    public static RSymbol getLetIdentifyingSymbol(RCall letDeclaration) {
        if (letDeclaration == null) return null;
        if (!(letDeclaration.getCallArguments().getFirstElement() instanceof RSymbol)) return null;

        return (RSymbol) letDeclaration.getCallArguments().getFirstElement();
    }

    public static boolean namesMatch(RCall letDeclarationCall, RIdentifier letUsage) {
        @Nullable RSymbol symbol = getLetIdentifyingSymbol(letDeclarationCall);
        if (symbol == null) return false;

        return Objects.equals(symbol.getName(), getLetUsageName(letUsage));
    }

    public static boolean namesMatch(RCall letDeclarationCall, @Nullable RSymbol letOverride) {
        @Nullable RSymbol symbol = getLetIdentifyingSymbol(letDeclarationCall);
        if (symbol == null || letOverride == null) return false;

        return Objects.equals(symbol.getName(), letOverride.getName());
    }

    public static boolean areSameLetDeclarations(RCall letDeclaration1, PsiElement letDeclaration2) {
        return areEquivalent(LetRCallPsiUtil.getEnclosingLetRCall(letDeclaration1), LetRCallPsiUtil.getEnclosingLetRCall(letDeclaration2));
    }

    public static boolean areEquivalent(@Nullable PsiElement element1, @Nullable PsiElement element2) {
        if (element1 == null || element2 == null) return false;
        return element1.getManager().areElementsEquivalent(LetRCallPsiUtil.getEnclosingLetRCall(element1), element2);
    }

    public static boolean isInRSpecTestFile(@Nullable PsiElement element) {
        return RSpecUtil.isRSpecTestFile(element.getProject(), element.getContainingFile().getVirtualFile());
    }

    public static boolean isPossibleLetOverrideUsage(RListOfExpressions candidate) {
        return LetRCallPsiUtil.getEnclosingLetRCall(candidate) != null;

//        String letDeclarationName = getLetDeclarationName(rCall);
//        return CALL_DEFINING_NAMES.contains(rCall.getName()) &&
//                rCall.getParent() instanceof RCall &&
//                ((RCall) rCall.getParent()).getArguments().size() > 0 &&
//                ((RCall) rCall.getParent()).getArguments().get(0) instanceof RSymbol;
    }

    public static boolean isPossibleLetReadUsage(RIdentifier identifier) {
        return identifier.isCall() && !(identifier.getParent() instanceof RCall && ((RCall) identifier.getParent()).getArguments().size() > 0);
    }

    public static boolean isLetReadOf(PsiElement usage, RCall letDeclaration) {
        if(!(usage instanceof RIdentifier)) return false;
        return FindLetDeclarationsUtil.upstreamLetDeclarationsOf((RIdentifier) usage)
                .anyMatch(resolvedUpstreamLetDeclaration -> areSameLetDeclarations(letDeclaration, resolvedUpstreamLetDeclaration));
    }

    public static boolean isLetOverrideOf(PsiElement usage, RCall letDeclaration) {
        if(!(usage instanceof RListOfExpressions)) return false;
        return FindLetDeclarationsUtil.upstreamLetDeclarationsOf((RListOfExpressions) usage)
                .anyMatch(resolvedUpstreamLetDeclaration -> areSameLetDeclarations(letDeclaration, resolvedUpstreamLetDeclaration));
    }
}
