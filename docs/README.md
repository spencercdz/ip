# Jaku User Guide

Jaku is a calm desktop task companion. Run `./gradlew run` to open its JavaFX chat window, type a command, and press Enter or Send. The command-line entry point remains available as `jaku.Jaku` for automated regression testing.

## Adding deadlines

Use `deadline DESCRIPTION /by yyyy-MM-dd` to add a dated task.

```
expected output
```

## Other commands

`todo DESCRIPTION`, `event DESCRIPTION /from START /to END`, `list`, `find KEYWORD`, `mark NUMBER`, `unmark NUMBER`, `delete NUMBER`, and `bye` are supported.

## Recurring tasks

Create a daily or weekly task with `repeat todo DESCRIPTION /from yyyy-MM-dd /every daily|weekly`.

Create a recurring event with `repeat event DESCRIPTION /from yyyy-MM-dd HH:mm /to yyyy-MM-dd HH:mm /every daily|weekly`. Its end must be after its start.

Marking a recurring task advances it by one interval. Unmarking it reverses it by one interval. Jaku saves the current occurrence automatically.

## Saved-data recovery

If Jaku cannot safely read its saved-task file, it starts in recovery mode and leaves that file unchanged. You can still inspect the empty session, but Jaku blocks task changes until you repair or replace the saved file and restart the app.
