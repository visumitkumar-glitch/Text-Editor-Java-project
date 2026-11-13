# Mini Text Editor with Undo/Redo (Java, pure OOP)

A console text editor built with plain Java: no frameworks, no external libraries.

## Design patterns and OOP concepts
| Concept | Where |
|---|---|
| Command pattern | `Command` interface, `InsertCommand`, `DeleteCommand`, `ReplaceCommand` |
| Memento pattern | `DocumentMemento`, used by `ReplaceAllCommand` to undo |
| Composite pattern | `MacroCommand` (macros and undo grouping) |
| Abstraction / Inheritance | `AbstractCommand` base class |
| Polymorphism | `History` and `Editor` treat every edit as a `Command` |
| Encapsulation | `Document` hides its buffer; memento state is package-private |
| Custom exception | `InvalidEditException` |
| Stacks | `History` (undo and redo `Deque`s) |

## Structure
```
src/com/editor/
  Main.java, SelfTest.java
  command/    Command, AbstractCommand, Insert/Delete/Replace/ReplaceAll/MacroCommand
  core/       Editor (facade: grouping + macros)
  exception/  InvalidEditException
  history/    History (undo/redo stacks)
  model/      Document, DocumentMemento
  ui/         ConsoleUI
```

## Build and run (JDK 11+)
Linux/macOS: `./build.sh` then `java -cp out com.editor.Main`
Windows: `build.bat` then `java -cp out com.editor.Main`
Tests: `java -cp out com.editor.SelfTest`

## Commands
```
show | append <text> | insert <pos> <text> | delete <start> <end>
replace <start> <end> <text> | replaceall <find> <with> | find <text>
undo [n] | redo [n] | history
group begin | group end        (several edits = one undo step)
macro record <name> | macro stop | macro play <name> | macro list
help | exit
```

## Sample session
```
> append Hello World
> replaceall World Java
> undo
> group begin
> append  one
> append  two
> group end
> undo          (removes both edits at once)
```
