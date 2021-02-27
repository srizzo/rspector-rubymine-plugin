package io.github.srizzo.rspector.referencecontributor;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import io.github.srizzo.rspector.util.FindLetDeclarationsUtil;
import io.github.srizzo.rspector.util.LetDeclarationPsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.RSymbol;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RListOfExpressions;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;

public class OverriddenLetDeclarationReference extends LetDeclarationReference<RListOfExpressions> {
    private final RCall letOverride;
    private final RCall letDeclaration;

    public OverriddenLetDeclarationReference(RListOfExpressions usage) {
        super(usage, new TextRange(0, usage.getTextLength()), false);
        letOverride = FindLetDeclarationsUtil.findLetDeclaration(usage);
        letDeclaration = FindLetDeclarationsUtil.getOverriddenLetDeclaration(letOverride);
    }

    @Override
    @Nullable
    public PsiElement resolve() {
        return letDeclaration;
    }

    @Override
    public boolean isReferenceTo(@NotNull PsiElement reference) {
        return LetDeclarationPsiUtil.areSameLetDeclarations(letDeclaration, reference);
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        RSymbol letIdentifyingSymbol = LetDeclarationPsiUtil.getLetIdentifyingSymbol(letOverride);
        var manipulator = ElementManipulators.getManipulator(letIdentifyingSymbol);
        return manipulator.handleContentChange(letIdentifyingSymbol, getRangeInElement(), newElementName);
    }
}
