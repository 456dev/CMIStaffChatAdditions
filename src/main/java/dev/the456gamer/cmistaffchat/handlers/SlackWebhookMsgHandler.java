package dev.the456gamer.cmistaffchat.handlers;

import dev.the456gamer.cmistaffchat.StaffChatPlugin;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import org.bukkit.command.CommandSender;
import org.json.simple.JSONObject;

public class SlackWebhookMsgHandler implements MsgHandler {

  String url, messageFormat;

  public SlackWebhookMsgHandler(String webhookUrl, String messageFormat) {
    // todo add name formatting
    this.url = webhookUrl;
    this.messageFormat = messageFormat;
  }

  @Override
  public boolean handle(CommandSender sender, String msg) {
    String formattedMsg = StaffChatPlugin.formatString(messageFormat, sender, msg);

    StaffChatPlugin.getInstance().getServer().getScheduler()
        .runTaskAsynchronously(StaffChatPlugin.getInstance(),
            () -> webhookExecute(url, formattedMsg));

    return true;
  }

  public static boolean webhookExecute(String strurl, String text) {
    JSONObject json = new JSONObject();
    json.put("text", text);

    URL url;
    try {
      url = new URL(strurl);
    } catch (MalformedURLException e) {
      e.printStackTrace();
      StaffChatPlugin.getInstance().getLogger().warning("Could not send staff message to slack!");
      return false;
    }
    HttpsURLConnection connection;
    try {
      connection = (HttpsURLConnection) url.openConnection();
    } catch (IOException e) {
      e.printStackTrace();
      StaffChatPlugin.getInstance().getLogger().warning("Could not send staff message to slack!");
      return false;
    }
    connection.addRequestProperty("Content-Type", "application/json");
    connection.setDoOutput(true);
    try {
      connection.setRequestMethod("POST");
    } catch (ProtocolException e) {
      e.printStackTrace();
      StaffChatPlugin.getInstance().getLogger().warning("Could not send staff message to slack!");
      return false;
    }

    OutputStream stream;
    try {
      stream = connection.getOutputStream();
      stream.write(json.toString().getBytes());
      stream.flush();
      stream.close();
    } catch (IOException e) {
      e.printStackTrace();
      StaffChatPlugin.getInstance().getLogger().warning("Could not send staff message to slack!");
    }

    try {
      connection.getInputStream().readAllBytes();
    } catch (IOException e) {
      e.printStackTrace();
      StaffChatPlugin.getInstance().getLogger().warning("Could not send staff message to slack!");
      return false;
    }
    connection.disconnect();
    return true;

  }

}
