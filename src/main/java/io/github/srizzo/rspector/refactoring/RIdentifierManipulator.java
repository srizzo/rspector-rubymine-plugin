package io.github.srizzo.rspector.refactoring;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RIdentifier;

public class RIdentifierManipulator extends AbstractElementManipulator<RIdentifier> {
    @NotNull
    public TextRange getRangeInElement(@NotNull RIdentifier element) {
        return TextRange.create(0, element.getTextLength());
    }

    @Nullable
    public RIdentifier handleContentChange(@NotNull RIdentifier element, @NotNull TextRange range, String newContent) throws IncorrectOperationException {
        return (RIdentifier) element.setName(newContent);
    }
}

