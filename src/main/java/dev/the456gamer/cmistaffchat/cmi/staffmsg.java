package dev.the456gamer.cmistaffchat.cmi;

import com.Zrips.CMI.CMI;
import dev.the456gamer.cmistaffchat.StaffChatPlugin;
import dev.the456gamer.cmistaffchat.handlers.MsgHandler;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class staffmsg implements com.Zrips.CMI.commands.Cmd {

  com.Zrips.CMI.commands.list.staffmsg orig;

  public staffmsg() {
    orig = new com.Zrips.CMI.commands.list.staffmsg();
  }

  // this is all that is needed to handle persistent (toggled) messages as well,
  // as the handler for that (in com.Zrips.CMI.Modules.ChatFormat.ChatFormatListener)
  // runs this method through the normal command handler anyway
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] args) {
    String strArgs = String.join(" ", args);

    // CMI has special cases for [on, off, toggle] when the sender is a player, resulting in no staff messages sent
    if (!((commandSender instanceof Player) && (strArgs.equalsIgnoreCase("on")
        || strArgs.equalsIgnoreCase("off") || strArgs.equalsIgnoreCase("toggle") || strArgs.equalsIgnoreCase("")))) {
      // otherwise, go over the handlers
      for (MsgHandler handler : StaffChatPlugin.getInstance().getMsgHandlers()) {
        try {
          handler.handle(commandSender, strArgs);
        } catch (Exception e) {
          StaffChatPlugin.getInstance().getLogger()
              .severe(
                  "Unhandled exception in StaffMsg handler ".concat(handler.getClass().getName()));
          e.printStackTrace();
        }
      }
    }

    // Always pass it back to CMI
    return orig.perform(cmi, commandSender, args);
  }

  @Override
  public void getExtra(ConfigReader configReader) {
    orig.getExtra(configReader);
  }
}
