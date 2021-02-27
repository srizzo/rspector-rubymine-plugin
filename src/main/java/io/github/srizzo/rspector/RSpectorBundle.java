package io.github.srizzo.rspector;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public final class RSpectorBundle extends DynamicBundle {
    public static final String BUNDLE = "messages.RSpectorBundle";

    private static final RSpectorBundle INSTANCE = new RSpectorBundle();

    private RSpectorBundle() {
        super(BUNDLE);
    }

    public static @Nls
    @NotNull
    String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, Object... params) {
        return INSTANCE.getMessage(key, params);
    }
}
