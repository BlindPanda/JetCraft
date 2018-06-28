package leon.com.jetcraft

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import android.util.Log
import java.util.HashMap

/**
 * Created by liyl9 on 2018/6/28.
 */
class SoundPlayer {
    private lateinit var context: Context
    private var soundPool: SoundPool
    private var map: HashMap<Int, Int>

    @SuppressLint("UseSparseArrays")
    constructor(mContext: Context){
        context = mContext
        map = HashMap()
        soundPool = SoundPool(8, AudioManager.STREAM_MUSIC, 0)
        initGameSound()
    }

    /**
     * 初始化游戏音乐
     */
    fun initGameSound() {
        map.put(1, soundPool.load(context, R.raw.shoot, 1))
        map.put(2, soundPool.load(context, R.raw.explosion, 1))
        map.put(3, soundPool.load(context, R.raw.explosion2, 1))
        map.put(4, soundPool.load(context, R.raw.explosion3, 1))
        map.put(5, soundPool.load(context, R.raw.bigexplosion, 1))
        map.put(6, soundPool.load(context, R.raw.get_goods, 1))
        map.put(7, soundPool.load(context, R.raw.button, 1))
    }

    /**
     * 播放游戏背音乐
     * @param sound
     * @param loop
     */
    fun playSound(sound: Int, loop: Int) {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val stramVolumeCurrent = am.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
        val stramMaxVolumeCurrent = am.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
        val volume = stramVolumeCurrent / stramMaxVolumeCurrent
        soundPool.play(map[sound]!!, volume, volume, 1, loop, 1.0f)
    }
}