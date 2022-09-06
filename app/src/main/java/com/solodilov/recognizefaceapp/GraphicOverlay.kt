package com.solodilov.recognizefaceapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import android.util.Log
import android.view.View


class GraphicOverlay(
    context: Context?,
    attrs: AttributeSet?,
) : View(context, attrs) {

    private val lock = Any()
    private val graphics: MutableList<Graphic> = ArrayList()
    private val transformationMatrix = Matrix()
    private var imageWidth = 0
    private var imageHeight = 0
    private var scaleFactor = 1.0f
    private var postScaleWidthOffset = 0f
    private var postScaleHeightOffset = 0f
    private var needUpdateTransformation = true

    abstract class Graphic(private val overlay: GraphicOverlay) {

        abstract fun draw(canvas: Canvas?)

        private fun scale(imagePixel: Float): Float {
            return imagePixel * overlay.scaleFactor
        }

        fun translateX(x: Float): Float =
            scale(x) - overlay.postScaleWidthOffset


        fun translateY(y: Float): Float =
            scale(y) - overlay.postScaleHeightOffset

    }

    fun clear() {
        synchronized(lock) { graphics.clear() }
        postInvalidate()
    }

    fun add(graphic: Graphic) {
        synchronized(lock) { graphics.add(graphic) }
    }

    fun setImageSourceInfo(width: Int, height: Int) {
        synchronized(lock) {
            this.imageWidth = width
            this.imageHeight = height
            needUpdateTransformation = true
        }
        postInvalidate()
    }

    private fun updateTransformationIfNeeded() {
        if (!needUpdateTransformation || imageWidth <= 0 || imageHeight <= 0) {
            return
        }
        val viewAspectRatio = width.toFloat() / height
        val imageAspectRatio: Float = imageWidth.toFloat() / imageHeight
        postScaleWidthOffset = 0f
        postScaleHeightOffset = 0f
        if (viewAspectRatio > imageAspectRatio) {
            scaleFactor = width.toFloat() / imageWidth
            postScaleHeightOffset = (width.toFloat() / imageAspectRatio - height) / 2
        } else {
            scaleFactor = height.toFloat() / imageHeight
            postScaleWidthOffset = (height.toFloat() * imageAspectRatio - width) / 2
        }
        transformationMatrix.reset()
        transformationMatrix.setScale(scaleFactor, scaleFactor)
        transformationMatrix.postTranslate(-postScaleWidthOffset, -postScaleHeightOffset)
        needUpdateTransformation = false
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        synchronized(lock) {
            updateTransformationIfNeeded()
            graphics.forEach {
                it.draw(canvas)
            }
        }
    }
}