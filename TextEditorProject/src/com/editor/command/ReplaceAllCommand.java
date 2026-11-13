package com.editor.command;

import com.editor.model.Document;
import com.editor.model.DocumentMemento;

/**
 * Find and replace. Many edits happen at once, so instead of computing an inverse
 * we take a MEMENTO (snapshot) before executing and restore it on undo.
 */
public class ReplaceAllCommand extends AbstractCommand {
    private final String target;
    private final String replacement;
    private DocumentMemento before;
    private int count;

    public ReplaceAllCommand(Document document, String target, String replacement) {
        super(document);
        this.target = target;
        this.replacement = replacement;
    }

    @Override
    public void execute() {
        before = document.save();
        count = document.replaceAll(target, replacement);
    }

    @Override
    public void undo() {
        document.restore(before);
    }

    public int getCount() {
        return count;
    }

    @Override
    public String getDescription() {
        return "Replace all \"" + target + "\" with \"" + replacement + "\"";
    }
}
