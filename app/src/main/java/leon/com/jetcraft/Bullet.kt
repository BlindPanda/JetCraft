package leon.com.jetcraft

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import java.util.*

/**
 * Created by liyl9 on 2018/5/18.
 */
class Bullet : GameItem {
    companion object {
        val BULLET_TYPE_PLAYER = 1
        val BULLET_TYPE_BOSS = 2
        val BULLET_TYPE_SOLDIER = 3

        val BULLET_SPEED_PLAYER = 20
        val BULLET_SPEED_BOSS = 3
        val BULLET_SPEED_SOLDIER = 3
    }

    var _screenWidth: Int = 0
    var _screenHeight: Int = 0
    var _angle: Int = 0 //30-150
    var _distance: Int = 0 //顺着随机角度触发按照speed行走的路径
    private var _bulletType: Int = BULLET_TYPE_PLAYER
    private var _bulletSpeed: Int = BULLET_SPEED_PLAYER
    var isInvalid: Boolean = false

    constructor(bmpBullet: Bitmap?, pointX: Int, pointY: Int, bulletType: Int, screenWith: Int, screenHeight: Int) {
        _bitmap = bmpBullet!!
        _pointX = pointX
        _pointY = pointY
        _screenWidth = screenWith
        _screenHeight = screenHeight
        _bulletType = bulletType
        _bulletSpeed = when (_bulletType) {
            BULLET_TYPE_PLAYER -> BULLET_SPEED_PLAYER
            BULLET_TYPE_BOSS -> BULLET_SPEED_BOSS
            BULLET_TYPE_SOLDIER -> BULLET_SPEED_SOLDIER
            else -> {
                BULLET_SPEED_PLAYER
            }
        }
        if (bulletType == BULLET_TYPE_BOSS) {
            _angle = getRandomAngel()
        }
    }

    private fun getRandomAngel(): Int {
        val max = 150
        val min = 30
        val random = Random()
        return random.nextInt(max) % (max - min + 1) + min
    }

    override fun draw(canvas: Canvas, paint: Paint) {
        if (!isInvalid) {
            canvas.drawBitmap(_bitmap, _pointX.toFloat(), _pointY.toFloat(), paint)
        }
    }

    override fun logic() {
        when (_bulletType) {
            BULLET_TYPE_PLAYER -> {
                _pointY -= _bulletSpeed
                if (_pointY <= -50) {
                    isInvalid = true
                }
            }
            BULLET_TYPE_BOSS -> {
                _distance += _bulletSpeed
                if (_angle <= 90) {
                    _pointX -= (_distance * Math.cos((_angle * Math.PI) / 180)).toInt()
                    _pointY += (_distance * Math.sin((_angle * Math.PI) / 180)).toInt()
                } else {
                    _pointX += (_distance * Math.cos(((180 - _angle) * Math.PI) / 180)).toInt()
                    _pointY += (_distance * Math.sin(((180 - _angle) * Math.PI) / 180)).toInt()
                }

                if (_pointY >= _screenHeight + 50) {
                    isInvalid = true
                }
                if (_pointX < -50 || _pointX > _screenWidth + 50) {
                    isInvalid = true
                }
            }
        }
    }

}
