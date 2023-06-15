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
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

object WarpGui : Listener {
    fun getWarpGui(player: Player): Inventory {
        val air = ItemStack(Material.IRON_AXE, 1, 64.toShort())
        val airm = air.itemMeta
        airm.displayName = " "
        airm.isUnbreakable = true
        airm.addItemFlags(*ItemFlag.values())
        air.itemMeta = airm
        val inv = Bukkit.createInventory(null, 27, "플레이어 " + player.name + "님의 파티 이동을 하시겠습니까?")
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
        inv.setItem(26, ItemStack(Material.IRON_AXE).apply {
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
        val party = Party.getParty(player) ?: return
        if (event.view.title == null) return
        if (event.clickedInventory == null) return
        if (event.clickedInventory.title != getWarpGui(party.leader).title) return
        event.isCancelled = true
        when (event.slot) {
            12 -> {
                player.teleport(Main.wloc[party] ?: party.leader.location)
                party.message(Language.getAccWarp(player.name))
                player.closeInventory()
            }
            14 -> {
                Party.getParty(player)?.message(Language.getDecWarp(player.name))
                player.closeInventory()
            }
        }
    }
}