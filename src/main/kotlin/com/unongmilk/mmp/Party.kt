package com.unongmilk.mmp

import org.bukkit.entity.Player

class Party(val leader : Player) {
    companion object {
        fun getParty(player : Player): Party? {
            Main.parties.forEach {
                if (player.uniqueId == it.leader.uniqueId) return it
                it.member.forEach { it2 ->
                    if (player.uniqueId == it2.uniqueId) return it
                }
            }
            return null
        }
    }
    var member = arrayListOf<Player>()
    fun isLeader(player : Player): Boolean {
        return leader.uniqueId == player.uniqueId
    }
    fun removePlayer(player : Player) {
        for (i in member.indices) {
            if (member[i].uniqueId == player.uniqueId) {
                member.removeAt(i)
            }
        }
    }
    fun message(message : String) {
        leader.sendMessage(message)
        member.forEach {
            it.sendMessage(message)
        }
    }
}