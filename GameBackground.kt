package leon.com.jetcraft

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import java.util.concurrent.LinkedBlockingQueue

/**
 * Created by liyl9 on 2018/5/16.
 */
class GameBackground {
    private var _gameBg1: Bitmap? = null
    private var _gameBg2: Bitmap? = null
    private var _gameBg3: Bitmap? = null
    private val speed: Int = 3
    private var bg1X: Int = 0
    private var bg1Y: Int = 0
    private var bg2X: Int = 0
    private var bg2Y: Int = 0
    private var bg3X: Int = 0
    private var bg3Y: Int = 0
    private var _screenWidth:Int = 0
    private var _screenHeight:Int = 0

    constructor(bmpBackGround1: Bitmap?,bmpBackGround2: Bitmap?, screenWidth: Int?, screenHeight: Int?) {
        _gameBg1 = bmpBackGround1
        _gameBg2 = bmpBackGround2
        _gameBg3 = bmpBackGround1
        bg1Y = screenHeight!! - bmpBackGround1!!.height
        //+101的原因：虽然两张背景图无缝隙连接但是因为图片资源头尾
        //直接连接不和谐，为了让视觉看不出是两张图连接而修正的位置
        bg2Y = bg1Y - _gameBg2!!.height
        bg3Y = bg2Y - _gameBg3!!.height
        _screenHeight = screenHeight
        _screenWidth = screenWidth!!
    }

    fun draw(canvas: Canvas, paint: Paint) {
        val srcRect1 = Rect(bg1X,0,_gameBg1!!.width,_gameBg1!!.height)
        val srcRect2 = Rect(bg2X,0,_gameBg1!!.width,_gameBg1!!.height)
        val srcRect3 = Rect(bg3X,0,_gameBg1!!.width,_gameBg1!!.height)
        val dstRect1 = Rect(0,bg1Y,_screenWidth!!,bg1Y+_gameBg1!!.height)
        val dstRect2 = Rect(0,bg2Y,_screenWidth!!,bg1Y)
        val dstRect3 = Rect(0,bg3Y,_screenWidth!!,bg2Y)
        canvas.drawBitmap(_gameBg1, srcRect1, dstRect1, paint)
        canvas.drawBitmap(_gameBg2, srcRect2, dstRect2, paint)
        canvas.drawBitmap(_gameBg2, srcRect3, dstRect3, paint)
    }

    fun logic() {
        bg1Y+=speed
        bg2Y+=speed
        bg3Y+=speed
        if(bg1Y>_screenHeight){
            bg1Y = bg3Y - _gameBg1!!.height
            val tmp = bg1Y
            bg1Y = bg2Y
            bg2Y =bg3Y
            bg3Y = tmp
        }
    }

}