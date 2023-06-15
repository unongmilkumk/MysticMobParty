package com.unongmilk.mmp

import org.bukkit.ChatColor

object Language {
    var DEFAULT = "${ChatColor.GRAY}"
    var NOT_AT_PARTY = "${DEFAULT}파티에 있지 않습니다"
    var ALREADY_AT_PARTY = "${DEFAULT}파티에 이미 있습니다"
    var TOO_MANY_AT_PARTY = "${DEFAULT}파티에 사람이 너무 많이 있습니다"
    var JOINED_PARTY = "${DEFAULT}%p님이 파티에 참가하셨습니다"
    var REMOVE_PARTY = "${DEFAULT}방장이 나감에 따라 파티가 해체됩니다"
    var CREATED_PARTY = "${DEFAULT}파티를 만들었습니다"
    var ACC_WARP = "${DEFAULT}%p님이 파티 이동을 수락하였습니다."
    var DEC_WARP = "${DEFAULT}%p님이 파티 이동을 거부하였습니다."
    var NOT_JOINED_PARTY = "${DEFAULT}%p님이 파티에 참가를 거부했습니다"
    var KICK = "${DEFAULT}%p님이 파티에서 강퇴당하셨습니다"
    var LEAVE = "${DEFAULT}%p님이 파티에서 나가셨습니다"
    var WARP = "${DEFAULT}파티장의 위치로 모입니다"
    var ERROR1 = "${DEFAULT}올바르지 않은 숫자입니다"
    fun getJoinedParty(player : String) = JOINED_PARTY.replace("%p", player)
    fun getNotJoinedParty(player : String) = NOT_JOINED_PARTY.replace("%p", player)
    fun getAccWarp(player : String) = ACC_WARP.replace("%p", player)
    fun getDecWarp(player : String) = DEC_WARP.replace("%p", player)
    fun getKick(player : String) = KICK.replace("%p", player)
    fun getLeave(player : String) = LEAVE.replace("%p", player)
}