package dev.the456gamer.cmistaffchat.handlers;

import org.bukkit.command.CommandSender;

public interface MsgHandler {

  boolean handle(CommandSender sender, String msg);
}
