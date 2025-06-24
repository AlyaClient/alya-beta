/*
 * Copyright (c) Rye 2025-2025.
 *
 * This file belongs to Rye Client,
 * an open-source Fabric Injection client.
 * Rye GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * This project (and subsequently, its files) are all licensed under the MIT License.
 * This project should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 */

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
