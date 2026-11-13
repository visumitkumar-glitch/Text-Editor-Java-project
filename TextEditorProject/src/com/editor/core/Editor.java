package com.editor.core;

import com.editor.command.Command;
import com.editor.command.MacroCommand;
import com.editor.exception.InvalidEditException;
import com.editor.history.History;
import com.editor.model.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Facade the UI talks to. Owns the Document and the History and adds two extras:
 *  - undo grouping: several edits become ONE undo step
 *  - macros: record a sequence of edits and replay it later (also one undo step)
 */
public class Editor {
    private final Document document = new Document();
    private final History history = new History();
    private final Map<String, List<Command>> macros = new HashMap<>();

    private List<Command> groupBuffer;   // null when not grouping
    private List<Command> macroBuffer;   // null when not recording
    private String recordingName;

    /** Runs a command and files it in history (or in the open group). */
    public void execute(Command command) {
        command.execute();
        if (groupBuffer != null) {
            groupBuffer.add(command);
        } else {
            history.push(command);
        }
        if (macroBuffer != null) {
            macroBuffer.add(command);
        }
    }

    // ---- undo / redo ----

    public int undo(int steps) {
        int done = 0;
        while (done < steps && history.canUndo()) {
            history.undo();
            done++;
        }
        return done;
    }

    public int redo(int steps) {
        int done = 0;
        while (done < steps && history.canRedo()) {
            history.redo();
            done++;
        }
        return done;
    }

    // ---- grouping ----

    public void beginGroup() {
        if (groupBuffer != null) {
            throw new InvalidEditException("A group is already open.");
        }
        groupBuffer = new ArrayList<>();
    }

    public int endGroup() {
        if (groupBuffer == null) {
            throw new InvalidEditException("No group is open.");
        }
        List<Command> finished = groupBuffer;
        groupBuffer = null;
        if (!finished.isEmpty()) {
            history.push(new MacroCommand("Group", finished));
        }
        return finished.size();
    }

    // ---- macros ----

    public void startRecording(String name) {
        if (macroBuffer != null) {
            throw new InvalidEditException("Already recording macro '" + recordingName + "'.");
        }
        macroBuffer = new ArrayList<>();
        recordingName = name;
    }

    public int stopRecording() {
        if (macroBuffer == null) {
            throw new InvalidEditException("Not recording a macro.");
        }
        macros.put(recordingName, macroBuffer);
        int size = macroBuffer.size();
        macroBuffer = null;
        recordingName = null;
        return size;
    }

    /** Replays a macro as a single undoable step. Positions are replayed exactly as recorded. */
    public void playMacro(String name) {
        List<Command> recorded = macros.get(name);
        if (recorded == null) {
            throw new InvalidEditException("No macro named '" + name + "'.");
        }
        MacroCommand macro = new MacroCommand("Macro '" + name + "'", recorded);
        execute(macro);
    }

    public boolean isRecording() {
        return macroBuffer != null;
    }

    public boolean isGrouping() {
        return groupBuffer != null;
    }

    public Document getDocument() {
        return document;
    }

    public History getHistory() {
        return history;
    }

    public List<String> getMacroNames() {
        return new ArrayList<>(macros.keySet());
    }
}
