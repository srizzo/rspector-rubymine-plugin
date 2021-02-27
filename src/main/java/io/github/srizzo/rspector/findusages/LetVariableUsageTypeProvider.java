package io.github.srizzo.rspector.findusages;

import com.intellij.find.findUsages.PsiElement2UsageTargetAdapter;
import com.intellij.psi.PsiElement;
import com.intellij.usages.UsageTarget;
import com.intellij.usages.impl.rules.UsageType;
import com.intellij.usages.impl.rules.UsageTypeProviderEx;
import io.github.srizzo.rspector.RSpectorBundle;
import io.github.srizzo.rspector.util.FindLetDeclarationsUtil;
import io.github.srizzo.rspector.util.LetDeclarationPsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class LetVariableUsageTypeProvider implements UsageTypeProviderEx {

    private static final UsageType RSPEC_READ_LET_VALUE = new UsageType(() -> RSpectorBundle.message("rspec.usage.type.read.let.variable"));
    private static final UsageType RSPEC_OVERRIDE_LET_VALUE = new UsageType(() -> RSpectorBundle.message("rspec.usage.type.override.let.variable"));

    @Nullable
    public UsageType getUsageType(@NotNull PsiElement element) {
        return null;
    }

    @Nullable
    public UsageType getUsageType(@NotNull PsiElement usage, @NotNull UsageTarget[] targets) {

        final RCall usageTarget = getTargetLetDeclaration(targets);
        if (usageTarget == null) return null;

        final RCall rootLetDeclaration = FindLetDeclarationsUtil.getRootLetDeclarationOf(usageTarget);

        if (LetDeclarationPsiUtil.isLetReadOf(usage, rootLetDeclaration)) return RSPEC_READ_LET_VALUE;

        if (LetDeclarationPsiUtil.areSameLetDeclarations(rootLetDeclaration, usage))
            return null;
//            return new UsageType(() -> "[ROOT]");

        if (LetDeclarationPsiUtil.isLetOverrideOf(usage, rootLetDeclaration)) return RSPEC_OVERRIDE_LET_VALUE;

//        if (RSpecPsiUtil.isLetOverrideOf(usageTarget, RSpecPsiUtil.findLetDeclarationCall(usage)))
//            return new UsageType(() -> "[OVERRIDEN BY]");
//            return RSPEC_OVERRIDDEN_BY_LET_VALUE;


        return null;
    }

    @Nullable
    private RCall getTargetLetDeclaration(@NotNull UsageTarget[] targets) {
        List<RCall> targetsLetDeclarations = Arrays.stream(targets)
                .filter(PsiElement2UsageTargetAdapter.class::isInstance)
                .map(PsiElement2UsageTargetAdapter.class::cast)
                .map(PsiElement2UsageTargetAdapter::getElement)
                .filter(LetDeclarationPsiUtil.LET_DEFINITION_CLASS::isInstance)
                .map(LetDeclarationPsiUtil.LET_DEFINITION_CLASS::cast)
                .map(FindLetDeclarationsUtil::findLetDeclaration)
                .collect(Collectors.toList());

        if (targetsLetDeclarations.size() > 1) {
            System.out.println("multiple reference contributors " + targetsLetDeclarations.size());
        }

        if (targetsLetDeclarations.isEmpty())
            return null;

        return targetsLetDeclarations.get(0);
    }
}
