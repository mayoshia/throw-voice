package tech.gdragon.commands.audio

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import tech.gdragon.BotUtils
import tech.gdragon.commands.CommandHandler
import tech.gdragon.commands.InvalidCommand
import tech.gdragon.i18n.Lang
import tech.gdragon.listener.CombinedAudioRecorderHandler

class Clip : CommandHandler() {
  override fun action(args: Array<String>, event: GuildMessageReceivedEvent) {

    val defaultChannel = BotUtils.defaultTextChannel(event.guild) ?: event.channel
    val message =
      if (event.guild.audioManager.connectedChannel == null) {
        ":no_entry_sign: _I am not currently recording._"
      } else {
        val voiceChannel = event.guild.audioManager.connectedChannel!!
        val audioReceiveHandler = event.guild.audioManager.receivingHandler as CombinedAudioRecorderHandler

        try {
          var seconds = 60L;
          if (args.size > 0) {
            seconds = args.first().toLong()
          }

          if (args.size <= 1) {
            audioReceiveHandler.saveClip(seconds, voiceChannel, defaultChannel)
            ""
          } else {
            val channelName = if (args[1].startsWith("#")) args[1].substring(1) else args[1]
            val channels = event.guild.getTextChannelsByName(channelName, true)

            if (channels.isEmpty()) {
              ":no_entry_sign: _Cannot find $channelName._"
            } else {
              channels.forEach { audioReceiveHandler.saveClip(seconds, voiceChannel, it) }
              ""
            }
          }
        } catch (e: NumberFormatException) {
          throw InvalidCommand(::usage, "Could not parse arguments: ${e.message}")
        }
      }

    if (message.isNotBlank())
      BotUtils.sendMessage(defaultChannel, message)
  }

  override fun usage(prefix: String, lang: Lang): String = "${prefix}clip [seconds]"

  override fun description(lang: Lang): String = "Saves a clip of the specified length (seconds) and outputs it in the current or specified text channel."
}
