package net.serverwars.sunsetPlugin.domain.menu.models.menuitem

abstract class SelectorMenuItem<T>(
    val value: T,
    permission: String
) : MenuItem(permission)
