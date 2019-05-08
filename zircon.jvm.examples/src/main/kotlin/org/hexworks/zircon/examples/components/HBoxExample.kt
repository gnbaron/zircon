package org.hexworks.zircon.examples.components

import org.hexworks.zircon.api.AppConfigs
import org.hexworks.zircon.api.CP437TilesetResources
import org.hexworks.zircon.api.ColorThemes
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Screens
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.SwingApplications
import org.hexworks.zircon.api.component.ComponentAlignment.BOTTOM_LEFT
import org.hexworks.zircon.api.component.ComponentAlignment.CENTER
import org.hexworks.zircon.api.component.ComponentAlignment.TOP_LEFT
import org.hexworks.zircon.api.component.HBox
import org.hexworks.zircon.api.extensions.onComponentEvent
import org.hexworks.zircon.api.uievent.ComponentEventType.ACTIVATED
import org.hexworks.zircon.api.uievent.Processed

object HBoxExample {

    private val theme = ColorThemes.techLight()
    private val tileset = CP437TilesetResources.rexPaint20x20()

    var count = 0

    @JvmStatic
    fun main(args: Array<String>) {

        val tileGrid = SwingApplications.startTileGrid(AppConfigs.newConfig()
                .withDefaultTileset(tileset)
                .withSize(Sizes.create(60, 30))
                .build())

        val screen = Screens.createScreenFor(tileGrid)

        val hbox = Components.hbox()
                .withSpacing(2)
                .withSize(50, 15)
                .withTitle("HBox")
                .wrapWithBox()
                .withAlignmentWithin(screen, CENTER)
                .build()

        0.until(3).forEach {
            addButton(hbox)
        }

        val addNew = Components.button()
                .withText("Add new")
                .withAlignmentWithin(screen, TOP_LEFT)
                .build().apply {
                    onComponentEvent(ACTIVATED) {
                        addButton(hbox)
                        Processed
                    }
                }

        screen.addComponent(hbox)
        screen.addComponent(addNew)

        screen.display()
        screen.applyColorTheme(theme)
    }

    private fun addButton(hbox: HBox) {
        hbox.addComponent(Components.button()
                .withText("Remove me: $count")
                .build().apply {
                    applyColorTheme(theme)
                    onComponentEvent(ACTIVATED) {
                        hbox.removeComponent(this)
                        Processed
                    }
                })
        count++
    }

}
