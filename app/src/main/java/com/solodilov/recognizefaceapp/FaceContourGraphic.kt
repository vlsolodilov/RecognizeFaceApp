package com.solodilov.recognizefaceapp

import android.graphics.*
import android.util.Log
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceContour

class FaceContourGraphic(
    overlay: GraphicOverlay,
    private val face: Face
) : GraphicOverlay.Graphic(overlay) {

    companion object {
        private const val FACE_POSITION_RADIUS = 5.0f
        private const val STROKE_WIDTH = 2.0f
        private const val SELECTED_COLOR = Color.GREEN
    }

    private val facePositionPaint = Paint()

    init {
        facePositionPaint.color = SELECTED_COLOR
    }

    override fun draw(canvas: Canvas?) {
        val contour = face.getContour(FaceContour.FACE)
        val path = Path()
        contour?.points?.forEachIndexed { index, point ->

            val px = translateX(point.x)
            val py = translateY(point.y)
            if (index == 0) {
                path.moveTo(px, py)
            }
            path.lineTo(px, py)
            canvas?.drawCircle(px, py, FACE_POSITION_RADIUS, facePositionPaint)
        }
        val paint = Paint().apply {
            color = SELECTED_COLOR
            style = Paint.Style.STROKE
            strokeWidth = STROKE_WIDTH
        }
        canvas?.drawPath(path, paint)
    }
}