package dev.the456gamer.cmistaffchat;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

import com.Zrips.CMI.CMI;
import dev.the456gamer.cmistaffchat.cmi.staffmsg;
import dev.the456gamer.cmistaffchat.handlers.ConsoleCommandMsgHandler;
import dev.the456gamer.cmistaffchat.handlers.ConsoleMsgHandler;
import dev.the456gamer.cmistaffchat.handlers.DiscordSrvMsgHandler;
import dev.the456gamer.cmistaffchat.handlers.DiscordWebhookMsgHandler;
import dev.the456gamer.cmistaffchat.handlers.LogFileMsgHandler;
import dev.the456gamer.cmistaffchat.handlers.MsgHandler;
import dev.the456gamer.cmistaffchat.handlers.SlackWebhookMsgHandler;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.text.StringSubstitutor;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class StaffChatPlugin extends JavaPlugin {

  private ArrayList<MsgHandler> handlers;

  @Override
  public void onEnable() {
    CMI cmiPlugin = (CMI) getServer().getPluginManager().getPlugin("CMI");
    if (cmiPlugin == null || !cmiPlugin.isEnabled()) {
      getLogger().severe("CMI not found or not enabled. Disabling");
      setEnabled(false);
      return;
    }

    String cmiVersion = cmiPlugin.getPluginMeta().getVersion();
    getLogger().info("CMI found v:" + cmiVersion);

    cmiPlugin.getCommandManager().getCommands()
        .get(com.Zrips.CMI.commands.list.staffmsg.class.getSimpleName())
        .setCmdClass(new staffmsg());
    getLogger().info("replaced staffmsg command handler with passthrough");

    handlers = new ArrayList<>();

    loadConfig();

  }

  public static boolean usePlugin(String pluginName) {
    Plugin plugin = getInstance().getServer().getPluginManager().getPlugin(pluginName);
    if (plugin == null) {
      return false;
    }
    return plugin.isEnabled();
  }


  public ArrayList<MsgHandler> getMsgHandlers() {
    return handlers;
  }

  public static StaffChatPlugin getInstance() {
    return StaffChatPlugin.getPlugin(StaffChatPlugin.class);
  }

  @Override
  public void onDisable() {
  }

  public void loadConfig() {
    this.saveDefaultConfig();
    reloadConfig();
    FileConfiguration config = getConfig();
    List<Map<?, ?>> msgHandlers = config.getMapList("handlers");
    handlers.clear();
    for (Map<?, ?> msgHandler : msgHandlers) {
      switch (getStringValue(msgHandler, "type").toLowerCase()) {
        case "consolemsg" -> handlers.add(new ConsoleMsgHandler(getStringValue(msgHandler, "format",
            "staffmsg: from:'${sendername}' msg:'${message}'")));
        case "logfile" -> handlers.add(new LogFileMsgHandler(
            getStringValue(msgHandler, "logline-format",
                "${isotime} - ${senderuuid} \"${sendername}\": \"${message}\""),
            getStringValue(msgHandler, "filename-format", "staffmsglog.txt")));
        case "discordwebhook" -> handlers.add(
            new DiscordWebhookMsgHandler(getStringValue(msgHandler, "webhook-url"),
                getStringValue(msgHandler, "msg-format",
                    "StaffMsg:\nfrom:'${sendername}'\nmsg:'${message}'"),
                getStringValue(msgHandler, "name-format", "${sendername}"),
                getStringValue(msgHandler, "avatarurl-format",
                    "https://mc-heads.net/head/${sendername}/256")));
        case "slackwebhook" -> handlers.add(
            new SlackWebhookMsgHandler(getStringValue(msgHandler, "webhook-url"),
                getStringValue(msgHandler, "msg-format",
                    "StaffMsg:\nfrom:'${sendername}'\nmsg:'${message}'")));
        case "consolecommand" ->
            handlers.add(new ConsoleCommandMsgHandler(getStringValue(msgHandler, "command")));
        case "discordsrv" -> {
          if (usePlugin("DiscordSRV")) {
            handlers.add(new DiscordSrvMsgHandler(getStringValue(msgHandler, "format",
                "StaffMsg:\nfrom:'${sendername}'\nmsg:'${message}'"),
                getStringValue(msgHandler, "channel", "456staffchat")));
          } else {
            getInstance().getLogger().warning("Can't use DiscordSRV handler: Plugin not enabled");
          }
        }
        default ->
            getLogger().warning("unknown msghandler ".concat((String) msgHandler.get("type")));
      }
    }
  }


  public static String getStringValue(Map<?, ?> object, String key) {
    return getStringValue(object, key, null);
  }

  public static String getStringValue(Map<?, ?> object, String key, String def) {
    Object obj = object.get(key);
    return (obj == null ? def : (String) obj);
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
      @NotNull String label, @NotNull String[] args) {
    if (command.getName().equals("cmistaffchatadditionsreload")) {
      sender.sendMessage(text("Reloading Config!").color(RED));
      loadConfig();
      return true;
    }
    return false;
  }


  public static String formatString(String fmt, CommandSender sender, String staffmsg) {
    if (fmt == null) {
      return null;
    }

    Map<String, String> replacements = new HashMap<>();

    UUID uuid;
    if (sender instanceof Entity) {
      uuid = ((Entity) sender).getUniqueId();
    } else if (sender instanceof BlockCommandSender) {
      // this codepath will probably never be called, but its there incase it is
      String uuidSrcString = "BLOCK:".concat(((BlockCommandSender) sender).getBlock().toString());
      uuid = UUID.nameUUIDFromBytes(uuidSrcString.getBytes(StandardCharsets.UTF_8));
    } else if (sender instanceof ConsoleCommandSender) {
      String uuidSrcString = "CONSOLE";
      uuid = UUID.nameUUIDFromBytes(uuidSrcString.getBytes(StandardCharsets.UTF_8));
    } else if (sender instanceof RemoteConsoleCommandSender) {
      String uuidSrcString = "REMOTE";
      uuid = UUID.nameUUIDFromBytes(uuidSrcString.getBytes(StandardCharsets.UTF_8));
    } else {
      uuid = UUID.randomUUID();
    }

    replacements.put("sendername", sender.getName()); // CONSOLE if by console
    replacements.put("senderuuid", uuid.toString());
    replacements.put("message", staffmsg);

    replacements.put("isotime", ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)
        .format(DateTimeFormatter.ISO_INSTANT));

    StringSubstitutor substitutor = new StringSubstitutor(replacements);
    return substitutor.replace(fmt);
  }


}
