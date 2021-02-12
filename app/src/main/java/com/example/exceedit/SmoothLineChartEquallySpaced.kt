package com.example.exceedit

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import java.util.*

class SmoothLineChartEquallySpaced @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {
    private val mPaint: Paint
    private val mPath: Path
    private val mCircleSize: Float
    private val mStrokeSize: Float
    private val mBorder: Float
    private var mValues: FloatArray?=null
    private val mMinY = 0f
    private var mMaxY = 0f
    fun setData(values: FloatArray?) {
        mValues = values
        if (values != null && values.size > 0) {
            mMaxY = values[0]
            //mMinY = values[0].y;
            for (y in values) {
                if (y > mMaxY) mMaxY = y
                /*if (y < mMinY)
					mMinY = y;*/
            }
        }
        invalidate()
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        if (mValues == null || mValues!!.size == 0) return
        val size = mValues!!.size
        val height = measuredHeight - 2 * mBorder
        val width = measuredWidth - 2 * mBorder
        val dX = if (mValues!!.size > 1) (mValues!!.size - 1).toFloat() else 2.toFloat()
        val dY: Float = if (mMaxY - mMinY > 0) mMaxY - mMinY else 2F
        mPath.reset()

        // calculate point coordinates
        val points: MutableList<PointF> = ArrayList(size)
        for (i in 0 until size) {
            val x = mBorder + i * width / dX
            val y = mBorder + height - (mValues!![i] - mMinY) * height / dY
            points.add(PointF(x, y))
        }

        // calculate smooth path
        var lX = 0f
        var lY = 0f
        mPath.moveTo(points[0].x, points[0].y)
        for (i in 1 until size) {
            val p = points[i] // current point

            // first control point
            val p0 = points[i - 1] // previous point
            val x1 = p0.x + lX
            val y1 = p0.y + lY

            // second control point
            val p1 = points[if (i + 1 < size) i + 1 else i] // next point
            lX = (p1.x - p0.x) / 2 * SMOOTHNESS // (lX,lY) is the slope of the reference line
            lY = (p1.y - p0.y) / 2 * SMOOTHNESS
            val x2 = p.x - lX
            val y2 = p.y - lY

            // add line
            mPath.cubicTo(x1, y1, x2, y2, p.x, p.y)
        }


        // draw path
        mPaint.color = CHART_COLOR
        mPaint.style = Paint.Style.STROKE
        canvas.drawPath(mPath, mPaint)

        // draw area
        if (size > 0) {
            mPaint.style = Paint.Style.FILL
            mPaint.color = CHART_COLOR and 0xFFFFFF or 0x10000000
            mPath.lineTo(points[size - 1].x, height + mBorder)
            mPath.lineTo(points[0].x, height + mBorder)
            mPath.close()
            canvas.drawPath(mPath, mPaint)
        }

        // draw circles
        mPaint.color = CHART_COLOR
        mPaint.style = Paint.Style.FILL_AND_STROKE
        for (point in points) {
            canvas.drawCircle(point.x, point.y, mCircleSize / 2, mPaint)
        }
        mPaint.style = Paint.Style.FILL
        mPaint.color = Color.WHITE
        for (point in points) {
            canvas.drawCircle(point.x, point.y, (mCircleSize - mStrokeSize) / 2, mPaint)
        }
    }

    companion object {
        private const val CHART_COLOR = -0xff6634
        private const val CIRCLE_SIZE = 8
        private const val STROKE_SIZE = 2
        private const val SMOOTHNESS = 0.35f // the higher the smoother, but don't go over 0.5
    }

    init {
        val scale = context.resources.displayMetrics.density
        mCircleSize = scale * CIRCLE_SIZE
        mStrokeSize = scale * STROKE_SIZE
        mBorder = mCircleSize
        mPaint = Paint()
        mPaint.isAntiAlias = true
        mPaint.strokeWidth = mStrokeSize
        mPath = Path()
    }
}