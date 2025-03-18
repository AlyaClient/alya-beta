package dev.thoq.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

import java.util.ArrayList;
import java.util.List;

public class CommandRepository {
    private static final List<AbstractCommand> commands = new ArrayList<>();

    public static void registerCommand(AbstractCommand command) {
        commands.add(command);
    }

    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        for (AbstractCommand command : commands) {
            command.register(dispatcher);
        }
    }
}
