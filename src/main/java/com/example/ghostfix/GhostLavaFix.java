package com.example.ghostfix;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.block.BlockLiquid;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import org.lwjgl.input.Keyboard;

@Mod(modid = GhostLavaFix.MODID, version = GhostLavaFix.VERSION, clientSideOnly = true)
public class GhostLavaFix {
    public static final String MODID = "ghostlavafix";
    public static final String VERSION = "1.0";

    private static KeyBinding ghostKeyBinding;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);

        ghostKeyBinding = new KeyBinding("key.ghostlavafix.sync", Keyboard.KEY_G, "Ghost Lava Fix Mod");
        ClientRegistry.registerKeyBinding(ghostKeyBinding);

        net.minecraftforge.client.ClientCommandHandler.instance.registerCommand(new GhostCommand());
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (ghostKeyBinding.isPressed()) {
            clearGhostBlocksAroundPlayer(6);
            Minecraft.getMinecraft().thePlayer.addChatMessage(
                    new ChatComponentText(EnumChatFormatting.GREEN + "[GhostLavaFix] Synced ghost lava and water around you.")
            );
        }
    }

    public static void clearGhostBlocksAroundPlayer(int radius) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.theWorld == null) return;

        BlockPos playerPos = new BlockPos(mc.thePlayer);

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = playerPos.add(x, y, z);

                    if (mc.theWorld.getBlockState(pos).getBlock() instanceof BlockLiquid) {
                        mc.thePlayer.sendQueue.addToSendQueue(
                                new net.minecraft.network.play.client.C07PacketPlayerDigging(
                                        net.minecraft.network.play.client.C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK,
                                        pos,
                                        net.minecraft.util.EnumFacing.DOWN
                                )
                        );
                    }
                }
            }
        }
    }

    public static class GhostCommand extends CommandBase {
        @Override
        public String getCommandName() { return "ghostlava"; }

        @Override
        public String getCommandUsage(ICommandSender sender) { return "/ghostlava"; }

        @Override
        public int getRequiredPermissionLevel() { return 0; }

        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            clearGhostBlocksAroundPlayer(6);
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "[GhostLavaFix] Ghost lava/water synchronized."));
        }
    }
}