package dev.thoq.command;

public class CommandBuilder {
    public static CommandBuilder create() {
        return new CommandBuilder();
    }

    public void putAll(AbstractCommand... commands) {
        for(AbstractCommand command : commands) {
            CommandRepository.registerCommand(command);
        }
    }
}
