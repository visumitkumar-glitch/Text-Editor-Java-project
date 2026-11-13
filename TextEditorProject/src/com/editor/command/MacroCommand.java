package com.editor.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * COMPOSITE command: bundles several commands into one.
 * Used for both macros (replayable sequences) and undo grouping
 * (several edits that undo/redo as a single step).
 */
public class MacroCommand implements Command {
    private final String name;
    private final List<Command> commands;

    public MacroCommand(String name, List<Command> commands) {
        this.name = name;
        this.commands = new ArrayList<>(commands);
    }

    @Override
    public void execute() {
        int done = 0;
        try {
            for (Command c : commands) {
                c.execute();
                done++;
            }
        } catch (RuntimeException e) {
            // all-or-nothing: roll back whatever already ran, then report the failure
            for (int i = done - 1; i >= 0; i--) {
                commands.get(i).undo();
            }
            throw e;
        }
    }

    @Override
    public void undo() {
        for (int i = commands.size() - 1; i >= 0; i--) {
            commands.get(i).undo();
        }
    }

    public List<Command> getCommands() {
        return Collections.unmodifiableList(commands);
    }

    public int size() {
        return commands.size();
    }

    @Override
    public String getDescription() {
        return name + " (" + commands.size() + " edits)";
    }

    @Override
    public String toString() {
        return getDescription();
    }
}
