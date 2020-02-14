package org.hexworks.zircon.internal.component.impl

import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.events.api.simpleSubscribeTo
import org.hexworks.cobalt.logging.api.LoggerFactory
import org.hexworks.zircon.api.component.ComponentContainer
import org.hexworks.zircon.api.uievent.Pass
import org.hexworks.zircon.api.uievent.UIEvent
import org.hexworks.zircon.api.uievent.UIEventResponse
import org.hexworks.zircon.internal.Zircon
import org.hexworks.zircon.internal.behavior.ComponentFocusHandler
import org.hexworks.zircon.internal.behavior.impl.DefaultComponentFocusHandler
import org.hexworks.zircon.internal.component.InternalComponentContainer
import org.hexworks.zircon.internal.data.LayerState
import org.hexworks.zircon.internal.event.ZirconEvent.ComponentAdded
import org.hexworks.zircon.internal.event.ZirconEvent.ComponentRemoved
import org.hexworks.zircon.internal.event.ZirconScope
import org.hexworks.zircon.internal.uievent.UIEventDispatcher
import org.hexworks.zircon.internal.uievent.impl.UIEventToComponentDispatcher
import kotlin.contracts.ExperimentalContracts
import kotlin.jvm.Synchronized

class DefaultComponentContainer(
        private val root: RootContainer,
        private val focusHandler: ComponentFocusHandler = DefaultComponentFocusHandler(root),
        private val dispatcher: UIEventToComponentDispatcher = UIEventToComponentDispatcher(
                root = root,
                focusHandler = focusHandler)
) : InternalComponentContainer,
        ComponentContainer by root,
        ComponentFocusHandler by focusHandler,
        UIEventDispatcher by dispatcher {

    override val layerStates: Iterable<LayerState>
        @Synchronized
        get() = root.flattenedTree.flatMap { it.layerStates }

    override val isActive = false.toProperty()
    private val logger = LoggerFactory.getLogger(this::class)

    @ExperimentalContracts
    @Synchronized
    override fun dispatch(event: UIEvent): UIEventResponse {
        return if (isActive.value) {
            dispatcher.dispatch(event)
        } else Pass
    }


    @Synchronized
    override fun activate() {
        logger.debug("Activating component container.")
        isActive.value = true
        refreshFocusables()
        Zircon.eventBus.simpleSubscribeTo<ComponentAdded>(ZirconScope) {
            refreshFocusables()
        }.keepWhile(isActive)
        Zircon.eventBus.simpleSubscribeTo<ComponentRemoved>(ZirconScope) {
            refreshFocusables()
        }.keepWhile(isActive)
    }

    @Synchronized
    override fun deactivate() {
        isActive.value = false
        dispatcher.focusComponent(root)
    }

}
