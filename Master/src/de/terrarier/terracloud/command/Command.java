package de.terrarier.terracloud.command;

public abstract class Command {

    private final String name;
    private final String description;
    private final String usage;
    private final String[] aliases;

    public Command(String name, String description, String usage, String... aliases) {
        this.name = name;
        this.description = description;
        this.usage = usage;
        this.aliases = aliases;
    }

    public abstract void execute(String[] args);

    public final String getName() {
        return name;
    }

    public final String getDescription() {
        return description;
    }

    public String getUsage() {
        return usage;
    }

    public final String[] getAliases() {
        return aliases;
    }

}
