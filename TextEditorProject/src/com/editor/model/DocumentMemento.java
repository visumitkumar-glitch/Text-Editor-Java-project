package com.editor.model;

/**
 * MEMENTO pattern: an immutable snapshot of a Document's state.
 * Only the model package can read the stored state (package-private),
 * so outside code can hold a snapshot but never peek inside or tamper with it.
 */
public final class DocumentMemento {
    private final String state;

    DocumentMemento(String state) {
        this.state = state;
    }

    String getState() {
        return state;
    }
}
