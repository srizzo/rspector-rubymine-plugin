package io.github.srizzo.rspector.referencecontributor;

import com.intellij.patterns.*;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import io.github.srizzo.rspector.util.LetDeclarationPsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RListOfExpressions;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RIdentifier;
import org.jetbrains.plugins.ruby.testing.rspec.RSpecUtil;

import java.util.function.BiFunction;

public class LetDeclarationsReferenceContributor extends PsiReferenceContributor {
    public static final double PRIORITY = Double.MAX_VALUE;

    public static final PsiFilePattern.Capture<PsiFile> RSPEC_TEST_FILE_PATTERN = PlatformPatterns.psiFile().with(
            new PatternCondition<>("Is RSpec Test File") {
                public boolean accepts(@NotNull PsiFile file, ProcessingContext context) {
                    return RSpecUtil.isRSpecTestFile(file.getProject(), file.getVirtualFile());
                }
            });
    public static final PsiElementPattern.Capture<RIdentifier> RSPEC_POSSIBLE_LET_VARIABLE_READ_PATTERN = PlatformPatterns.psiElement(RIdentifier.class).inFile(RSPEC_TEST_FILE_PATTERN);
    public static final PsiElementPattern.Capture<RListOfExpressions> RSPEC_POSSIBLE_LET_VARIABLE_OVERRIDE_PATTERN = PlatformPatterns.psiElement(RListOfExpressions.class).withParent(RCall.class);

    public PsiReferenceProvider referenceProvider(BiFunction<PsiElement, ProcessingContext, PsiReference[]> provider) {
        return new PsiReferenceProvider() {
            @NotNull
            @Override
            public PsiReference[] getReferencesByElement(@NotNull PsiElement element,
                                                         @NotNull ProcessingContext context) {
                return provider.apply(element, context);
            }
        };
    }

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(RSPEC_POSSIBLE_LET_VARIABLE_READ_PATTERN,
                referenceProvider((psiElement, processingContext) -> {
                    RIdentifier identifier = (RIdentifier) psiElement;
                    if (LetDeclarationPsiUtil.isPossibleLetReadUsage(identifier))
                        return new PsiReference[]{new ReadLetDeclarationReference(identifier)};
                    return PsiReference.EMPTY_ARRAY;
                }), PRIORITY);

        registrar.registerReferenceProvider(RSPEC_POSSIBLE_LET_VARIABLE_OVERRIDE_PATTERN,
                referenceProvider((psiElement, processingContext) -> {

                    RListOfExpressions rListOfExpressions = (RListOfExpressions) psiElement;
                    if (LetDeclarationPsiUtil.isPossibleLetOverrideUsage(rListOfExpressions))
                        return new PsiReference[]{new OverriddenLetDeclarationReference(rListOfExpressions)};
                    return PsiReference.EMPTY_ARRAY;
                }), PRIORITY);
    }
}
