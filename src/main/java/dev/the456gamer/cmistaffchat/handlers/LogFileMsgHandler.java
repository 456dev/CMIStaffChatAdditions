package dev.the456gamer.cmistaffchat.handlers;

import dev.the456gamer.cmistaffchat.StaffChatPlugin;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.bukkit.command.CommandSender;

public class LogFileMsgHandler implements MsgHandler {

  String lineFormat, filenameFormat;

  public LogFileMsgHandler(String lineFormat, String filenameFormat) {
    this.lineFormat = lineFormat;
    this.filenameFormat = filenameFormat;
  }

  @Override
  public boolean handle(CommandSender sender, String msg) {

    File file = new File(StaffChatPlugin.getInstance().getDataFolder(),
        StaffChatPlugin.formatString(filenameFormat, sender, msg));
    try (FileWriter fw = new FileWriter(file, true); BufferedWriter bw = new BufferedWriter(
        fw); PrintWriter out = new PrintWriter(bw)) {
      out.println(StaffChatPlugin.formatString(lineFormat, sender, msg));
    } catch (IOException e) {
      //exception handling left as an exercise for the reader
      StaffChatPlugin.getInstance().getLogger().warning("unable to write staffmsg to file");
    }

    return true;
  }
}
