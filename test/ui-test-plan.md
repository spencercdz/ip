# UI Test Plan

Each case is run in a fresh Jaku process. Output comparisons are exact, including spacing and divider lines. Use `␠` to represent a trailing space in an expected-output block.

## L4-1 Add and list every task type

Aim: Verify todos, arbitrary deadline text, multi-day events, confirmations, counts, and list formatting.

### Input

```text
todo borrow book
deadline do homework /by no idea :-p
event orientation week /from 4/10/2019 /to 11/10/2019
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
Got it. I've added this task:
  [T][ ] borrow book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] do homework (by: no idea :-p)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] orientation week (from: 4/10/2019 to: 11/10/2019)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][ ] do homework (by: no idea :-p)
3.[E][ ] orientation week (from: 4/10/2019 to: 11/10/2019)
____________________________________________________________
Bye for now. Hope to see you again soon!
____________________________________________________________
```

## L4-2 Mark and unmark typed tasks

Aim: Verify first and last indexes and preserve type-specific details while task statuses change.

### Input

```text
todo first task
deadline middle task /by Sunday
event last task /from Mon 2pm /to 4pm
mark 1
mark 3
list
unmark 1
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
Got it. I've added this task:
  [T][ ] first task
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] middle task (by: Sunday)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] last task (from: Mon 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] first task
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [E][X] last task (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] first task
2.[D][ ] middle task (by: Sunday)
3.[E][X] last task (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
  [T][ ] first task
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] first task
2.[D][ ] middle task (by: Sunday)
3.[E][X] last task (from: Mon 2pm to: 4pm)
____________________________________________________________
Bye for now. Hope to see you again soon!
____________________________________________________________
```

## L5-1 Handle empty todos and unknown commands

Aim: Verify minimum Level 5 errors, case-insensitive commands, unchanged state, and continued interaction.

### Input

```text
todo
ToDo kept task
read book

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
I need a description after "todo".
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] kept task
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
I don't recognize that command. Try todo, deadline, event, list, mark, unmark, or bye.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] kept task
____________________________________________________________
Bye for now. Hope to see you again soon!
____________________________________________________________
```

## L5-2 Reject malformed deadlines and events

Aim: Interleave invalid and valid timed tasks while verifying rejected inputs do not change task state.

### Input

```text
deadline
deadline /by Sunday
deadline write report
deadline write report /by
deadline write report /by Friday
event
event /from Monday /to Tuesday
event project meeting
event project meeting /from /to Tuesday
event project meeting /from Monday /to
event project meeting /from Monday /to Tuesday
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
Use: deadline <description> /by <date or time>.
____________________________________________________________
____________________________________________________________
Use: deadline <description> /by <date or time>.
____________________________________________________________
____________________________________________________________
Use: deadline <description> /by <date or time>.
____________________________________________________________
____________________________________________________________
Use: deadline <description> /by <date or time>.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] write report (by: Friday)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Use: event <description> /from <start> /to <end>.
____________________________________________________________
____________________________________________________________
Use: event <description> /from <start> /to <end>.
____________________________________________________________
____________________________________________________________
Use: event <description> /from <start> /to <end>.
____________________________________________________________
____________________________________________________________
Use: event <description> /from <start> /to <end>.
____________________________________________________________
____________________________________________________________
Use: event <description> /from <start> /to <end>.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Monday to: Tuesday)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[D][ ] write report (by: Friday)
2.[E][ ] project meeting (from: Monday to: Tuesday)
____________________________________________________________
Bye for now. Hope to see you again soon!
____________________________________________________________
```

## L5-3 Reject invalid task numbers and command shapes

Aim: Verify task-number errors and unsupported extra arguments without corrupting task status or ending the session.

### Input

```text
mark
unmark nope
mark 0
todo only task
mark 2
mark 1
unmark -1
unmark 1
list now
bye now

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
Use: mark <task number>.
____________________________________________________________
____________________________________________________________
Use: unmark <task number>.
____________________________________________________________
____________________________________________________________
Task number 0 is not in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] only task
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Task number 2 is not in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] only task
____________________________________________________________
____________________________________________________________
Task number -1 is not in the list.
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
  [T][ ] only task
____________________________________________________________
____________________________________________________________
I don't recognize that command. Try todo, deadline, event, list, mark, unmark, or bye.
____________________________________________________________
____________________________________________________________
I don't recognize that command. Try todo, deadline, event, list, mark, unmark, or bye.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] only task
____________________________________________________________
Bye for now. Hope to see you again soon!
____________________________________________________________
```
