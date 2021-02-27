package io.github.srizzo.rspector.referencecontributor;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;

public abstract class LetDeclarationReference<T extends RPsiElement> extends PsiReferenceBase<T> implements PsiReference {
    public LetDeclarationReference(@NotNull T usage, TextRange textRange, boolean soft) {
        super(usage, textRange, soft);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }
}
