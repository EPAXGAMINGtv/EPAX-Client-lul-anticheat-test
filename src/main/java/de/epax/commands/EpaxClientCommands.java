package de.epax.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class EpaxClientCommands {

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {

            dispatcher.register(literal("epaxclient")
                    .then(literal("autocrit")
                            .then(argument("value", BoolArgumentType.bool())
                                    .executes(context -> {
                                        boolean value = BoolArgumentType.getBool(context, "value");
                                        EpaxClientState.autocrit = value;

                                        MinecraftClient.getInstance().player.sendMessage(
                                                Text.literal("AutoCrit set to: " + value), false
                                        );
                                        return 1;
                                    })
                            )
                    )
                    .then(literal("fly")
                            .then(argument("value", BoolArgumentType.bool())
                                    .executes(context -> {
                                        boolean value = BoolArgumentType.getBool(context, "value");
                                        EpaxClientState.fly = value;

                                        MinecraftClient.getInstance().player.sendMessage(
                                                Text.literal("Fly set to: " + value), false
                                        );
                                        return 1;
                                    })
                            )
                    )
            );

        });
    }
}