package com.example.customfancontroller

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import android.util.AttributeSet
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




class DialView(context: Context, attrs: AttributeSet) : View(context){

    private var mFanOffColor: Int
    private var mFanOnColor: Int

    private val SELECTION_COUNT = 4  // Total number of selections.
    private var mWidth: Float = 0.toFloat()                    // Custom view width.
    private var mHeight: Float = 0.toFloat()                   // Custom view height.
    private var mTextPaint: Paint? = null                // For text in the view.
    private var mDialPaint: Paint? = null                // For dial circle in the view.
    private var mRadius: Float = 0.toFloat()                   // Radius of the dial.
    private var mActiveSelection: Int = 0            // The active selection.
    // String buffer for dial labels and float for ComputeXY result.
    private val mTempLabel = StringBuffer(8)
    private val mTempResult = FloatArray(2)

    init{
        mFanOnColor = Color.CYAN
        mFanOffColor = Color.GRAY
        if ( attrs!= null) {
            val typedArray = getContext().obtainStyledAttributes(
                attrs,
                R.styleable.DialView,
                0, 0
            )

            // Set the fan on and fan off colors from the attribute values.
            mFanOnColor = typedArray.getColor(R.styleable.DialView_fanOnColor, mFanOnColor)
            mFanOffColor = typedArray.getColor(R.styleable.DialView_fanOffColor, mFanOffColor)
            // Must recycle the TypedArray when finished.
            typedArray.recycle()
        }
        mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTextPaint!!.setColor(Color.BLACK)
        mTextPaint!!.setStyle(Paint.Style.FILL_AND_STROKE)
        mTextPaint!!.setTextAlign(Paint.Align.CENTER)
        mTextPaint!!.setTextSize(40f)
        mDialPaint = Paint(Paint.ANTI_ALIAS_FLAG)
//        mDialPaint!!.setColor(Color.GRAY)
        mDialPaint!!.setColor(mFanOffColor)
        mActiveSelection = 0

        setOnClickListener(object : OnClickListener{
            override fun onClick(v: View?) {
                mActiveSelection = (mActiveSelection + 1) % SELECTION_COUNT
                if(mActiveSelection >= 1){
                    mDialPaint!!.setColor(mFanOnColor)
                }
                else{
                    mDialPaint!!.setColor(mFanOffColor)
                }
                invalidate()
            }
        })
    }


    private fun computeXYForPosition(pos: Int, radius: Float): FloatArray {
        val result = mTempResult
        val startAngle = Math.PI * (9 / 8.0)   // Angles are in radians.
        val angle = startAngle + pos * (Math.PI / 4)
        result[0] = (radius * Math.cos(angle)).toFloat() + mWidth / 2
        result[1] = (radius * Math.sin(angle)).toFloat() + mHeight / 2
        return result
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int){
        mWidth = w.toFloat()
        mHeight = h.toFloat()
        mRadius = (Math.min(mWidth, mHeight) / 2 * 0.8).toFloat()
    }

    override fun onDraw(canvas: Canvas?) {

        mDialPaint?.let { canvas?.drawCircle(mWidth / 2, mHeight /2, mRadius, it) }
        val labelRadius: Float = mRadius + 20
        val label: StringBuffer = mTempLabel
        for (i in 0 until SELECTION_COUNT) {
            val xyData = computeXYForPosition(i, labelRadius)
            val x = xyData[0]
            val y = xyData[1]
            label.setLength(0)
            label.append(i)
            mTextPaint?.let {  canvas?.drawText(label, 0, label.length, x, y, mTextPaint!!)}
        }
        val markerRadius: Float = mRadius - 35
        var xyData: FloatArray = computeXYForPosition(mActiveSelection, markerRadius)

        var x: Float = xyData[0]
        var y: Float = xyData[1]
        mTextPaint?.let { canvas?.drawCircle(x,y,20.toFloat(), it) }
    }



}