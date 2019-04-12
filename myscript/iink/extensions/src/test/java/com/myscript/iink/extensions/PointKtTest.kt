/*
 * Copyright (c) MyScript. All rights reserved.
 */

package com.myscript.iink.extensions

import com.myscript.iink.graphics.Point
import org.junit.Test
import java.security.InvalidAlgorithmParameterException

class PointKtTest {

    @Test
    fun testPointInsidePolygon() {
        val point = Point(.5f, .5f)
        val triangle = arrayOf(
            Point(0f, 0f),
            Point(0f, 2f),
            Point(2f, 0f),
            Point(0f, 0f)
        )
        assert(point.isInsidePolygon(triangle))
    }

    @Test
    fun testPointOutsidePolygon() {
        val point = Point(1.5f, 1.5f)
        val triangle = arrayOf(
            Point(0f, 0f),
            Point(0f, 2f),
            Point(2f, 0f),
            Point(0f, 0f)
        )
        assert(!point.isInsidePolygon(triangle))
    }

    @Test(expected = InvalidAlgorithmParameterException::class)
    fun testInvalidPolygonException() {
        val point = Point(.5f, .5f)
        val openTriangle = arrayOf(
            Point(0f, 0f),
            Point(0f, 2f),
            Point(2f, 0f)
        )
        point.isInsidePolygon(openTriangle)
    }

    @Test
    fun testPointLeftToEdge() {
        val point = Point(0f, 0f)
        val edgeStart = Point(2f, 0f)
        val edgeEnd = Point(0f, 2f)
        assert(point.isLeftToEdge(edgeStart, edgeEnd))
    }

    @Test
    fun testPointRightToEdge() {
        val point = Point(2f, 2f)
        val edgeStart = Point(2f, 0f)
        val edgeEnd = Point(0f, 2f)
        assert(point.isRightToEdge(edgeStart, edgeEnd))
    }

    @Test
    fun testPointOnEdge() {
        val point = Point(1f, 1f)
        val edgeStart = Point(2f, 0f)
        val edgeEnd = Point(0f, 2f)
        assert(point.isOnEdge(edgeStart, edgeEnd))
    }

    @Test
    fun testCrossProduct() {
        val v1 = Point(1f, 0f)
        val v2 = Point(0f, 1f)
        assert(v1.crossProductWith(v2) == 1f)
    }
}
