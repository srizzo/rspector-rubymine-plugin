package io.github.srizzo.rspector.refactoring;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.SearchScope;
import io.github.srizzo.rspector.referencecontributor.LetDeclarationReference;
import io.github.srizzo.rspector.util.LetRCallPsiUtil;
import io.github.srizzo.rspector.util.FindLetDeclarationsUtil;
import io.github.srizzo.rspector.util.LetDeclarationPsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;
import org.jetbrains.plugins.ruby.ruby.lang.rename.RubyRenameProcessor;

import java.util.Collection;
import java.util.stream.Collectors;

public final class LetVariableRenameProcessor extends RubyRenameProcessor {

    @Override
    public @NotNull Collection<PsiReference> findReferences(@NotNull PsiElement element, @NotNull SearchScope searchScope, boolean searchInCommentsAndStrings) {
        return super.findReferences(element, searchScope, searchInCommentsAndStrings)
                .stream().filter(LetDeclarationReference.class::isInstance)
                .collect(Collectors.toList());
    }

    @Override
    public @Nullable PsiElement substituteElementToRename(@NotNull PsiElement element, @Nullable Editor editor) {
        RCall letDeclarationCall = LetRCallPsiUtil.getEnclosingLetRCall(element);
        if (letDeclarationCall == null) return null;

        return LetDeclarationPsiUtil.getLetIdentifyingSymbol(letDeclarationCall);
    }

    public boolean canProcessElement(@NotNull PsiElement element) {
        return FindLetDeclarationsUtil.findLetDeclaration(element) != null;
    }
}
