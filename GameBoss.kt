package leon.com.jetcraft

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint

/**
 * Created by liyl9 on 2018/5/18.
 */
class GameBoss : GameItem {
    private var _bossHp = 40
    private var _bossSpeed = 5
    var _frameWidth = 0
    var _frameHeight = 0
    private var frameIndex = 0
    private var _screenWidth = 0
    private var _screenHeight = 0

    constructor(bmpEnemyBoos: Bitmap?, screenWidth: Int?, screenHeight: Int?) {
        _bitmap = bmpEnemyBoos!!
        _frameWidth = _bitmap.width / 10
        _frameHeight = _bitmap.height
        _pointX = screenWidth!! / 2 - _frameWidth / 2
        _screenWidth = screenWidth
        _screenHeight = screenHeight!!
    }

    override fun draw(canvas: Canvas, paint: Paint) {
        canvas.save()
        canvas.clipRect(_pointX, _pointY, _pointX + _frameWidth, _pointY + _frameHeight)
        canvas.drawBitmap(_bitmap, (_pointX - frameIndex * _frameWidth).toFloat(), _pointY.toFloat(), paint)
        canvas.restore()
    }

    override fun logic() {
        frameIndex++
        if (frameIndex >= 10) {
            frameIndex = 0
        }
        _pointX += _bossSpeed
        if ((_pointX + _frameWidth >= _screenWidth) || _pointX <= 0) {
            _bossSpeed = -_bossSpeed
        }
    }

    fun isShotByPlayerBullet(bullet: Bullet): Boolean {
        val bulletX = bullet._pointX
        val bulletY = bullet._pointY
        val bulletWidth = bullet._bitmap.width
        val bulletHeight = bullet._bitmap.height
        if (_pointX > bulletX && _pointX > bulletX + bulletWidth) {
            return false
        } else if (_pointX < bulletX && _pointX + _frameWidth < bulletX) {
            return false
        } else if (_pointY > bulletY && _pointY > bulletY + bulletHeight) {
            return false
        } else if (_pointY <= bulletY && _pointY + _frameHeight < bulletY) {
            return false
        }
        return true
    }

    fun setBossHp(hp: Int) {
        _bossHp = hp
    }

    fun getBossHp(): Int {
        return _bossHp
    }
    fun resetBossStatus(){
        _pointX = _screenWidth!! / 2 - _frameWidth / 2
        _bossHp = 40
    }
}