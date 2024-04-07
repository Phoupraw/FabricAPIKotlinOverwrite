package net.minecraft.util

import net.minecraft.text.Text

interface Nameable {
    val name: Text?
    fun hasCustomName(): Boolean = this.customName != null
    val displayName: Text? get() = this.name
    val customName: Text? get() = null
}