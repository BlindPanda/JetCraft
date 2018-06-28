package leon.com.jetcraft

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint

/**
 * Created by liyl9 on 2018/5/18.
 */
class Explosion :GameItem{
    var playDone: Boolean = false
    private var frameWidth = 0
    private var frameHeight = 0
    private var _explosionTotalFrame = 0
    private var frameIndex = 0

    constructor(bmpBoom: Bitmap?, pointX: Int, pointY: Int, screenWidth: Int?, screenHeight: Int?,explosionTotalFrame:Int) {
        _bitmap = bmpBoom!!
        _pointX = pointX
        _pointY = pointY
        _explosionTotalFrame = explosionTotalFrame
        frameWidth = _bitmap.width / _explosionTotalFrame
        frameHeight = _bitmap.height

    }

    override fun draw(canvas: Canvas, paint: Paint) {
            canvas.save()
            canvas.clipRect(_pointX, _pointY, _pointX + frameWidth, _pointY + frameHeight)
            canvas.drawBitmap(_bitmap, (_pointX - frameIndex * frameWidth).toFloat(), _pointY.toFloat(), paint)
            canvas.restore()
    }

    override fun logic() {
        if (frameIndex < _explosionTotalFrame) {
            frameIndex++
        } else {
            playDone = true
        }
    }

}