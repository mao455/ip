# Bo UI test plan

These tests exercise Bo through its standard input and output. Each test case
starts a fresh Bo process. The runner checks the expected non-empty output
lines in order, ignoring only surrounding whitespace; decorative separators and
the startup banner therefore do not need to be repeated in every expectation.

Run the complete plan with:

```bash
python3 .codex/skills/test-ui/scripts/run_ui_tests.py
```

The runner compiles with Java 25, prints the full console input/output for each
case, and stops immediately if a case fails.

## Test case 1: Add each task type and list tasks

Aim: Verify that todo, deadline, and event commands create the correct task types and that `list` displays them in order.

### Input

```text
todo read book
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
list
bye
```

### Expected output

```text
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list.
Got it. I've added this task:
[D][ ] return book (by: June 6th)
Now you have 2 tasks in the list.
Got it. I've added this task:
[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
Now you have 3 tasks in the list.
Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: June 6th)
3.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
Bye. Hope to see you again soon!
```

## Test case 2: Skip malformed saved records

Aim: Verify that malformed records do not crash Bo or prevent valid records later in the file from loading.

### Initial saved file

```text
T | 1 | valid todo
not a task record
D | 0 | valid deadline | Friday
E | 2 | invalid status | Monday | Tuesday
E | 0 | valid event | Monday | Tuesday
```

### Input

```text
list
bye
```

### Expected output

```text
Warning: I skipped 2 invalid task line(s) in the saved file.
Here are the tasks in your list:
1.[T][X] valid todo
2.[D][ ] valid deadline (by: Friday)
3.[E][ ] valid event (from: Monday to: Tuesday)
Bye. Hope to see you again soon!
```

## Test case 3: Preserve separator characters in task fields

Aim: Verify that task descriptions and date fields containing the storage separator or a backslash survive saving and loading.

### Input

```text
todo compare A | B \\ C
deadline review A | B /by Friday | 5pm
list
bye
```

### Expected output

```text
Got it. I've added this task:
[T][ ] compare A | B \\ C
Got it. I've added this task:
[D][ ] review A | B (by: Friday | 5pm)
Here are the tasks in your list:
1.[T][ ] compare A | B \\ C
2.[D][ ] review A | B (by: Friday | 5pm)
Bye. Hope to see you again soon!
```

### Expected saved file

```text
T | 0 | compare A \| B \\\\ C
D | 0 | review A \| B | Friday \| 5pm
```

## Test case 4: Mark and unmark a task

Aim: Verify that `mark` changes a task's status to done and `unmark` changes it back.

### Input

```text
todo read book
todo return book
mark 2
unmark 2
list
bye
```

### Expected output

```text
Nice! I've marked this task as done:
[T][X] return book
OK, I've marked this task as not done yet:
[T][ ] return book
1.[T][ ] read book
2.[T][ ] return book
Bye. Hope to see you again soon!
```

## Test case 5: Delete a task and renumber the list

Aim: Verify that deleting a task removes the selected task, shifts later tasks, and updates the task count.

### Input

```text
todo read book
todo return book
todo buy bread
mark 1
delete 2
list
bye
```

### Expected output

```text
Nice! I've marked this task as done:
[T][X] read book
Noted. I've removed this task:
[T][ ] return book
Now you have 2 tasks in the list.
1.[T][X] read book
2.[T][ ] buy bread
Bye. Hope to see you again soon!
```

## Test case 6: Reject invalid commands and arguments

Aim: Verify that malformed input produces friendly errors and does not terminate Bo or add invalid tasks.

### Input

```text
todo
deadline return book
event project meeting /from Monday
blah
delete abc
delete 99
mark
list
bye
```

### Expected output

```text
OOPS!!! The description of a todo cannot be empty.
OOPS!!! A deadline must include a /by date, e.g. deadline return book /by Friday.
OOPS!!! An event must include a /to time.
OOPS!!! I'm sorry, but I don't know what that means :-(
OOPS!!! The task number must be a whole number.
OOPS!!! I couldn't find a task with that number.
OOPS!!! Please use mark followed by one task number, e.g. mark 1.
Here are the tasks in your list:
Bye. Hope to see you again soon!
```

## Test case 7: Save the task list after changes

Aim: Verify that adding, marking, unmarking, and deleting tasks writes the current task list to disk.

### Input

```text
todo write report
deadline submit form /by Friday
event project sync /from Monday /to Tuesday
mark 1
unmark 1
delete 2
bye
```

### Expected output

```text
Got it. I've added this task:
[T][ ] write report
Got it. I've added this task:
[D][ ] submit form (by: Friday)
Got it. I've added this task:
[E][ ] project sync (from: Monday to: Tuesday)
Nice! I've marked this task as done:
[T][X] write report
OK, I've marked this task as not done yet:
[T][ ] write report
Noted. I've removed this task:
[D][ ] submit form (by: Friday)
Bye. Hope to see you again soon!
```

### Expected saved file

```text
T | 0 | write report
E | 0 | project sync | Monday | Tuesday
```

## Test case 8: Load tasks at startup

Aim: Verify that Bo loads todo, deadline, and event tasks, including their completion status, from the saved file when it starts.

### Initial saved file

```text
T | 1 | read book
D | 0 | return book | Friday
E | 0 | project sync | Monday | Tuesday
```

### Input

```text
list
bye
```

### Expected output

```text
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: Friday)
3.[E][ ] project sync (from: Monday to: Tuesday)
Bye. Hope to see you again soon!
```
