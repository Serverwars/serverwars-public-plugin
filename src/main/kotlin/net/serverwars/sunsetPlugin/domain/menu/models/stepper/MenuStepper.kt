package net.serverwars.sunsetPlugin.domain.menu.models.stepper

import net.serverwars.sunsetPlugin.domain.menu.models.menu.SelectorMenu
import org.bukkit.entity.HumanEntity

abstract class MenuStepper(
    val viewer: HumanEntity,
) {
    private var stepIndex = 0

    abstract val steps: List<SelectorMenu<*>>
    abstract fun onFinalStepComplete()

    fun open() {
        if (this.steps.isEmpty()) error("No steps defined")

        this.stepIndex = 0
        this.viewer.openInventory(this.steps[0].inventory)
    }

    fun onStepComplete() {
        if (this.stepIndex >= this.steps.size - 1) onFinalStepComplete()
        else this.viewer.openInventory(this.steps[++this.stepIndex].inventory)
    }

}
