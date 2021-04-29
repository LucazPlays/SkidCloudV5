package de.terrarier.logging;

import org.fusesource.jansi.Ansi;

public enum Color {

    BLACK(Ansi.ansi().reset().fg(Ansi.Color.BLACK).boldOff().toString()),
    DARK_BLUE(Ansi.ansi().reset().fg(Ansi.Color.BLUE).boldOff().toString()),
    DARK_GREEN(Ansi.ansi().reset().fg(Ansi.Color.GREEN).boldOff().toString()),
    DARK_AQUA(Ansi.ansi().reset().fg(Ansi.Color.CYAN).boldOff().toString()),
    DARK_RED( Ansi.ansi().reset().fg(Ansi.Color.RED).boldOff().toString()),
    DARK_PURPLE( Ansi.ansi().reset().fg(Ansi.Color.MAGENTA).boldOff().toString()),
    GOLD(Ansi.ansi().reset().fg(Ansi.Color.YELLOW).boldOff().toString()),
    GRAY(Ansi.ansi().reset().fg(Ansi.Color.WHITE).boldOff().toString()),
    DARK_GRAY(Ansi.ansi().reset().fg(Ansi.Color.BLACK).bold().toString()),
    BLUE(Ansi.ansi().reset().fg(Ansi.Color.BLUE).bold().toString()),
    GREEN(Ansi.ansi().reset().fg(Ansi.Color.GREEN).bold().toString()),
    AQUA(Ansi.ansi().reset().fg(Ansi.Color.CYAN).bold().toString()),
    RED(Ansi.ansi().reset().fg(Ansi.Color.RED).bold().toString()),
    LIGHT_PURPLE(Ansi.ansi().reset().fg(Ansi.Color.MAGENTA).bold().toString()),
    YELLOW(Ansi.ansi().reset().fg(Ansi.Color.YELLOW).bold().toString()),
    WHITE(Ansi.ansi().reset().fg(Ansi.Color.WHITE).bold().toString()),

    MAGIC(Ansi.ansi().a(Ansi.Attribute.BLINK_SLOW).toString()),
    BOLD(Ansi.ansi().a(Ansi.Attribute.UNDERLINE_DOUBLE).toString()),
    STRIKETHROUGH(Ansi.ansi().a(Ansi.Attribute.STRIKETHROUGH_ON).toString()),
    UNDERLINE(Ansi.ansi().a(Ansi.Attribute.UNDERLINE).toString()),
    ITALIC(Ansi.ansi().a(Ansi.Attribute.ITALIC).toString()),
    RESET(Ansi.ansi().reset().toString());

    private final String ansiCode;

    Color(String ansiCode) {
        this.ansiCode = ansiCode;
    }

    public String toString() {
        return ansiCode;
    }

}
