package com.editor.model;

import com.editor.exception.InvalidEditException;

import java.util.ArrayList;
import java.util.List;

/**
 * The text being edited (the "Originator" in the Memento pattern).
 * Its content is private; the only way to change it is through the
 * validated methods below, which is encapsulation in action.
 */
public class Document {
    private final StringBuilder content = new StringBuilder();

    public void insert(int position, String text) {
        if (text == null || text.isEmpty()) {
            throw new InvalidEditException("Nothing to insert.");
        }
        if (position < 0 || position > content.length()) {
            throw new InvalidEditException(
                    "Position " + position + " is out of range (0-" + content.length() + ").");
        }
        content.insert(position, text);
    }

    /** Deletes [start, end) and returns the removed text. */
    public String delete(int start, int end) {
        if (start < 0 || end > content.length() || start >= end) {
            throw new InvalidEditException(
                    "Invalid range [" + start + ", " + end + ") for length " + content.length() + ".");
        }
        String removed = content.substring(start, end);
        content.delete(start, end);
        return removed;
    }

    /** Replaces every occurrence of target and returns how many were replaced. */
    public int replaceAll(String target, String replacement) {
        if (target == null || target.isEmpty()) {
            throw new InvalidEditException("Search text cannot be empty.");
        }
        List<Integer> hits = findAll(target);
        if (hits.isEmpty()) {
            return 0;
        }
        String updated = content.toString().replace(target, replacement);
        content.setLength(0);
        content.append(updated);
        return hits.size();
    }

    /** Returns the start index of every non-overlapping occurrence of text. */
    public List<Integer> findAll(String text) {
        List<Integer> positions = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return positions;
        }
        int index = content.indexOf(text);
        while (index >= 0) {
            positions.add(index);
            index = content.indexOf(text, index + text.length());
        }
        return positions;
    }

    public int length() {
        return content.length();
    }

    public String getText() {
        return content.toString();
    }

    // ---- Memento pattern ----

    public DocumentMemento save() {
        return new DocumentMemento(content.toString());
    }

    public void restore(DocumentMemento memento) {
        content.setLength(0);
        content.append(memento.getState());
    }
}
