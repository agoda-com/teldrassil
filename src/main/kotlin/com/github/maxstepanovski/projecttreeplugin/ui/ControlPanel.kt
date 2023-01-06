package com.github.maxstepanovski.projecttreeplugin.ui

import com.github.maxstepanovski.projecttreeplugin.config.ConfigParams
import java.awt.Button
import javax.swing.JLabel
import javax.swing.JPanel

class ControlPanel(
        private val setZoomCallback: (shouldIncrease: Boolean) -> Unit,
        private val setFontCallback: (shouldIncrease: Boolean) -> Unit,
        private val switchEdgeMode: () -> Unit
) : JPanel() {
    private val zoomInButton = Button(PLUS)
    private val zoomOutButton = Button(MINUS)
    private val zoomLabel = JLabel(ZOOM)
    private val switchEdgeModeButton = Button(CENTERED_ON)
    private val increaseFont = Button(PLUS)
    private val decreaseFont = Button(MINUS)
    private val fontSizeLabel = JLabel(FONT_SIZE)

    init {
        zoomInButton.addActionListener {
            setZoomCallback(true)
        }
        add(zoomInButton)
        add(zoomLabel)
        add(zoomOutButton)
        zoomOutButton.addActionListener {
            setZoomCallback(false)
        }
        add(switchEdgeModeButton)
        switchEdgeModeButton.addActionListener {
            ConfigParams.CENTERED_CONNECTION = !ConfigParams.CENTERED_CONNECTION
            if (ConfigParams.CENTERED_CONNECTION) {
                switchEdgeModeButton.label = CENTERED_ON
            } else {
                switchEdgeModeButton.label = CENTERED_OFF
            }
            switchEdgeMode()
        }
        add(increaseFont)
        increaseFont.addActionListener {
            setFontCallback(true)
        }
        add(fontSizeLabel)
        add(decreaseFont)
        decreaseFont.addActionListener {
            setFontCallback(false)
        }
    }

    companion object {
        private const val CENTERED_ON = "Centered on"
        private const val CENTERED_OFF = "Centered off"
        private const val ZOOM = "Zoom"
        private const val PLUS = "+"
        private const val MINUS = "-"
        private const val FONT_SIZE = "Font size"
    }
}