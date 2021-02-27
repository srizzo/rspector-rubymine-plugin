package io.github.srizzo.rspector.referencesearch;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceService;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.RequestResultProcessor;
import com.intellij.psi.search.UsageSearchContext;
import com.intellij.psi.search.searches.ReferencesSearch.SearchParameters;
import com.intellij.util.Processor;
import io.github.srizzo.rspector.util.FindLetDeclarationsUtil;
import io.github.srizzo.rspector.util.LetDeclarationPsiUtil;
import io.github.srizzo.rspector.referencecontributor.LetDeclarationReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.RSymbol;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RIdentifier;

import java.util.List;

public final class LetVariableReferencesSearch extends QueryExecutorBase<PsiReference, SearchParameters> {
    public static final boolean NOT_PROCESSABLE = true;

    public LetVariableReferencesSearch() {
        super(true);
    }

    public void processQuery(@NotNull SearchParameters params, @NotNull Processor<? super PsiReference> consumer) {
        final RCall rootLetDeclaration;

        if (params.getElementToSearch() instanceof RIdentifier) {
            rootLetDeclaration = FindLetDeclarationsUtil.getRootLetDeclarationOf((RIdentifier) params.getElementToSearch());
            if (rootLetDeclaration == null) return;

        } else if (params.getElementToSearch() instanceof RSymbol ||
                params.getElementToSearch() instanceof RCall) {
            final RCall letDeclarationToFindReferencesFor = FindLetDeclarationsUtil.findLetDeclaration(params.getElementToSearch());
            if (letDeclarationToFindReferencesFor == null) return;

            rootLetDeclaration = FindLetDeclarationsUtil.getRootLetDeclarationOf(letDeclarationToFindReferencesFor);
            if (rootLetDeclaration == null) return;
        } else {
            return;
        }

        RSymbol searchTarget = LetDeclarationPsiUtil.getLetIdentifyingSymbol(rootLetDeclaration);

        final String name = LetDeclarationPsiUtil.getLetDeclarationName(rootLetDeclaration);
        if (StringUtil.isEmpty(name)) return;

        params.getOptimizer().searchWord(name,
                new LocalSearchScope(rootLetDeclaration.getContainingFile()),
                UsageSearchContext.IN_CODE,
                true,
                searchTarget,
                new RequestResultProcessor(new Object[] { rootLetDeclaration }) {
            @Override
            public boolean processTextOccurrence(@NotNull PsiElement usage, int offsetInElement, @NotNull Processor<? super PsiReference> processor) {
                if (!usage.isValid())
                    return NOT_PROCESSABLE;

                LetDeclarationReference letDeclarationReference = getUpstreamLetDeclarationsReference(usage);
                if (letDeclarationReference == null)
                    return NOT_PROCESSABLE;

                processor.process(letDeclarationReference);
                return NOT_PROCESSABLE;
            }
        });
    }

    @Nullable
    private static LetDeclarationReference getUpstreamLetDeclarationsReference(@NotNull PsiElement usage) {
//        new PsiReferenceService.Hints(letDeclarationToFindReferencesFor, offsetInElement)
        List<PsiReference> usageReferences = PsiReferenceService.getService().getReferences(usage, PsiReferenceService.Hints.NO_HINTS);
        LetDeclarationReference letDeclarationReference = usageReferences.stream()
                .filter(LetDeclarationReference.class::isInstance)
                .map(LetDeclarationReference.class::cast).findFirst().orElse(null);
        return letDeclarationReference;
    }

}
