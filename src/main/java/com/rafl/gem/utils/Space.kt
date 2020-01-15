package com.rafl.gem.utils

data class Point(val x: Float, val y: Float)
fun p(x: Float, y: Float) = Point(x, y)

data class Line(private val slope: Float, private val intercept: Float) {
    operator fun contains(p: Point) = (slope*p.x + intercept) == p.y
}

fun line(p0: Point, p1: Point) : Line? {
    if (p0 == p1) return null
    val m = (p1.y-p0.y)/(p1.x-p0.x)
    val b = p0.y - m*p0.x
    return Line(m, b)
}