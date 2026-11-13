package com.editor.command;

/** COMMAND pattern: every edit is an object that knows how to do and undo itself. */
public interface Command {
    void execute();

    void undo();

    String getDescription();
}
