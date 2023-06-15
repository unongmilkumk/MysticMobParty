package com.unongmilk.mmp

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

object HeadGetter {
    @Suppress("DEPRECATION")
    fun getHead(player: Player): ItemStack {
        val item = ItemStack(Material.SKULL_ITEM, 1, 3.toShort())
        val skull = item.itemMeta as SkullMeta
        skull.displayName = "${ChatColor.WHITE}${player.name}"
        skull.owner = player.name
        item.itemMeta = skull
        return item
    }
}