package io.github.srizzo.rspector.referencecontributor;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import io.github.srizzo.rspector.util.FindLetDeclarationsUtil;
import io.github.srizzo.rspector.util.LetDeclarationPsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RIdentifier;

public class ReadLetDeclarationReference extends LetDeclarationReference<RIdentifier> {
    private final RIdentifier letRead;
    private final RCall letDeclaration;

    public ReadLetDeclarationReference(@NotNull RIdentifier usage) {
        super(usage, new TextRange(0, usage.getTextLength()), false);
        letRead = usage;
        letDeclaration = FindLetDeclarationsUtil.getReadLetDeclaration(letRead);
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
}
