# Jaku User Guide

Jaku is a desktop task chatbot. Run `./gradlew run` to open its JavaFX chat window, type a command, and press Enter or Send. The command-line entry point remains available as `jaku.Jaku` for automated regression testing.

## Adding deadlines

Use `deadline DESCRIPTION /by yyyy-MM-dd` to add a dated task.

```
expected output
```

## Other commands

`todo DESCRIPTION`, `event DESCRIPTION /from START /to END`, `list`, `find KEYWORD`, `mark NUMBER`, `unmark NUMBER`, `delete NUMBER`, and `bye` are supported.
