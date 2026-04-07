# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and verification

- `./gradlew build` — full build.
- `./gradlew check` — main local verification task used by the shared run configuration; runs tests and coverage checks.
- `./gradlew verifyPlugin` — JetBrains plugin verification against recommended IDE builds.
- `./gradlew runIde` — launch a development IDE with the plugin installed.
- `./gradlew runIdeForUiTests` — launch the IDE with the robot server enabled for UI tests.
- `./gradlew test --tests 'fully.qualified.TestName'` — run a single test class or method when tests exist.

## Project setup

- Gradle wrapper uses Gradle 9.2.1.
- Kotlin JVM toolchain is pinned to Java 21.
- The plugin targets IntelliJ Platform `2025.3` and depends on the Ruby plugin (`org.jetbrains.plugins.ruby`).
- Plugin versioning, target build range, Marketplace metadata, and platform dependency versions live in `gradle.properties`.

## Architecture

This is a small IntelliJ Platform plugin that adds RubyMine/RSpec PSI integrations for RSpec `let`-style declarations.

### Integration points

The plugin is wired through `src/main/resources/META-INF/plugin.xml`:

- a Ruby `psi.referenceContributor` registers references for potential `let` reads and overrides
- custom find-usages handler/provider and usage-type provider replace Ruby defaults for supported targets
- a `referencesSearch` extension makes rename/find-usages work from the root declaration
- custom `elementManipulator`, `renamePsiElementProcessor`, and `elementDescriptionProvider` make rename/navigation behave like a first-class language feature

### Core model

Most logic revolves around identifying RSpec symbol-defining calls (`let`, `let!`, and `subject`) and mapping Ruby PSI back to the defining `RCall`.

- `util/LetRCallPsiUtil.java` converts different PSI shapes (`RCall`, `RBlockCall`, `RSymbol`, `RIdentifier`, etc.) into the enclosing RSpec declaration call.
- `util/LetDeclarationPsiUtil.java` handles declaration names, matching, equivalence, and usage classification.
- `util/FindLetDeclarationsUtil.java` walks outward through nested RSpec example groups to find upstream declarations, overridden declarations, and the root declaration for a name.

That upstream-scope walk is the key behavior in this repo: nested example groups can shadow `let` declarations, so navigation and rename must distinguish between the nearest declaration and the root declaration.

### Feature flow

- `referencecontributor/*` attaches references directly to Ruby PSI nodes in RSpec files, separating read references from override references.
- `referencesearch/LetVariableReferencesSearch.java` limits searches to the containing file, searches by declaration name, then filters candidate occurrences back down to RSpector reference types.
- `findusages/*` customizes which elements support Find Usages and how they are labeled in the IDE.
- `refactoring/LetVariableRenameProcessor.java` redirects rename operations to the identifying symbol of the enclosing declaration and filters results to RSpector references only.

## Repository conventions

- Plugin description for Marketplace publishing is extracted from the `<!-- Plugin description -->` block in `README.md`; keep that block in sync with plugin capabilities.
- Changelog HTML for the plugin manifest is generated from `CHANGELOG.md` by the Gradle changelog plugin.
- There are currently shared IDE run configurations for `runIde`, `check`, and `verifyPlugin` under `.run/`.
- There are no committed tests under `src/test` right now, even though the build is configured for JUnit 4 and `check`/`test` tasks are available.
