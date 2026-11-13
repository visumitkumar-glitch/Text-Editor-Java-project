package com.editor.command;

import com.editor.model.Document;

public class InsertCommand extends AbstractCommand {
    private final int position;
    private final String text;

    public InsertCommand(Document document, int position, String text) {
        super(document);
        this.position = position;
        this.text = text;
    }

    @Override
    public void execute() {
        document.insert(position, text);
    }

    @Override
    public void undo() {
        document.delete(position, position + text.length());
    }

    @Override
    public String getDescription() {
        return "Insert \"" + text + "\" at " + position;
    }
}
