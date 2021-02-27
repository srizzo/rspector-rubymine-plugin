package io.github.srizzo.rspector.util;

import com.intellij.psi.PsiElement;
import io.github.srizzo.rspector.util.FindLetDeclarationsUtil;
import org.jetbrains.annotations.NotNull;

public class FindUsagesUtil {
    public static boolean canFindUsagesFor(@NotNull PsiElement element) {
        return FindLetDeclarationsUtil.findLetDeclaration(element) != null;
    }
}
