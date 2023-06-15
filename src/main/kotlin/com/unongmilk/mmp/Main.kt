package com.unongmilk.mmp

import com.sucy.skill.SkillAPI
import com.sucy.skill.api.enums.ExpSource
import com.sucy.skill.api.event.PlayerExperienceGainEvent
import com.sucy.skill.api.player.PlayerData
import com.unongmilk.mmp.gui.InfoGui
import com.unongmilk.mmp.gui.TargetGui
import com.unongmilk.mmp.gui.WarpGui
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

class Main : JavaPlugin() {
    companion object {
        lateinit var instance : Main
        var parties = arrayListOf<Party>()
        var inviting = hashMapOf<UUID, Party>()
        var wloc = hashMapOf<Party, Location?>()
        var isSharing = arrayListOf<Party>()
        lateinit var data : ConfigPlug
    }
    @Suppress("UNCHECKED_CAST")
    override fun onEnable() {
        instance = this
        data = ConfigPlug("data.yml")
        if (!dataFolder.exists()) dataFolder.mkdir()
        val setting = ConfigPlug("setting.yml")
        server.pluginManager.registerEvents(InfoGui, this)
        server.pluginManager.registerEvents(TargetGui, this)
        server.pluginManager.registerEvents(WarpGui, this)
        server.pluginManager.registerEvents(object : Listener {
            @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
            fun onExpGain(event: PlayerExperienceGainEvent) {
                val party = Party.getParty(event.playerData.player) ?: return
                if (isSharing.contains(party)) return
                isSharing.add(party)
                event.isCancelled = true
                party.leader.let {
                    if (event.playerData.player.location.distance(it.location) <= (setting.get("distance") as Double)) {
                        if (event.playerData.player.name == it.name) SkillAPI.getPlayerData(it).giveExp(event.exp, event.source)
                        else SkillAPI.getPlayerData(it).giveExp(event.exp * (setting.get("distance") as Double), event.source)
                    }
                }
                party.member.forEach {
                    if (event.playerData.player.location.distance(it.location) <= (setting.get("distance") as Double)) {
                        if (event.playerData.player.name == it.name) SkillAPI.getPlayerData(it).giveExp(event.exp, event.source)
                        else SkillAPI.getPlayerData(it).giveExp(event.exp * (setting.get("distance") as Double), event.source)
                    }
                }
                isSharing.remove(party)
            }
        }, this)
        if (data.has("party")) {
            val sec = data.config.getConfigurationSection("party")
            for (key in sec.getKeys(false)) {
                val party = Party(data.get("party.${key}.leader") as Player)
                party.member = data.get("party.${key}.member") as ArrayList<Player>
                parties.add(party)
            }
        }
        getCommand("파티").setExecutor { sender, _, _, args ->
            val p = sender as Player
            if (args.isEmpty()) return@setExecutor false
            when (args[0]) {
                "생성" -> {
                    if (Party.getParty(p) == null) {
                        parties.add(Party(p))
                        p.sendMessage(Language.CREATED_PARTY)
                    } else {
                        p.sendMessage(Language.ALREADY_AT_PARTY)
                    }
                }
                "초대" -> {
                    val party = Party.getParty(p)
                    if (party == null) {
                        p.sendMessage(Language.NOT_AT_PARTY)
                        return@setExecutor false
                    }
                    if (party.member.size >= 10) {
                        p.sendMessage(Language.TOO_MANY_AT_PARTY)
                        return@setExecutor false
                    }
                    if (args.size == 2) {
                        val t = Bukkit.getPlayer(args[1])
                        if (t != null) {
                            if (Party.getParty(t) == null && !inviting.containsKey(t.uniqueId)) {
                                inviting[t.uniqueId] = party
                                t.openInventory(TargetGui.getTargetGui(p))
                            } else {
                                p.sendMessage(Language.ALREADY_AT_PARTY)
                            }
                        }
                    }
                }
                "정보" -> {
                    val party = Party.getParty(p)
                    if (party == null) {
                        p.sendMessage(Language.NOT_AT_PARTY)
                        return@setExecutor false
                    }
                    p.openInventory(InfoGui.getInfoGui(party, p))
                }
                "탈퇴" -> {
                    val party = Party.getParty(p)
                    if (party == null) {
                        p.sendMessage(Language.NOT_AT_PARTY)
                        return@setExecutor false
                    }
                    if (party.isLeader(p)) {
                        p.sendMessage(Language.REMOVE_PARTY)
                        parties.remove(party)
                    } else {
                        party.message(Language.getLeave(p.name))
                        party.removePlayer(p)
                    }
                }
                "워프" -> {
                    val party = Party.getParty(p)
                    if (party == null) {
                        p.sendMessage(Language.NOT_AT_PARTY)
                        return@setExecutor false
                    }
                    if (party.isLeader(p)) {
                        p.sendMessage(Language.WARP)
                        if (args.size == 4 && p.isOp) {
                            val a = args[1].toIntOrNull()
                            val b = args[2].toIntOrNull()
                            val c = args[3].toIntOrNull()
                            if (a == null || b == null || c == null) {
                                p.sendMessage(Language.ERROR1)
                                return@setExecutor false
                            }
                            wloc[party] = p.location.clone().apply {
                                this.x = a.toDouble()
                                this.y = b.toDouble()
                                this.z = c.toDouble()
                            }
                        }
                        party.member.forEach {
                            it.openInventory(WarpGui.getWarpGui(party.leader))
                        }
                    }
                }
            }
            true
        }
    }

    override fun onDisable() {
        parties.forEach {
            data.set("party.${it.leader.name}.leader", it.leader)
            data.set("party.${it.leader.name}.member", it.member)
        }
        data.inplug()
    }
}