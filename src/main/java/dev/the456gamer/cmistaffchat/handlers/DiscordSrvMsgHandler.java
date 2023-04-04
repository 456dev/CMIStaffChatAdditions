package dev.the456gamer.cmistaffchat.handlers;

import dev.the456gamer.cmistaffchat.StaffChatPlugin;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;
import org.bukkit.command.CommandSender;

public class DiscordSrvMsgHandler implements MsgHandler {

  String formatString, targetChannel;

  public DiscordSrvMsgHandler(String formatString, String targetChannel) {

    this.formatString = formatString;
    this.targetChannel = targetChannel;

  }

  @Override
  public boolean handle(CommandSender sender, String msg) {

    String fmtMsg = StaffChatPlugin.formatString(formatString, sender, msg);

    TextChannel channel = DiscordSRV.getPlugin()
        .getDestinationTextChannelForGameChannelName(targetChannel);
    DiscordUtil.queueMessage(channel, fmtMsg);

    return true;
  }
}
