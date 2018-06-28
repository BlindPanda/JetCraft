package leon.com.jetcraft

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import android.view.MotionEvent

/**
 * Created by liyl9 on 2018/5/16.
 */
class GameEndPage {
    private var _bmpMenu: Bitmap? = null
    private var _bmpButton: Bitmap? = null
    private var _buttonText: String? = "重新开始"
    private var _btnX: Float = 0f
    private var _btnY: Float = 0f
    private var _isPress: Boolean = false
    private var _screenWidth:Int? = 0
    private var _screenHeight:Int? = 0


    constructor(bmpMenu: Bitmap?, bmpButton: Bitmap?, screenWidth: Int?, screenHeight: Int?) {
        _bmpMenu = bmpMenu
        _bmpButton = bmpButton
        _screenWidth = screenWidth
        _screenHeight = screenHeight
        _btnX = (screenWidth!! / 2 - bmpButton!!.width / 2).toFloat()
        _btnY = (screenHeight!! - 2*(bmpButton!!.height)).toFloat()
    }

    fun draw(canvas: Canvas, paint: Paint) {
        Log.d("yanlonglong", "GameEndPage draw")
        val srcRect = Rect(0, 0, _bmpMenu!!.width, _bmpMenu!!.height)
        val dstRect = Rect(0, 0, _screenWidth!!, _screenHeight!!)
        canvas.drawBitmap(_bmpMenu, srcRect, dstRect, paint)
        canvas.drawBitmap(_bmpButton, _btnX, _btnY, paint)
        paint.textSize= 38F
        canvas.drawText(_buttonText,(_btnX+_bmpButton!!.width / 4),(_btnY+_bmpButton!!.height/2),paint)
    }

    fun onTouchEvent(event: MotionEvent, soundPlayer: SoundPlayer?) {
        val pointX = event.x
        val pointY = event.y
        if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
            _isPress = (pointX > _btnX && pointX < (_btnX + _bmpButton!!.width)) && (pointY > _btnY && pointY < (_btnY + _bmpButton!!.height))
            if(_isPress)
                soundPlayer!!.playSound(1,0)
        } else if (event.action == MotionEvent.ACTION_UP) {
            if ((pointX > _btnX && pointX < (_btnX + _bmpButton!!.width)) && (pointY > _btnY && pointY < (_btnY + _bmpButton!!.height))) {
                _isPress = false
                GameSurfaceView.gameState = GameSurfaceView.GAMEING
            }

        }
    }

}