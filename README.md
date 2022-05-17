# RSpector Plugin

![Build](https://github.com/srizzo/rspector-plugin/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/io.github.srizzo.rspector.rspector-plugin.svg)](https://plugins.jetbrains.com/plugin/io.github.srizzo.rspector.rspector-plugin)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/io.github.srizzo.rspector.rspector-plugin.svg)](https://plugins.jetbrains.com/plugin/io.github.srizzo.rspector.rspector-plugin)

<!-- Plugin description -->
Enhanced RSpec Support for RubyMine


## Currently:

- [x] _Find Usages_ on `let` variables
- [x] _Go to Declaration or Usages_ on `let` variables
- [x] _Refactor | Rename..._  of `let` variable names and usages

## Roadmap

- [ ] _Go to Declaration or Usages_ `subject` variable names
- [ ] _Find Usages_ on `shared_context` and `shared_examples`
- [ ] Autocompletion for `include_context`, `include_examples` and `it_behaves_like`
- [ ] Intentions and autocompletion for mocks and doubles
<!-- Plugin description end -->

## Installation

- From the marketplace:

  https://plugins.jetbrains.com/plugin/16250-rspector

- Using IDE built-in plugin system:

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "RSpector"</kbd> >
  <kbd>Install Plugin</kbd>

- Manually:

  Download the [latest release](https://github.com/srizzo/rspector-plugin/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
