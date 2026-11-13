package com.editor;

import com.editor.core.Editor;
import com.editor.ui.ConsoleUI;

public class Main {
    public static void main(String[] args) {
        new ConsoleUI(new Editor()).run();
    }
}
