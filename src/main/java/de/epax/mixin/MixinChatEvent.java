package de.epax.mixin;

import de.epax.commands.EpaxClientState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinChatEvent {

    @Inject(method = "onChatMessage", at = @At("HEAD"))
    public void onChatMessage(ChatMessageS2CPacket packet, CallbackInfo info) {
        MinecraftClient client = MinecraftClient.getInstance();

        // Get the message from the packet
        String message = packet.toString();

        if (message.startsWith("epaxclient")) {
            processChatMessage(client, message);
        }

        // Send the chat message to the player
        Text chatText = Text.of(message);
        client.player.sendMessage(chatText, false);
    }

    private void processChatMessage(MinecraftClient client, String message) {
        String[] parts = message.split(" ");
        if (parts.length > 2) {
            String command = parts[1].toLowerCase();
            boolean value = Boolean.parseBoolean(parts[2]);

            if (command.equals("autocrit")) {
                EpaxClientState.autocrit = value;
                client.player.sendMessage(Text.of("AutoCrit set to: " + value), false);
            } else if (command.equals("fly")) {
                EpaxClientState.fly = value;
                client.player.sendMessage(Text.of("Fly set to: " + value), false);
            }
        }
    }
}