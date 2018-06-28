package leon.com.jetcraft

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent

/**
 * Created by liyl9 on 2018/5/17.
 */
class GamePlayer:GameItem {
    override fun logic() {
        //nothing to do
    }

    var _playerHpBitmap:Bitmap
    private var _screenWidth:Int = 0
    private var _screenHeight:Int = 0
    private var playerHp = 5//默认5条命
    private var _moveSpeed = 5
    private var _lastTouchX = 0f
    private var _lastTouchY = 0f
    constructor(bmpPlayer: Bitmap?, bmpPlayerHp: Bitmap?, screenWidth: Int?, screenHeight: Int?){
        _bitmap = bmpPlayer!!
        _playerHpBitmap = bmpPlayerHp!!
        _screenWidth = screenWidth!!
        _screenHeight=screenHeight!!
        _pointX = screenWidth/2-bmpPlayer.width/2
        _pointY=screenHeight-bmpPlayer.height - _playerHpBitmap.height
    }
    override fun draw(canvas: Canvas,paint: Paint){
        canvas.drawBitmap(_bitmap,_pointX.toFloat(),_pointY.toFloat(),paint)
        for(i in 0 until playerHp){
            canvas.drawBitmap(_playerHpBitmap, (i * _playerHpBitmap.width).toFloat(),
                    (_screenHeight - _playerHpBitmap.height).toFloat(), paint)
        }
    }
    fun onTouchEvent(event: MotionEvent, soundPlayer: SoundPlayer?){
        if(event.action == MotionEvent.ACTION_DOWN) {
            _moveSpeed = 5
            _lastTouchX = event.x
            _lastTouchY = event.y
        }
        if (event.action == MotionEvent.ACTION_MOVE) {
            _moveSpeed = 9
            val offsetX = event.x - _lastTouchX
            val offsetY = event.y - _lastTouchY
            //计算新坐标
            _pointX+=(offsetX.toInt())
            _pointY+=(offsetY.toInt())
            //判断是否合法
            if(_pointX< 0)
                _pointX = 0
            if(_pointX>_screenWidth-_bitmap.width)
                _pointX = _screenWidth-_bitmap.width
            if(_pointY>_screenHeight - _playerHpBitmap.height-_bitmap.height)
                _pointY = _screenHeight - _playerHpBitmap.height-_bitmap.height
            if(_pointY<0)
                _pointY = 0

            _lastTouchX = event.x
            _lastTouchY = event.y
        }
        if(event.action == MotionEvent.ACTION_UP){
            _lastTouchY = 0f
            _lastTouchX = 0f
        }
    }
    fun isShotByBossBullet(bullet: Bullet):Boolean{
        val bulletX = bullet._pointX
        val bulletY = bullet._pointY
        val bulletWidth = bullet._bitmap.width
        val bulletHeight = bullet._bitmap.height
        if(_pointX>bulletX&&_pointX>bulletX+bulletWidth){
            return false
        }else if(_pointX<bulletX&&_pointX+_bitmap.width<bulletX){
            return false
        }else if(_pointY>bulletY&&_pointY>bulletY+bulletHeight){
            return false
        }else if(_pointY<=bulletY&&_pointY+_bitmap.height<bulletY){
            return false
        }
        return true
    }

    fun setHp(i: Int) {
        playerHp = i
    }
    fun getHp():Int{
        return playerHp
    }
    fun resetPlayerStatus(){
        _pointX = _screenWidth/2-_bitmap.width/2
        _pointY=_screenHeight-_bitmap.height - _playerHpBitmap.height
        playerHp = 5
    }
}