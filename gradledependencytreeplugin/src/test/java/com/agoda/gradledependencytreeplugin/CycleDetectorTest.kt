package com.agoda.gradledependencytreeplugin

import com.agoda.gradledependencytreeplugin.cycledetector.CycleDetectedException
import com.agoda.gradledependencytreeplugin.cycledetector.CycleDetector
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class CycleDetectorTest {

    @Test
    fun `detectCycle correctly detects cycle when a direct cycle exist`() {
        val firstNode = DependencyNode("1", "first node")
        val secondNode = DependencyNode("2", "second node")
        firstNode.children.add(secondNode)
        secondNode.children.add(firstNode)
        assertThrows(CycleDetectedException::class.java){
            CycleDetector().detectCycle(firstNode)
        }
    }

    @Test
    fun `detectCycle correctly detects cycle when indirect cycle exist`() {
        val firstNode = DependencyNode("1", "first node")
        val secondNode = DependencyNode("2", "second node")
        val thirdNode = DependencyNode("3", "third node")
        val fourthNode = DependencyNode("4", "fourth node")
        val fifthNode = DependencyNode("5", "fifth node")
        firstNode.children.add(secondNode)
        secondNode.children.add(thirdNode)
        secondNode.children.add(fourthNode)
        fourthNode.children.add(fifthNode)
        fifthNode.children.add(firstNode)
        assertThrows(CycleDetectedException::class.java){
            CycleDetector().detectCycle(firstNode)
        }
    }

    @Test
    fun `detectCycle does not detect cycle when a cycle does not exist`() {
        val firstNode = DependencyNode("1", "first node")
        val secondNode = DependencyNode("2", "second node")
        val thirdNode = DependencyNode("3", "third node")
        val fourthNode = DependencyNode("4", "fourth node")
        val fifthNode = DependencyNode("5", "fifth node")
        firstNode.children.add(secondNode)
        secondNode.children.add(thirdNode)
        secondNode.children.add(fourthNode)
        fourthNode.children.add(fifthNode)
        assertDoesNotThrow{
            CycleDetector().detectCycle(firstNode)
        }
    }

}