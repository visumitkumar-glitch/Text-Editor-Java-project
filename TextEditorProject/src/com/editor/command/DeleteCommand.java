package com.editor.command;

import com.editor.model.Document;

public class DeleteCommand extends AbstractCommand {
    private final int start;
    private final int end;
    private String removedText; // remembered so undo can put it back

    public DeleteCommand(Document document, int start, int end) {
        super(document);
        this.start = start;
        this.end = end;
    }

    @Override
    public void execute() {
        removedText = document.delete(start, end);
    }

    @Override
    public void undo() {
        document.insert(start, removedText);
    }

    @Override
    public String getDescription() {
        return "Delete [" + start + ", " + end + ")";
    }
}
