package xcj.appsets.view

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import android.os.VibrationEffect
import android.os.VibrationEffect.DEFAULT_AMPLITUDE
import android.os.Vibrator
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.animation.doOnEnd
import androidx.fragment.app.FragmentActivity
import xcj.appsets.R
import xcj.appsets.util.DensityUtil
import kotlin.math.max

/**
 *
 * @Author xucaiju
 * @Description: a logo designed for AppSets
 * @Date: Created in 19:27 2020/8/29
 *
 **/
class AppSetsLogo @JvmOverloads constructor(
    ctx: Context,
    attributes: AttributeSet?,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(
    ctx, attributes,
    defStyleAttr,
    defStyleRes
) {
    private var isShowCoordinateSystem = false
    private var mWidth = 200
    private var mHeight = 200
    private var lineThickness = mHeight * .120625f
    private var radius = (mHeight * .140625f) *.5f
    private var coordinateXStartX = mWidth * .28515615f
    private var coordinateXStopX = mWidth*(1-.28515615f)
    private var coordinateYStart = mHeight*.5f
    private val mLines = arrayOf(RectF(), RectF(), RectF(), RectF())
    private var mOnPressListener:OnPressListener? = null
    private var leftVertexOfLinePosition = .28515615f
    private val mLinesColor = intArrayOf(
        Color.parseColor("#FFDB4437"),
        Color.parseColor("#FFF4B400"),
        Color.parseColor("#FF0F9D58"),
        Color.parseColor("#FF4285F4")
    )
    private val linesDegree = floatArrayOf(55f, -30f, -32f, -35f)
    private val defaultLinesDegree = floatArrayOf(55f, -30f, -32f, -35f)
    private var lineThicknessMode = 0
    private var mBackgroundColor = Color.TRANSPARENT
    private lateinit var mPaint: Paint
    private var animationDuration = 450L
    private fun initPaint(){
        mPaint = Paint().apply {
            style = Paint.Style.FILL
            isAntiAlias = true
        }
    }
    public fun setAnimationDuration(duration: Long){
        animationDuration = duration
    }
    private fun init(typedArray: TypedArray, ctx: Context) {
        if (typedArray.hasValue(R.styleable.AppSetsLogo_line_one_color)){
            mLinesColor[0] = typedArray.getColor(R.styleable.AppSetsLogo_line_one_color, mLinesColor[0])
        }
        if (typedArray.hasValue(R.styleable.AppSetsLogo_line_two_color)){
            mLinesColor[1] = typedArray.getColor(R.styleable.AppSetsLogo_line_two_color, mLinesColor[1])
        }
        if (typedArray.hasValue(R.styleable.AppSetsLogo_line_three_color)){
            mLinesColor[2] = typedArray.getColor(R.styleable.AppSetsLogo_line_one_color, mLinesColor[2])
        }
        if (typedArray.hasValue(R.styleable.AppSetsLogo_line_four_color)){
            mLinesColor[3] = typedArray.getColor(R.styleable.AppSetsLogo_line_one_color, mLinesColor[3])
        }
        if (typedArray.hasValue(R.styleable.AppSetsLogo_show_coordinate)){
            isShowCoordinateSystem = typedArray.getBoolean(R.styleable.AppSetsLogo_show_coordinate, false)
        }
        if (typedArray.hasValue(R.styleable.AppSetsLogo_line_thickness)){
            lineThickness = typedArray.getFloat(R.styleable.AppSetsLogo_line_thickness, mHeight * .120625f)
        }
        if (typedArray.hasValue(R.styleable.AppSetsLogo_background_color)){
            mBackgroundColor = typedArray.getColor(R.styleable.AppSetsLogo_background_color, Color.TRANSPARENT)
        }
        if (typedArray.hasValue(R.styleable.AppSetsLogo_line_thickness_mode)){
            lineThicknessMode = typedArray.getInt(R.styleable.AppSetsLogo_line_thickness_mode, 0)
        }
        if (typedArray.hasValue(R.styleable.AppSetsLogo_line_one_degree)){
            linesDegree[0] = typedArray.getFloat(R.styleable.AppSetsLogo_line_one_degree, 55f)
        }
        if (typedArray.hasValue(R.styleable.AppSetsLogo_line_two_degree)){
            linesDegree[1] = typedArray.getFloat(R.styleable.AppSetsLogo_line_two_degree, -32f)
        }
        if (typedArray.hasValue(R.styleable.AppSetsLogo_line_three_degree)){
            linesDegree[2] = typedArray.getFloat(R.styleable.AppSetsLogo_line_three_degree, -30f)
        }
        if (typedArray.hasValue(R.styleable.AppSetsLogo_line_four_degree)){
            linesDegree[3] = typedArray.getFloat(R.styleable.AppSetsLogo_line_four_degree, -35f)
        }

        if (typedArray.hasValue(R.styleable.AppSetsLogo_left_vertex_of_line_position)){
            leftVertexOfLinePosition = typedArray.getFloat(R.styleable.AppSetsLogo_left_vertex_of_line_position, .28515615f)
        }
    }
    public fun setIsShowCoordinateSystem(isShown:Boolean){
        isShowCoordinateSystem = isShown
    }
    public fun isShowCoordinateSystem() = isShowCoordinateSystem
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val minimumWidth = suggestedMinimumWidth
        val minimumHeight = suggestedMinimumHeight
        val width: Int = measureWidth(minimumWidth, widthMeasureSpec)
        val height: Int = measureHeight(minimumHeight, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }
    private fun measureWidth(defaultWidth: Int, measureSpec: Int): Int {
        var mDefaultWidth = defaultWidth
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        when (specMode) {
            MeasureSpec.AT_MOST -> {
                // defaultWidth = ?
            }
            MeasureSpec.EXACTLY -> {
                mDefaultWidth = specSize
            }
            MeasureSpec.UNSPECIFIED -> {

                mDefaultWidth = max(mDefaultWidth, specSize)
            }
        }
        return mDefaultWidth
    }

    private fun measureHeight(defaultHeight: Int, measureSpec: Int): Int {
        var mDefaultHeight = defaultHeight
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        when (specMode) {
            MeasureSpec.AT_MOST -> {
                // defaultHeight = ?
            }
            MeasureSpec.EXACTLY -> {
                mDefaultHeight = specSize
            }
            MeasureSpec.UNSPECIFIED -> {
                mDefaultHeight = max(mDefaultHeight, specSize)
            }
        }
        return mDefaultHeight
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }
    private fun drawCoordinateSystem(canvas: Canvas?, line:Int){
        canvas?.apply {
            if(isShowCoordinateSystem){
                when(line){
                    0->{
                        drawLine(-coordinateXStartX,0f, coordinateXStopX, 0f, mPaint.apply { color =
                            Color.BLUE })
                        drawLine(0f,-coordinateYStart, 0f, coordinateYStart, mPaint)
                    }
                    1->{
                        drawLine(-coordinateXStartX,0f, coordinateXStopX, 0f, mPaint.apply { color =
                            Color.GREEN })
                        drawLine(0f,-coordinateYStart, 0f, coordinateYStart, mPaint)
                    }
                    2->{
                        drawLine(-coordinateXStartX,0f, coordinateXStopX, 0f, mPaint.apply { color =
                            Color.YELLOW })
                        drawLine(0f,-coordinateYStart, 0f, coordinateYStart, mPaint)
                    }
                    3->{
                        drawLine(-coordinateXStartX,0f, coordinateXStopX, 0f, mPaint.apply { color =
                            Color.RED })
                        drawLine(0f,-coordinateYStart, 0f, coordinateYStart, mPaint)
                    }
                    else->{
                        drawLine(-coordinateXStartX,0f, coordinateXStopX, 0f, mPaint.apply { color =
                            Color.BLACK })
                        drawLine(0f,-coordinateYStart, 0f, coordinateYStart, mPaint)
                    }
                }
            }
        }
    }
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //以260dp*256dp为例
        //线条厚度比例为：0.140625，如果我的方块为256*256，则我的线条厚度为36
        //1/256 = 0.00390625
        //线条长度比例为0.59375，如果我的方块为256*256，则我的线条长度为152
        //线条左边端点距离画布左边比例为0.28515625， 如果我的方块为256*256，则我的线条左边的坐标为73
        //Take 260dp*256dp as an example
        //The line thickness ratio is: 0.140625, if my square is 256*256, then my line thickness is 36
        //1/256 = 0.00390625
        //The line length ratio is 0.59375, if my square is 256*256, then my line length is 152
        //The ratio of the left end of the line to the left of the canvas is 0.28515625. If my square is 256*256, the coordinate on the left of my line is 73
        canvas?.apply {
            if(mBackgroundColor!=Color.TRANSPARENT){
                drawCircle(mWidth*.5f, mHeight*.5f, mWidth*.5f, mPaint.apply { color = mBackgroundColor })
            }
            if(leftVertexOfLinePosition>0 && leftVertexOfLinePosition<1){
                coordinateXStartX = mWidth * leftVertexOfLinePosition
                translate(coordinateXStartX, coordinateYStart)
            }else{
                translate(coordinateXStartX, coordinateYStart)
            }
            drawCoordinateSystem(this, 99)

            //Rotate the coordinate clockwise by 60 degrees
            rotate(linesDegree[0])
            drawCoordinateSystem(this, 0)
            mPaint.color = mLinesColor[3]
            drawRoundRect(mLines[0], radius, radius, mPaint)

            //Rotate the coordinates counterclockwise by 30 degrees
            rotate(linesDegree[1])
            drawCoordinateSystem(this, 1)
            mPaint.color = mLinesColor[2]
            drawRoundRect(mLines[1], radius, radius, mPaint)

            //Rotate the coordinate 32 degrees counterclockwise
            rotate(linesDegree[2])
            drawCoordinateSystem(this, 2)
            mPaint.color = mLinesColor[1]
            drawRoundRect(mLines[2], radius, radius, mPaint)

            //Rotate the coordinate counterclockwise by 35 degrees
            rotate(linesDegree[3])
            drawCoordinateSystem(this, 3)
            mPaint.color = mLinesColor[0]
            drawRoundRect(mLines[3], radius, radius, mPaint)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //get final width,height
        mWidth = w
        mHeight = h
        val toInt = DensityUtil.px2dip(context, w.toFloat()).toInt()
        val toInt1 = DensityUtil.px2dip(context, h.toFloat()).toInt()
        d("final Mode: [px]","width:[${mWidth} px], height:[${mHeight} px]\n")
        d("final Mode: [dp]","width:[${toInt} dp], height:[${toInt1} dp]\n")
        setUpLines()
    }
    private fun setUpLines(){
        coordinateXStartX = mWidth * .28515615f
        coordinateXStopX = mWidth * (1-.28515615f)
        coordinateYStart = mHeight * .5f
        if(lineThicknessMode==0){
            lineThickness = mHeight * .120625f
        }
        radius = (mHeight * .140625f) *.5f
        mLines[0].apply {
            set(
                0f,
                -lineThickness,
                mWidth*.53375f,
                0f
            )
        }
        mLines[1].apply {
            set(
                0f,
                -lineThickness*.75f,
                mWidth*.59375f,
                lineThickness*.25f
            )
        }
        mLines[2].apply {
            set(
                0f,
                -lineThickness*.5f,
                mWidth*.61375f,
                lineThickness*.5f
            )
        }
        mLines[3].apply {
            set(
                -(mWidth*.00390625f)*1.5f,
                -lineThickness*.25f,
                mWidth*.55375f,
                lineThickness*.75f
            )
        }

    }
    var pressedTime = 0L

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action){
            MotionEvent.ACTION_DOWN->{//press down
                d("onTouchEvent", "ACTION_DOWN")
                if(mOnPressListener!=null){
                    mOnPressListener?.onPress(this)
                }
                pressedTime = System.currentTimeMillis()
                startAnimation(0)
                vibrator.apply {
                    if(hasVibrator()){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            VibrationEffect.createOneShot(animationDuration, DEFAULT_AMPLITUDE)
                        }else{
                            vibrate(animationDuration)
                        }
                    }
                }
            }
            MotionEvent.ACTION_MOVE->{
                d("onTouchEvent", "ACTION_MOVE")
            }
            MotionEvent.ACTION_UP->{//uplift
                startAnimation(1)
                d("onTouchEvent", "ACTION_UP")
                pressedTime = System.currentTimeMillis() - pressedTime
                if(pressedTime>100){//long click
                    //Toast.makeText(context, "long click", Toast.LENGTH_SHORT).show()
                    mOnPressListener?.onUnPress(this)
                    performClick()
                }else{//click
                    //Toast.makeText(context, "click", Toast.LENGTH_SHORT).show()
                    mOnPressListener?.onUnPress(this)
                    performClick()
                }

            }
            MotionEvent.ACTION_CANCEL->{//uplift
                startAnimation(1)
                d("onTouchEvent", "ACTION_CANCEL")
                pressedTime = System.currentTimeMillis() - pressedTime
                if(pressedTime>100){//long click
                    mOnPressListener?.onUnPress(this)
                    performClick()
                    //Toast.makeText(context, "long click", Toast.LENGTH_SHORT).show()
                }else{//click
                    //Toast.makeText(context, "click", Toast.LENGTH_SHORT).show()
                    mOnPressListener?.onUnPress(this)
                    performClick()
                }
            }
        }
        return true
    }
    private var mInterpolator = AccelerateDecelerateInterpolator()
    private val vibrator = context.getSystemService(VIBRATOR_SERVICE) as Vibrator
    private val animatorSet = AnimatorSet()

    private fun startAnimation(flag:Int){
        when(flag){
            0->{//close
                val line1CloseAnimator: ValueAnimator = ValueAnimator.ofFloat(linesDegree[0], 0f).apply {
                    duration = animationDuration
                    interpolator = mInterpolator
                    addUpdateListener {
                        val value = it.animatedValue as Float
                        linesDegree[0] = value
                        invalidate()
                    }
                }
                val line2CloseAnimator: ValueAnimator = ValueAnimator.ofFloat(linesDegree[1], 0f).apply {
                    duration = animationDuration
                    interpolator = mInterpolator
                    addUpdateListener {
                        val value = it.animatedValue as Float
                        linesDegree[1] = value
                        invalidate()
                    }
                }
                val line3CloseAnimator: ValueAnimator = ValueAnimator.ofFloat(linesDegree[2], 0f).apply {
                    duration = animationDuration
                    interpolator = mInterpolator
                    addUpdateListener {
                        val value = it.animatedValue as Float
                        linesDegree[2] = value
                        invalidate()
                    }
                }
                val line4CloseAnimator: ValueAnimator = ValueAnimator.ofFloat(linesDegree[3], 0f).apply {
                    duration = animationDuration
                    interpolator = mInterpolator
                    addUpdateListener {
                        val value = it.animatedValue as Float
                        linesDegree[3] = value
                        invalidate()
                    }
                }
                animatorSet.apply {
                    playTogether(line1CloseAnimator, line2CloseAnimator, line3CloseAnimator, line4CloseAnimator)
                    start()
                    doOnEnd {
                        //stop vibrate
                        if (vibrator.hasVibrator()) {
                            vibrator.cancel()
                        }
                    }
                }
            }
            1->{//open
                val line1OpenAnimator: ValueAnimator = ValueAnimator.ofFloat(linesDegree[0], defaultLinesDegree[0]).apply {
                    duration = animationDuration
                    interpolator = mInterpolator
                    addUpdateListener {
                        val value = it.animatedValue as Float
                        linesDegree[0] = value
                        invalidate()
                    }
                }
                val line2OpenAnimator: ValueAnimator = ValueAnimator.ofFloat(linesDegree[1], defaultLinesDegree[1]).apply {
                    duration = animationDuration
                    interpolator = mInterpolator
                    addUpdateListener {
                        val value = it.animatedValue as Float
                        linesDegree[1] = value
                        invalidate()
                    }
                }
                val line3OpenAnimator:ValueAnimator = ValueAnimator.ofFloat(linesDegree[2], defaultLinesDegree[2]).apply {
                    duration = animationDuration
                    interpolator = mInterpolator
                    addUpdateListener {
                        val value = it.animatedValue as Float
                        linesDegree[2] = value
                        invalidate()
                    }
                }
                val line4OpenAnimator: ValueAnimator = ValueAnimator.ofFloat(linesDegree[3], defaultLinesDegree[3]).apply {
                    duration = animationDuration
                    interpolator = mInterpolator
                    addUpdateListener {
                        val value = it.animatedValue as Float
                        linesDegree[3] = value
                        invalidate()
                    }
                }
                animatorSet.apply {
                    playTogether(line1OpenAnimator, line2OpenAnimator, line3OpenAnimator, line4OpenAnimator)
                    start()
                }
            }
        }
    }
    override fun performClick(): Boolean {
        return super.performClick()
    }
    interface OnPressListener{
        fun onPress(view: View)
        fun onUnPress(view: View)
    }
    public fun setOnPressListener(listener: OnPressListener){
        mOnPressListener = listener
    }
    public fun isSetOnPressListener():Boolean{
        return mOnPressListener==null
    }

    public fun bindOnFragmentResume(fragmentActivity: FragmentActivity, action:((View)->Unit)?){
        startBloomAnimation {
            action?.invoke(this)
        }
    }
    private val startInterpolator = DecelerateInterpolator()
    public fun startBloomAnimation(block: (AppSetsLogo.()->Unit)?):AppSetsLogo{
        ValueAnimator.ofFloat(defaultLinesDegree[0], defaultLinesDegree[1]).apply {
            duration = 500
            interpolator = startInterpolator
            addUpdateListener {
                val value = it.animatedValue as Float
                linesDegree[1] = value
                invalidate()
            }
            start()
        }
        ValueAnimator.ofFloat(defaultLinesDegree[1], defaultLinesDegree[2]).apply {
            duration = 600
            interpolator = startInterpolator
            addUpdateListener {
                val value = it.animatedValue as Float
                linesDegree[2] = value
                invalidate()
            }
            startDelay = 400
            start()
        }
        ValueAnimator.ofFloat(defaultLinesDegree[2], defaultLinesDegree[3]).apply {
            duration = 700
            interpolator = startInterpolator
            addUpdateListener {
                val value = it.animatedValue as Float
                linesDegree[3] = value
                invalidate()
            }
            startDelay = 900
            start()
        }
        block?.invoke(this)
        return this
    }
    companion object{
        private const val TAG = "AppSetsLogo"
        private var debug = true
        fun View.d(tag:String?, msg:String){
            val mTag:String = tag?:TAG
            if(debug){
                Log.d(mTag, msg)
            }
        }
    }

    init {
        val typedArray = context.obtainStyledAttributes(
            attributes,
            R.styleable.AppSetsLogo,
            defStyleAttr,
            defStyleRes
        )
        init(typedArray,context)
        initPaint()
        typedArray.recycle()
    }

}