package io.github.srizzo.rspector;

import com.intellij.psi.ElementDescriptionLocation;
import com.intellij.psi.ElementDescriptionProvider;
import com.intellij.psi.PsiElement;
import com.intellij.usageView.UsageViewLongNameLocation;
import io.github.srizzo.rspector.util.LetDeclarationPsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;

public class LetDeclarationDescriptionProvider implements ElementDescriptionProvider {
    @Override
    public String getElementDescription(@NotNull final PsiElement element, @Nullable final ElementDescriptionLocation location) {
        if (element instanceof RCall && location instanceof UsageViewLongNameLocation) {
            String variableName = LetDeclarationPsiUtil.getLetDeclarationName((RCall) element);
            if (variableName == null) return null;
            return "Let Variable :" + variableName;
        }

        return null;
    }
}
