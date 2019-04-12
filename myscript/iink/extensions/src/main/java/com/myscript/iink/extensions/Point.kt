/*
 * Copyright (c) MyScript. All rights reserved.
 */

package com.myscript.iink.extensions

import android.support.annotation.VisibleForTesting
import com.myscript.iink.graphics.Point
import java.security.InvalidAlgorithmParameterException

/**
 * Determines if a point is inside a polygon.
 * This determination works with the winding number, see [windingNumberToPolygon].
 * @param vertices vertex points of a polygon v(n + 1) with v(n) == v(0).
 * @return `true` if the point is inside the given polygon, otherwise `false`.
 */
fun Point.isInsidePolygon(vertices: Array<Point>) = windingNumberToPolygon(vertices) != 0

// region implementations (Inclusion of a Point in a Polygon)

/**
 * Computes winding number for a point to a polygon.
 * @param vertices vertex points of a polygon v(n + 1) with v(n) == v(0).
 * @return the winding number (=0 only when the point is outside).
 * @see [Inclusion of a Point in a Polygon](http://geomalgorithms.com/a03-_inclusion.html)
 */
@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
fun Point.windingNumberToPolygon(vertices: Array<Point>): Int {
    if (vertices.firstOrNull() != vertices.lastOrNull())
        throw InvalidAlgorithmParameterException("Invalid polygon: this is not a closed polygon.")
    var windingNumber = 0
    var prev: Point? = null
    vertices.forEach { end ->
        if (prev != null) {
            // an edge of the polygon contains a start point and an end point.
            val start = prev as Point
            if (start.y <= y) {
                if (y < end.y /* an upward crossing */ && isLeftToEdge(start, end))
                    ++windingNumber
            } else {
                if (y >= end.y /* a downward crossing */ && isRightToEdge(start, end))
                    --windingNumber
            }
        }
        // the end point becomes the start point of the next edge.
        prev = end
    }
    return windingNumber
}

/**
 * Determines if the point is on the left side of the given edge.
 * @param start the start point of the edge.
 * @param end the end point of the edge.
 * @return `true` if the point is on the left side of the given edge, otherwise `false`.
 */
@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
fun Point.isLeftToEdge(start: Point, end: Point): Boolean = relativePositionToEdge(start, end) > 0f

/**
 * Determines if the point is on the right side of the given edge.
 * @param start the start point of the edge.
 * @param end the end point of the edge.
 * @return `true` if the point is on the right side to the given edge, otherwise `false`.
 */
@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
fun Point.isRightToEdge(start: Point, end: Point): Boolean = relativePositionToEdge(start, end) < 0f

/**
 * Determines if the point is on the given edge.
 * @param start the start point of the edge.
 * @param end the end point of the edge.
 * @return `true` if the point is on the given edge, otherwise `false`.
 */
@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
fun Point.isOnEdge(start: Point, end: Point): Boolean = relativePositionToEdge(start, end) == 0f

/**
 * Determines the relative position to an infinite line.
 * @param start the start point of the edge.
 * @param end the end point of the edge.
 * @return With the original point (0, 0) at the top-left corner:
 *         position > 0 for the point is left to the edge;
 *         position = 0 for the point is on the edge;
 *         position < 0 for the point is right to the edge.
 * @see [Area of Triangles and Polygons](http://geomalgorithms.com/a01-_area.html).
 */
private fun Point.relativePositionToEdge(start: Point, end: Point): Float =
    (start - this).crossProductWith(end - this)

/**
 * Computes the cross product with another vector.
 * Algorithm: v1 × v2 = x1 * y2 - x2 * y1.
 * @param other another vector.
 * @return the cross product.
 */
@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
fun Point.crossProductWith(other: Point): Float = x * other.y - other.x * y

// endregion

private operator fun Point.plus(other: Point) = Point(x + other.x, y + other.y)
private operator fun Point.minus(other: Point) = Point(x - other.x, y - other.y)
