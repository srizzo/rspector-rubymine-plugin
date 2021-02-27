package io.github.srizzo.rspector.util;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RCompoundStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RListOfExpressions;
import org.jetbrains.plugins.ruby.ruby.lang.psi.iterators.RBlockCall;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RIdentifier;
import org.jetbrains.plugins.ruby.testing.rspec.RSpecUtil;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public class FindLetDeclarationsUtil {
    @Nullable
    public static RCall findLetDeclaration(@Nullable PsiElement candidate) {
        return LetRCallPsiUtil.getEnclosingLetRCall(candidate);
    }

    @NotNull
    static Stream<RCall> upstreamLetDeclarationsOf(@NotNull RIdentifier candidate) {
        return upstreamLetDeclarationsStartingWith(RSpecUtil.getCoveringExampleGroupScope(candidate))
                .filter(letDeclaration -> LetDeclarationPsiUtil.namesMatch(letDeclaration, candidate))
                .map(FindLetDeclarationsUtil::findLetDeclaration);
    }

    @NotNull
    static Stream<RCall> upstreamLetDeclarationsOf(@NotNull RCall candidate) {
        return upstreamLetDeclarationsStartingWith(RSpecUtil.getCoveringExampleGroupScope(candidate))
                .filter(letDeclaration -> !LetDeclarationPsiUtil.areSameLetDeclarations(candidate, letDeclaration))
                .filter(letDeclaration -> LetDeclarationPsiUtil.namesMatch(letDeclaration, LetDeclarationPsiUtil.getLetIdentifyingSymbol(candidate)))
                .map(FindLetDeclarationsUtil::findLetDeclaration);
    }

    @NotNull
    static Stream<RCall> upstreamLetDeclarationsOf(@NotNull RListOfExpressions candidate) {
        RCall letDeclarationCall = LetRCallPsiUtil.getEnclosingLetRCall(candidate);
        if (letDeclarationCall == null) return Stream.empty();
        return upstreamLetDeclarationsOf(letDeclarationCall);
    }

    @NotNull
    private static Stream<RCall> upstreamLetDeclarationsStartingWith(RBlockCall exampleGroup) {
        if (exampleGroup == null) return Stream.empty();
        return Stream.concat(
                exampleGroupLetDeclarations(exampleGroup),
                upstreamLetDeclarationsStartingWith(RSpecUtil.getCoveringExampleGroupScope(exampleGroup)))
                .sequential();
    }

    @NotNull
    private static Stream<RCall> exampleGroupLetDeclarations(RBlockCall exampleGroup) {
        if (exampleGroup == null) return Stream.empty();

        RCompoundStatement statement = exampleGroup.getBlock().getCompoundStatement();
        return Arrays.stream(statement.getChildren())
                .sequential()
                .map(LetRCallPsiUtil::getEnclosingLetRCall)
                .filter(Objects::nonNull);
    }

    @Nullable
    public static RCall getRootLetDeclarationOf(RCall letDeclaration) {
        return upstreamLetDeclarationsOf(letDeclaration).reduce((first, second) -> second).orElse(letDeclaration);
    }

    @Nullable
    public static RCall getRootLetDeclarationOf(RIdentifier letRead) {
        return upstreamLetDeclarationsOf(letRead).reduce((first, second) -> second).orElse(null);
    }

    @Nullable
    public static RCall getOverriddenLetDeclaration(RCall usage) {
        return upstreamLetDeclarationsOf(usage).findFirst().orElse(null);
    }

    @Nullable
    public static RCall getReadLetDeclaration(RIdentifier usage) {
        return upstreamLetDeclarationsOf(usage).findFirst().orElse(null);
    }
}
