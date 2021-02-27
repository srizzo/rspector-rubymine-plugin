package io.github.srizzo.rspector.findusages;

import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.psi.PsiElement;
import io.github.srizzo.rspector.RSpectorBundle;
import io.github.srizzo.rspector.util.FindLetDeclarationsUtil;
import io.github.srizzo.rspector.util.FindUsagesUtil;
import io.github.srizzo.rspector.util.LetDeclarationPsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.RubyWordsScanner;
import org.jetbrains.plugins.ruby.ruby.lang.findUsages.RubyFindUsagesProvider;

public final class LetVariableFindUsagesProvider extends RubyFindUsagesProvider {
    public boolean canFindUsagesFor(@NotNull PsiElement element) {
        return FindUsagesUtil.canFindUsagesFor(element);
    }

    @NotNull
    public String getDescriptiveName(@NotNull PsiElement element) {
//        if (true) return "getDescriptiveName";
        if (!FindUsagesUtil.canFindUsagesFor(element))
            return super.getDescriptiveName(element) + " [DN]";
        return LetDeclarationPsiUtil.getLetDeclarationName(FindLetDeclarationsUtil.findLetDeclaration(element)) + " [DN]";
    }

    @NotNull
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
//        if (true) return "getNodeText";
        if (!FindUsagesUtil.canFindUsagesFor(element))
            return super.getNodeText(element, useFullName) + " [NT]";
        return LetDeclarationPsiUtil.getLetDeclarationName(FindLetDeclarationsUtil.findLetDeclaration(element)) + " [NT]";
    }

    @NotNull
    public String getType(@NotNull PsiElement usageTarget) {
        if (!FindUsagesUtil.canFindUsagesFor(usageTarget)) {
            return super.getType(usageTarget);
        }
        return RSpectorBundle.message("rspec.usage.type.let.variable");
    }

    @NotNull
    public WordsScanner getWordsScanner() {
        return new RubyWordsScanner();
    }
}
