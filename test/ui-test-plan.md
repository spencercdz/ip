# UI Test Plan

Each case is run in a fresh Jaku process. Output comparisons are exact, including spacing and divider lines. Use `␠` to represent a trailing space in an expected-output block.

## L4-1 Add and list every task type

Aim: Verify todos, ISO deadline dates, multi-day events, confirmations, counts, and list formatting.

### Input

```text
todo borrow book
deadline do homework /by 2026-08-30
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
  [D][ ] do homework (by: Aug 30 2026)
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
2.[D][ ] do homework (by: Aug 30 2026)
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
deadline middle task /by 2026-09-01
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
  [D][ ] middle task (by: Sep 1 2026)
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
2.[D][ ] middle task (by: Sep 1 2026)
3.[E][X] last task (from: Mon 2pm to: 4pm)
____________________________________________________________
____________________________________________________________
OK, I've marked this task as not done yet:
  [T][ ] first task
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] first task
2.[D][ ] middle task (by: Sep 1 2026)
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
I don't recognize that command. Try todo, deadline, event, list, mark, unmark, delete, find, or bye.
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
deadline write report /by 2026-08-28
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
  [D][ ] write report (by: Aug 28 2026)
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
1.[D][ ] write report (by: Aug 28 2026)
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
I don't recognize that command. Try todo, deadline, event, list, mark, unmark, delete, find, or bye.
____________________________________________________________
____________________________________________________________
I don't recognize that command. Try todo, deadline, event, list, mark, unmark, delete, find, or bye.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][ ] only task
____________________________________________________________
Bye for now. Hope to see you again soon!
____________________________________________________________
```

## L6-1 Delete a typed task and renumber the list

Aim: Verify case-insensitive deletion, removed-task formatting, remaining count, and automatic renumbering.

### Input

```text
todo read book
deadline return book /by 2026-06-06
event project meeting /from Aug 6th 2pm /to 4pm
todo join sports club
mark 1
mark 2
DELETE 3
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
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Jun 6 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] join sports club
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [T][X] read book
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [D][X] return book (by: Jun 6 2026)
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[T][X] read book
2.[D][X] return book (by: Jun 6 2026)
3.[T][ ] join sports club
____________________________________________________________
Bye for now. Hope to see you again soon!
____________________________________________________________
```

## L6-2 Reject invalid deletions and empty the list

Aim: Interleave invalid and valid deletions while verifying state remains correct and an empty list is handled.

### Input

```text
delete
delete nope
delete 0
todo only task
delete 2
delete -1
delete 1
delete 1
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
Use: delete <task number>.
____________________________________________________________
____________________________________________________________
Use: delete <task number>.
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
Task number -1 is not in the list.
____________________________________________________________
____________________________________________________________
Noted. I've removed this task:
  [T][ ] only task
Now you have 0 tasks in the list.
____________________________________________________________
____________________________________________________________
Task number 1 is not in the list.
____________________________________________________________
____________________________________________________________
Your list is empty for now.
____________________________________________________________
Bye for now. Hope to see you again soon!
____________________________________________________________
```

## L7-1 Save tasks after successful mutations

Aim: Verify a missing data directory is created and all task types, including completion state, are persisted.

### Input

```text
todo read book
deadline return book /by 2026-09-01
event meeting /from 2pm /to 4pm
mark 2
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
  [T][ ] read book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return book (by: Sep 1 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] meeting (from: 2pm to: 4pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Nice! I've marked this task as done:
  [D][X] return book (by: Sep 1 2026)
____________________________________________________________
Bye for now. Hope to see you again soon!
____________________________________________________________
```

### Expected saved data

```text
T	0	read book
D	1	return book	2026-09-01
E	0	meeting	2pm	4pm
```

## L7-2 Load valid records and skip corrupt records

Aim: Verify a later Jaku process restores valid task types and completion state while silently skipping malformed data.

### Input

```text
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
Here are the tasks in your list:
1.[T][ ] read book
2.[D][X] return book (by: Sep 1 2026)
3.[E][ ] meeting (from: 2pm to: 4pm)
____________________________________________________________
Bye for now. Hope to see you again soon!
____________________________________________________________
```

### Initial data

```text
T	0	read book
D	1	return book	2026-09-01
E	0	meeting	2pm	4pm
Q	0	unknown type
D	2	invalid status	Monday
E	0	missing fields
T	0	bad\qescape
D	0	legacy deadline	Sunday
```

## L7-3 Keep running when persistence fails

Aim: Verify a read or write failure produces a framed error and does not add an unsaved task to memory.

### Input

```text
todo cannot save
list
bye
```

### Expected output

```text
____________________________________________________________
I couldn't load your saved tasks.
____________________________________________________________
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
I couldn't save your tasks.
____________________________________________________________
____________________________________________________________
Your list is empty for now.
____________________________________________________________
Bye for now. Hope to see you again soon!
____________________________________________________________
```

### Data path kind

directory

## L8-1 Parse and format deadline dates

Aim: Verify valid ISO dates, including a leap day, are formatted for display while invalid dates do not change task state.

### Input

```text
deadline submit report /by 2024-02-29
deadline wrong format /by 29-02-2024
deadline impossible date /by 2026-02-29
todo unchanged task
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
  [D][ ] submit report (by: Feb 29 2024)
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Use: deadline <description> /by yyyy-MM-dd.
____________________________________________________________
____________________________________________________________
Use: deadline <description> /by yyyy-MM-dd.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] unchanged task
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the tasks in your list:
1.[D][ ] submit report (by: Feb 29 2024)
2.[T][ ] unchanged task
____________________________________________________________
Bye for now. Hope to see you again soon!
____________________________________________________________
```

## L9-1 Find tasks by a case-insensitive keyword

Aim: Verify matching across task types, case-insensitive and multi-word matching, empty results, and missing-keyword guidance.

### Input

```text
todo read Book
deadline return library book /by 2026-06-06
event book club /from 6pm /to 8pm
todo write report
find BOOK
find library book
find meeting
find
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
  [T][ ] read Book
Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [D][ ] return library book (by: Jun 6 2026)
Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [E][ ] book club (from: 6pm to: 8pm)
Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
Got it. I've added this task:
  [T][ ] write report
Now you have 4 tasks in the list.
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
1.[T][ ] read Book
2.[D][ ] return library book (by: Jun 6 2026)
3.[E][ ] book club (from: 6pm to: 8pm)
____________________________________________________________
____________________________________________________________
Here are the matching tasks in your list:
1.[D][ ] return library book (by: Jun 6 2026)
____________________________________________________________
____________________________________________________________
No matching tasks found.
____________________________________________________________
____________________________________________________________
I need a keyword after "find".
____________________________________________________________
Bye for now. Hope to see you again soon!
____________________________________________________________
```
