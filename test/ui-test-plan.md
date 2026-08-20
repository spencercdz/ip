# UI Test Plan

Each case is run in a fresh Jaku process. Output comparisons are exact, including spacing and divider lines. Use `␠` to represent a trailing space in an expected-output block.

## L3-1 Mark and unmark tasks

Aim: Verify the Level 3 task lifecycle and guard existing behavior before Level 4 changes.

### Input

```text
read book
return book
mark 2
list
unmark 2
list

bye
```

### Expected output

```text
____________________________________________________________
     _     _     _  __ _   _␠
    | |   / \   | |/ /| | | |
 _  | |  / _ \  | ' / | | | |
| |_| | / ___ \ | . \ | |_| |
 \___/ /_/   \_\|_|\_\ \___/␠
Hello there! I'm Jaku.
How can I help you today?
____________________________________________________________
____________________________________________________________
added: read book
____________________________________________________________
____________________________________________________________
added: return book
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [X] return book
____________________________________________________________
____________________________________________________________
1.[ ] read book
2.[X] return book
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
  [ ] return book
____________________________________________________________
____________________________________________________________
1.[ ] read book
2.[ ] return book
____________________________________________________________
Bye for now. Hope to see you again soon!
____________________________________________________________
```
