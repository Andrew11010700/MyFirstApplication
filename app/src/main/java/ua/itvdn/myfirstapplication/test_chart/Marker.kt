package ua.itvdn.myfirstapplication.test_chart

data class Marker(
    val value: Float,
    val position: Position = Position()
)

data class Position(
    var x: Float = 0f,
    var y: Float = 0f
)
