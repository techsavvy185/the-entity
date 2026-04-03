package com.bitbenders.theentity.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.bitbenders.theentity.ui.theme.EntityBlack
import com.bitbenders.theentity.ui.theme.EntityGreen
import com.bitbenders.theentity.ui.theme.EntityRed
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HardwareDial(
    rotationValue: Float,
    onRotationChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val clamped = rotationValue.coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val delta = (-dragAmount.y / 600f)
                    onRotationChanged((clamped + delta).coerceIn(0f, 1f))
                }
            },
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val stroke = 10.dp.toPx()
            val radius = (size.minDimension / 2f) - stroke
            val center = Offset(size.width / 2f, size.height / 2f)

            drawCircle(
                color = EntityBlack,
                radius = radius,
                center = center,
            )

            drawArc(
                color = EntityGreen.copy(alpha = 0.25f),
                startAngle = 150f,
                sweepAngle = 240f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )

            drawArc(
                color = if (clamped > 0.85f) EntityRed else EntityGreen,
                startAngle = 150f,
                sweepAngle = 240f * clamped,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )

            val pointerAngle = Math.toRadians((150f + 240f * clamped).toDouble())
            val pointerLength = radius * 0.75f
            val end = Offset(
                x = center.x + (cos(pointerAngle) * pointerLength).toFloat(),
                y = center.y + (sin(pointerAngle) * pointerLength).toFloat(),
            )

            drawLine(
                color = EntityGreen,
                start = center,
                end = end,
                strokeWidth = 6.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }
    }
}

