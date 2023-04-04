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

public class DiscordWebhookMsgHandler implements MsgHandler {

  String url, msgFormat, nameFormat, avatarUrlFormat;


  public DiscordWebhookMsgHandler(String webhookUrl, String msgFormat, String nameFormat,
      String avatarUrlFormat) {
    this.url = webhookUrl;
    this.msgFormat = msgFormat;
    this.nameFormat = nameFormat;
    this.avatarUrlFormat = avatarUrlFormat;

  }

  @Override
  public boolean handle(CommandSender sender, String msg) {
    String formattedMsg = StaffChatPlugin.formatString(msgFormat, sender, msg);
    String formattedName = StaffChatPlugin.formatString(nameFormat, sender, msg);
    String formattedAvatarUrl = StaffChatPlugin.formatString(avatarUrlFormat, sender, msg);
    StaffChatPlugin.getInstance().getServer().getScheduler()
        .runTaskAsynchronously(StaffChatPlugin.getInstance(),
            () -> DiscordWebhookMsgHandler.webhookExecute(url, formattedMsg, formattedName,
                formattedAvatarUrl));

    return true;
  }

  public static boolean webhookExecute(String strurl, String content, String username,
      String avatarUrl) {
    JSONObject json = new JSONObject();
    json.put("content", content);
    if (username != null) {
      json.put("username", username);
    }
    if (avatarUrl != null) {
      json.put("avatar_url", avatarUrl);
    }

    URL url;
    try {
      url = new URL(strurl);
    } catch (MalformedURLException e) {
      e.printStackTrace();
      StaffChatPlugin.getInstance().getLogger().warning("Could not send staff message to discord!");
      return false;
    }
    HttpsURLConnection connection;
    try {
      connection = (HttpsURLConnection) url.openConnection();
    } catch (IOException e) {
      e.printStackTrace();
      StaffChatPlugin.getInstance().getLogger().warning("Could not send staff message to discord!");
      return false;
    }
    connection.addRequestProperty("Content-Type", "application/json");
    connection.setDoOutput(true);
    try {
      connection.setRequestMethod("POST");
    } catch (ProtocolException e) {
      e.printStackTrace();
      StaffChatPlugin.getInstance().getLogger().warning("Could not send staff message to discord!");
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
      StaffChatPlugin.getInstance().getLogger().warning("Could not send staff message to discord!");
    }

    try {
      connection.getInputStream().readAllBytes();
    } catch (IOException e) {
      e.printStackTrace();
      StaffChatPlugin.getInstance().getLogger().warning("Could not send staff message to discord!");
      return false;
    }
    connection.disconnect();
    return true;

  }


}
