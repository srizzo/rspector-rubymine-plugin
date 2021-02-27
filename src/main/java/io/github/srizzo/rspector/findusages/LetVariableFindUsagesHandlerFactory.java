package io.github.srizzo.rspector.findusages;

import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.find.findUsages.FindUsagesHandlerFactory;
import com.intellij.psi.PsiElement;
import io.github.srizzo.rspector.util.FindLetDeclarationsUtil;
import io.github.srizzo.rspector.util.FindUsagesUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class LetVariableFindUsagesHandlerFactory extends FindUsagesHandlerFactory {
    public boolean canFindUsages(@NotNull PsiElement element) {
        return FindUsagesUtil.canFindUsagesFor(element);
    }

    @Nullable
    public FindUsagesHandler createFindUsagesHandler(@NotNull PsiElement element, boolean forHighlightUsages) {
        return this.createFindUsagesHandler(element, forHighlightUsages ? OperationMode.HIGHLIGHT_USAGES : OperationMode.USAGES_WITH_DEFAULT_OPTIONS);
    }

    @Nullable
    public FindUsagesHandler createFindUsagesHandler(@NotNull PsiElement element, @NotNull OperationMode operationMode) {
        return Optional.of(FindLetDeclarationsUtil.findLetDeclaration(element))
                .map(LetVariableFindUsagesHandler::new)
                .orElse(null);
    }
}
