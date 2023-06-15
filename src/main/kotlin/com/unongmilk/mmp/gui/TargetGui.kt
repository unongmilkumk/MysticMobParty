package com.unongmilk.mmp.gui

import com.unongmilk.mmp.Language
import com.unongmilk.mmp.Main
import com.unongmilk.mmp.Party
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

object TargetGui : Listener {
    fun getTargetGui(player: Player): Inventory {
        val air = ItemStack(Material.IRON_AXE, 1, 64.toShort())
        val airm = air.itemMeta
        airm.displayName = " "
        airm.isUnbreakable = true
        airm.addItemFlags(*ItemFlag.values())
        air.itemMeta = airm
        val inv = Bukkit.createInventory(null, 27, "플레이어 " + player.name + "님의 파티에 참가하시겠습니까?")
        for (i in 0..26) {
            inv.setItem(i, air)
        }
        val accept = ItemStack(Material.IRON_AXE, 1, 249.toShort())
        val acceptm = air.itemMeta
        acceptm.displayName = ChatColor.GREEN.toString() + "수락"
        acceptm.isUnbreakable = true
        acceptm.addItemFlags(*ItemFlag.values())
        accept.itemMeta = acceptm
        inv.setItem(12, accept)
        val deny = ItemStack(Material.IRON_AXE, 1, 250.toShort())
        val denym = air.itemMeta
        denym.displayName = ChatColor.RED.toString() + "거부"
        denym.isUnbreakable = true
        denym.addItemFlags(*ItemFlag.values())
        deny.itemMeta = denym
        inv.setItem(14, deny)
        inv.setItem(56, ItemStack(Material.IRON_AXE).apply {
            this.itemMeta = this.itemMeta.apply {
                val this2 = this
                this2.isUnbreakable = true
                this2.addItemFlags(*ItemFlag.values())
            }
            this.durability = 17
        })
        return inv
    }

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        val player = event.whoClicked as Player
        if (event.view.title == null) return
        if (event.clickedInventory == null) return
        var b = true
        Main.inviting.forEach {
            if (event.clickedInventory.title == getTargetGui(it.value.leader).title) b = false
        }
        if (b) return
        event.isCancelled = true
        val party = Party.getParty(player)
        if (party != null) {
            player.sendMessage(Language.ALREADY_AT_PARTY)
            return
        }
        val invi = Main.inviting[player.uniqueId]!!
        if (!Main.inviting.containsKey(player.uniqueId)) {
            return
        }
        if (invi.member.size >= 10) {
            player.sendMessage(Language.TOO_MANY_AT_PARTY)
            return
        }
        when (event.slot) {
            12 -> {
                invi.member.add(player)
                invi.message(Language.getJoinedParty(player.name))
                Main.inviting.remove(player.uniqueId)
                player.closeInventory()
            }
            14 -> {
                invi.message(Language.getNotJoinedParty(player.name))
                Main.inviting.remove(player.uniqueId)
                player.closeInventory()
            }
        }
    }
    @EventHandler
    fun onClose(event: InventoryCloseEvent) {
        val player = event.player as Player
        if (event.view.title == null) return
        if (event.inventory == null) return
        var b = true
        Main.inviting.forEach {
            if (event.inventory.title == getTargetGui(it.value.leader).title) b = false
        }
        if (b) return
        val party = Party.getParty(player)
        if (party == null && Main.inviting.containsKey(player.uniqueId)) {
            Main.inviting[player.uniqueId]!!.message(Language.getNotJoinedParty(player.name))
            Main.inviting.remove(player.uniqueId)
            player.closeInventory()
        }
    }
}