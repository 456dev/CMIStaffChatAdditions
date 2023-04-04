package dev.the456gamer.cmistaffchat.handlers;

import dev.the456gamer.cmistaffchat.StaffChatPlugin;
import org.bukkit.command.CommandSender;

public class ConsoleCommandMsgHandler implements MsgHandler {

  String cmdformat;

  public ConsoleCommandMsgHandler(String cmdformat) {
    this.cmdformat = cmdformat;
  }


  @Override
  public boolean handle(CommandSender sender, String msg) {
    String formattedCommand = StaffChatPlugin.formatString(cmdformat, sender, msg);
    StaffChatPlugin.getInstance().getServer()
        .dispatchCommand(StaffChatPlugin.getInstance().getServer().getConsoleSender(),
            formattedCommand);
    return true;
  }
}
