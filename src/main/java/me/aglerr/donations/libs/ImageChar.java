package me.aglerr.donations.libs;

public enum ImageChar {
    BLOCK('\u2588'),
    DARK_SHADE('\u2593'),
    MEDIUM_SHADE('\u2592'),
    LIGHT_SHADE('\u2591');

    private final char c;

    ImageChar(char Char) {
        this.c = Char;
    }

    public char getChar() {
        return this.c;
    }
}
