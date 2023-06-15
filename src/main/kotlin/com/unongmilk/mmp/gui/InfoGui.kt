package com.unongmilk.mmp.gui

import com.unongmilk.mmp.HeadGetter
import com.unongmilk.mmp.Language
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
import org.bukkit.inventory.meta.SkullMeta

object InfoGui : Listener{
    @Suppress("DEPRECATION")
    private fun getHeadPlus(player: Player, isLeader: Boolean): ItemStack {
        player.health.toInt()
        val item = ItemStack(Material.SKULL_ITEM, 1, 3.toShort())
        val skull = item.itemMeta as SkullMeta
        skull.displayName = "${ChatColor.WHITE}${player.name}"
        if (isLeader) {
            val lore = ArrayList<String>()
            lore.add(ChatColor.RED.toString() + "우클릭 하여 추방")
            skull.lore = lore
        }
        skull.owner = player.name
        item.itemMeta = skull
        return item
    }
    fun getInfoGui(party: Party, player: Player): Inventory {
        val air = ItemStack(Material.IRON_AXE, 1, 64.toShort())
        val airm = air.itemMeta
        airm.displayName = " "
        airm.isUnbreakable = true
        airm.addItemFlags(*ItemFlag.values())
        air.itemMeta = airm
        val inv = Bukkit.createInventory(
            null,
            54,
            party.leader.name + "님의 파티 (" + party.member.size.plus(1) + "/11)"
        )
        for (i in 0..53) {
            inv.setItem(i, air)
        }
        inv.setItem(4, HeadGetter.getHead(party.leader))
        party.member.forEach { member ->
            val slot = arrayListOf(18, 20, 22, 24, 26, 38, 39, 40, 41, 42)
            for (s in slot) {
                if (inv.getItem(s).type == Material.IRON_AXE) {
                    inv.setItem(s, getHeadPlus(member, party.isLeader(player)))
                    break
                }
            }
        }
        inv.setItem(53, ItemStack(Material.IRON_AXE).apply {
            this.itemMeta = this.itemMeta.apply {
                val this2 = this
                this2.isUnbreakable = true
                this2.addItemFlags(*ItemFlag.values())
            }
            this.durability = 1
        })
        return inv
    }

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        val p = event.whoClicked as Player
        val pa: Party = Party.getParty(p) ?: return
        if (event.view.title == null) return
        if (event.clickedInventory == null) return
        if (event.view.title != getInfoGui(pa, p).title) return
        event.isCancelled = true
        if (!pa.isLeader(p)) return
        val item = event.clickedInventory.getItem(event.slot)
        val slot = arrayListOf(18, 20, 22, 24, 26, 38, 39, 40, 41, 42)
        if (!slot.contains(event.slot)) return
        if (!event.isRightClick) {
            p.sendMessage("Error - Right Click Not")
        } else if (!item.hasItemMeta()) {
            p.sendMessage("Error - Not Item Meta Having")
        } else if (!item.itemMeta.hasDisplayName()) {
            p.sendMessage("Error - Not Display Name Having")
        } else if (Bukkit.getPlayer(item.itemMeta.displayName.removePrefix("${ChatColor.WHITE}")) == null) {
            p.sendMessage("Error - Not Player")
        } else {
            pa.message(Language.getKick(item.itemMeta.displayName.removePrefix("${ChatColor.WHITE}")))
            pa.removePlayer(Bukkit.getPlayer(item.itemMeta.displayName.removePrefix("${ChatColor.WHITE}")))
            p.closeInventory()
            p.openInventory(getInfoGui(pa, p))
        }
    }
}