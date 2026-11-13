package com.editor.history;

import com.editor.command.Command;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/** The "Caretaker": keeps the undo and redo stacks. It never edits the document itself. */
public class History {
    private final Deque<Command> undoStack = new ArrayDeque<>();
    private final Deque<Command> redoStack = new ArrayDeque<>();

    /** Records a command that was just executed. A fresh edit invalidates the redo stack. */
    public void push(Command command) {
        undoStack.push(command);
        redoStack.clear();
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    /** Undoes the most recent command and returns it. */
    public Command undo() {
        Command c = undoStack.pop();
        c.undo();
        redoStack.push(c);
        return c;
    }

    public Command redo() {
        Command c = redoStack.pop();
        c.execute();
        undoStack.push(c);
        return c;
    }

    /** Most recent first. */
    public List<String> describeUndoStack() {
        List<String> out = new ArrayList<>();
        for (Command c : undoStack) {
            out.add(c.getDescription());
        }
        return out;
    }

    public int undoSize() {
        return undoStack.size();
    }

    public int redoSize() {
        return redoStack.size();
    }
}
