package de.epax;

import com.mojang.brigadier.Command;
import de.epax.commands.EpaxClientCommands;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Epaxhackclient implements ClientModInitializer {
	public static final String MOD_ID = "epax-hack-client";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		Command<ClientCommandManager> command = context -> {
			return 0;
		};
		EpaxClientCommands.register();
		LOGGER.info("Epax Hack Client Mod ist geladen und bereit!");
	}
}