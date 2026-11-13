package com.editor.command;

import com.editor.model.Document;

/** Shared base class: every concrete command works on one Document. */
public abstract class AbstractCommand implements Command {
    protected final Document document;

    protected AbstractCommand(Document document) {
        this.document = document;
    }

    @Override
    public String toString() {
        return getDescription();
    }
}
