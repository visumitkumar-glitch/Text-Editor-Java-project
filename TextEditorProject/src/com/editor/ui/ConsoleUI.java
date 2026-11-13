package com.editor.ui;

import com.editor.command.DeleteCommand;
import com.editor.command.InsertCommand;
import com.editor.command.ReplaceAllCommand;
import com.editor.command.ReplaceCommand;
import com.editor.core.Editor;
import com.editor.exception.InvalidEditException;

import java.util.List;
import java.util.Scanner;

/** Reads typed commands and forwards them to the Editor. Contains no editing logic itself. */
public class ConsoleUI {
    private final Editor editor;
    private final Scanner in = new Scanner(System.in);

    public ConsoleUI(Editor editor) {
        this.editor = editor;
    }

    public void run() {
        System.out.println("=== Mini Text Editor (type 'help' for commands) ===");
        while (true) {
            System.out.print("> ");
            if (!in.hasNextLine()) {
                break;
            }
            String line = in.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("quit")) {
                System.out.println("Bye!");
                break;
            }
            try {
                handle(line);
            } catch (InvalidEditException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Error: expected a number. Type 'help' for usage.");
            }
        }
    }

    private void handle(String line) {
        String[] p = line.split("\\s+", 2);
        String cmd = p[0].toLowerCase();
        String rest = p.length > 1 ? p[1] : "";

        switch (cmd) {
            case "help":
                printHelp();
                break;
            case "show":
                show();
                break;
            case "append": {
                int pos = editor.getDocument().length();
                editor.execute(new InsertCommand(editor.getDocument(), pos, rest));
                show();
                break;
            }
            case "insert": {
                String[] a = rest.split("\\s+", 2);
                need(a.length == 2, "insert <pos> <text>");
                editor.execute(new InsertCommand(editor.getDocument(), Integer.parseInt(a[0]), a[1]));
                show();
                break;
            }
            case "delete": {
                String[] a = rest.split("\\s+");
                need(a.length == 2, "delete <start> <end>");
                editor.execute(new DeleteCommand(editor.getDocument(),
                        Integer.parseInt(a[0]), Integer.parseInt(a[1])));
                show();
                break;
            }
            case "replace": {
                String[] a = rest.split("\\s+", 3);
                need(a.length == 3, "replace <start> <end> <text>");
                editor.execute(new ReplaceCommand(editor.getDocument(),
                        Integer.parseInt(a[0]), Integer.parseInt(a[1]), a[2]));
                show();
                break;
            }
            case "replaceall": {
                String[] a = rest.split("\\s+", 2);
                need(a.length == 2, "replaceall <find> <replacement>");
                ReplaceAllCommand r = new ReplaceAllCommand(editor.getDocument(), a[0], a[1]);
                editor.execute(r);
                System.out.println("Replaced " + r.getCount() + " occurrence(s).");
                show();
                break;
            }
            case "find": {
                need(!rest.isEmpty(), "find <text>");
                List<Integer> hits = editor.getDocument().findAll(rest);
                System.out.println(hits.isEmpty() ? "Not found." : "Found at positions: " + hits);
                break;
            }
            case "undo": {
                int n = rest.isEmpty() ? 1 : Integer.parseInt(rest.trim());
                int done = editor.undo(n);
                System.out.println("Undid " + done + " step(s).");
                show();
                break;
            }
            case "redo": {
                int n = rest.isEmpty() ? 1 : Integer.parseInt(rest.trim());
                int done = editor.redo(n);
                System.out.println("Redid " + done + " step(s).");
                show();
                break;
            }
            case "history":
                printHistory();
                break;
            case "group":
                handleGroup(rest.trim().toLowerCase());
                break;
            case "macro":
                handleMacro(rest.trim());
                break;
            default:
                System.out.println("Unknown command. Type 'help'.");
        }
    }

    private void handleGroup(String sub) {
        if (sub.equals("begin")) {
            editor.beginGroup();
            System.out.println("Group started: following edits will undo as ONE step.");
        } else if (sub.equals("end")) {
            int n = editor.endGroup();
            System.out.println("Group closed with " + n + " edit(s).");
        } else {
            System.out.println("Usage: group begin | group end");
        }
    }

    private void handleMacro(String rest) {
        String[] a = rest.split("\\s+", 2);
        switch (a[0].toLowerCase()) {
            case "record":
                need(a.length == 2, "macro record <name>");
                editor.startRecording(a[1].trim());
                System.out.println("Recording macro '" + a[1].trim() + "'...");
                break;
            case "stop":
                System.out.println("Macro saved with " + editor.stopRecording() + " edit(s).");
                break;
            case "play":
                need(a.length == 2, "macro play <name>");
                editor.playMacro(a[1].trim());
                show();
                break;
            case "list":
                System.out.println("Macros: " + editor.getMacroNames());
                break;
            default:
                System.out.println("Usage: macro record <name> | macro stop | macro play <name> | macro list");
        }
    }

    private void show() {
        String text = editor.getDocument().getText();
        System.out.println("[" + text.length() + " chars] \"" + text + "\"");
    }

    private void printHistory() {
        List<String> items = editor.getHistory().describeUndoStack();
        if (items.isEmpty()) {
            System.out.println("History is empty.");
            return;
        }
        System.out.println("Undo history (latest first), redo available: " + editor.getHistory().redoSize());
        for (int i = 0; i < items.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + items.get(i));
        }
    }

    private void need(boolean ok, String usage) {
        if (!ok) {
            throw new InvalidEditException("Usage: " + usage);
        }
    }

    private void printHelp() {
        System.out.println(
                "  show                         print the document\n"
              + "  append <text>                add text at the end\n"
              + "  insert <pos> <text>          insert text at a position\n"
              + "  delete <start> <end>         delete range [start, end)\n"
              + "  replace <start> <end> <text> replace a range with new text\n"
              + "  replaceall <find> <with>     find and replace everywhere\n"
              + "  find <text>                  list positions of text\n"
              + "  undo [n] / redo [n]          undo or redo n steps (default 1)\n"
              + "  history                      show the undo stack\n"
              + "  group begin | group end      make several edits one undo step\n"
              + "  macro record <name> | stop | play <name> | list\n"
              + "  exit");
    }
}
