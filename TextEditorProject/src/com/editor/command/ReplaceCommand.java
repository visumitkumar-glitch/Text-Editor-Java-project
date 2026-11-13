package com.editor.command;

import com.editor.model.Document;

public class ReplaceCommand extends AbstractCommand {
    private final int start;
    private final int end;
    private final String newText;
    private String oldText;

    public ReplaceCommand(Document document, int start, int end, String newText) {
        super(document);
        this.start = start;
        this.end = end;
        this.newText = newText;
    }

    @Override
    public void execute() {
        oldText = document.delete(start, end);
        document.insert(start, newText);
    }

    @Override
    public void undo() {
        document.delete(start, start + newText.length());
        document.insert(start, oldText);
    }

    @Override
    public String getDescription() {
        return "Replace [" + start + ", " + end + ") with \"" + newText + "\"";
    }
}
