package io.github.srizzo.rspector.findusages;

import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;

final class LetVariableFindUsagesHandler extends FindUsagesHandler {
    LetVariableFindUsagesHandler(@NotNull RCall letDeclaration) {
        super(letDeclaration);
    }

    @NotNull
    public PsiElement[] getSecondaryElements() {
        return super.getSecondaryElements();
    }


    @NotNull
    public PsiElement[] getPrimaryElements() {
        return super.getPrimaryElements();
    }
}
