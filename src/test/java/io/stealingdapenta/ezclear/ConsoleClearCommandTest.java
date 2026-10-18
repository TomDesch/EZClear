package io.stealingdapenta.ezclear;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.junit.jupiter.api.Test;

class ConsoleClearCommandTest {

    @Test
    void onCommandClearsConsoleWhenSenderIsConsole() {
        List<String> messages = new ArrayList<>();
        ConsoleCommandSender sender = consoleSender(messages);

        boolean handled = new ConsoleClearCommand().onCommand(sender, null, "consoleclear", new String[0]);

        assertTrue(handled);
        assertEquals("\u001B[H\u001B[2J\u001B[3J", messages.get(0));
        assertTrue(messages.stream().anyMatch(message -> message.contains("Clearing console at local time")));
        assertEquals(4, messages.size());
    }

    @Test
    void onCommandRejectsNonConsoleSender() {
        List<String> messages = new ArrayList<>();
        CommandSender sender = commandSender(messages);

        boolean handled = new ConsoleClearCommand().onCommand(sender, null, "consoleclear", new String[0]);

        assertTrue(handled);
        assertEquals(1, messages.size());
        assertTrue(messages.get(0).contains("You can only execute this command directly in the console."));
    }

    private static ConsoleCommandSender consoleSender(List<String> messages) {
        return (ConsoleCommandSender) Proxy.newProxyInstance(
                ConsoleCommandSender.class.getClassLoader(),
                new Class<?>[] {ConsoleCommandSender.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("sendMessage")) {
                        if (args != null && args.length > 0) {
                            if (args[0] instanceof String string) {
                                messages.add(string);
                            } else if (args[0] instanceof String[] strings) {
                                for (String message : strings) {
                                    messages.add(message);
                                }
                            }
                        }
                        return null;
                    }
                    if (method.getName().equals("getName")) {
                        return "Console";
                    }
                    if (method.getName().equals("isOp")) {
                        return false;
                    }
                    if (method.getName().equals("setOp")) {
                        return null;
                    }
                    if (method.getName().equals("hasPermission")) {
                        return true;
                    }
                    if (method.getName().equals("isPermissionSet")) {
                        return true;
                    }
                    if (method.getName().equals("spigot")) {
                        return null;
                    }
                    return defaultValue(method.getReturnType());
                });
    }

    private static CommandSender commandSender(List<String> messages) {
        return (CommandSender) Proxy.newProxyInstance(
                CommandSender.class.getClassLoader(),
                new Class<?>[] {CommandSender.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("sendMessage")) {
                        if (args != null && args.length > 0) {
                            if (args[0] instanceof String string) {
                                messages.add(string);
                            } else if (args[0] instanceof String[] strings) {
                                for (String message : strings) {
                                    messages.add(message);
                                }
                            }
                        }
                        return null;
                    }
                    if (method.getName().equals("getName")) {
                        return "Player";
                    }
                    if (method.getName().equals("isOp")) {
                        return false;
                    }
                    if (method.getName().equals("setOp")) {
                        return null;
                    }
                    if (method.getName().equals("hasPermission")) {
                        return true;
                    }
                    if (method.getName().equals("isPermissionSet")) {
                        return true;
                    }
                    if (method.getName().equals("spigot")) {
                        return null;
                    }
                    return defaultValue(method.getReturnType());
                });
    }

    private static Object defaultValue(Class<?> type) {
        if (type == boolean.class) {
            return false;
        }
        if (type == byte.class) {
            return (byte) 0;
        }
        if (type == short.class) {
            return (short) 0;
        }
        if (type == int.class) {
            return 0;
        }
        if (type == long.class) {
            return 0L;
        }
        if (type == float.class) {
            return 0f;
        }
        if (type == double.class) {
            return 0d;
        }
        if (type == char.class) {
            return '\0';
        }
        return null;
    }
}
