package com.editor;

import com.editor.command.DeleteCommand;
import com.editor.command.InsertCommand;
import com.editor.command.ReplaceAllCommand;
import com.editor.command.ReplaceCommand;
import com.editor.core.Editor;
import com.editor.exception.InvalidEditException;
import com.editor.model.Document;

/** Tiny dependency-free test runner (no JUnit needed). Run: java -cp out com.editor.SelfTest */
public class SelfTest {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        insertAndUndo();
        deleteAndUndo();
        replaceAndUndo();
        replaceAllUsesMemento();
        redoClearedByNewEdit();
        groupIsOneStep();
        macroReplay();
        invalidEditsRejected();
        System.out.println("\n" + passed + " passed, " + failed + " failed");
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void insertAndUndo() {
        Editor e = new Editor();
        Document d = e.getDocument();
        e.execute(new InsertCommand(d, 0, "Hello"));
        e.execute(new InsertCommand(d, 5, " World"));
        check("insert builds text", "Hello World", d.getText());
        e.undo(1);
        check("undo insert", "Hello", d.getText());
        e.redo(1);
        check("redo insert", "Hello World", d.getText());
    }

    private static void deleteAndUndo() {
        Editor e = new Editor();
        Document d = e.getDocument();
        e.execute(new InsertCommand(d, 0, "Hello World"));
        e.execute(new DeleteCommand(d, 5, 11));
        check("delete", "Hello", d.getText());
        e.undo(1);
        check("undo delete", "Hello World", d.getText());
    }

    private static void replaceAndUndo() {
        Editor e = new Editor();
        Document d = e.getDocument();
        e.execute(new InsertCommand(d, 0, "Hello World"));
        e.execute(new ReplaceCommand(d, 6, 11, "Java"));
        check("replace", "Hello Java", d.getText());
        e.undo(1);
        check("undo replace", "Hello World", d.getText());
    }

    private static void replaceAllUsesMemento() {
        Editor e = new Editor();
        Document d = e.getDocument();
        e.execute(new InsertCommand(d, 0, "cat sat on cat mat"));
        e.execute(new ReplaceAllCommand(d, "cat", "dog"));
        check("replaceAll", "dog sat on dog mat", d.getText());
        e.undo(1);
        check("undo replaceAll (memento)", "cat sat on cat mat", d.getText());
    }

    private static void redoClearedByNewEdit() {
        Editor e = new Editor();
        Document d = e.getDocument();
        e.execute(new InsertCommand(d, 0, "A"));
        e.undo(1);
        e.execute(new InsertCommand(d, 0, "B"));
        check("redo cleared after new edit", 0, e.redo(1));
    }

    private static void groupIsOneStep() {
        Editor e = new Editor();
        Document d = e.getDocument();
        e.beginGroup();
        e.execute(new InsertCommand(d, 0, "One"));
        e.execute(new InsertCommand(d, 3, "Two"));
        e.execute(new InsertCommand(d, 6, "Three"));
        e.endGroup();
        check("group built text", "OneTwoThree", d.getText());
        e.undo(1);
        check("group undone in one step", "", d.getText());
        e.redo(1);
        check("group redone in one step", "OneTwoThree", d.getText());
    }

    private static void macroReplay() {
        Editor e = new Editor();
        Document d = e.getDocument();
        e.startRecording("sig");
        e.execute(new InsertCommand(d, 0, "--"));
        e.execute(new InsertCommand(d, 2, "end"));
        e.stopRecording();
        e.undo(2);
        check("document empty before replay", "", d.getText());
        e.playMacro("sig");
        check("macro replay", "--end", d.getText());
        e.undo(1);
        check("macro undone in one step", "", d.getText());
    }

    private static void invalidEditsRejected() {
        Document d = new Document();
        boolean threw = false;
        try {
            d.insert(5, "x");
        } catch (InvalidEditException ex) {
            threw = true;
        }
        check("bad position rejected", true, threw);
    }

    private static void check(String name, Object expected, Object actual) {
        if (expected.equals(actual)) {
            passed++;
            System.out.println("PASS  " + name);
        } else {
            failed++;
            System.out.println("FAIL  " + name + " (expected '" + expected + "' but got '" + actual + "')");
        }
    }
}
