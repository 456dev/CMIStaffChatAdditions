package dev.the456gamer.cmistaffchat.handlers;

import dev.the456gamer.cmistaffchat.StaffChatPlugin;
import org.bukkit.command.CommandSender;

public class ConsoleMsgHandler implements MsgHandler {

  String fmt = "InterceptedStaffMsg: from:'%1$s' msg:'%2$s'";

  public ConsoleMsgHandler(String fmtstr) {
    if (fmtstr != null) {
      this.fmt = fmtstr;
    }
  }

  @Override
  public boolean handle(CommandSender sender, String msg) {

    String fmtMsg = StaffChatPlugin.formatString(fmt, sender, msg);
    StaffChatPlugin.getInstance().getLogger().info(fmtMsg);

    return true;
  }
}
