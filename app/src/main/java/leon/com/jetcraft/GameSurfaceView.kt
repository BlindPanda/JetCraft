package leon.com.jetcraft

import android.content.Context
import android.graphics.*
import android.media.MediaPlayer
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView


/**
 * Created by liyl9 on 2018/5/16.
 */
class GameSurfaceView(context: Context?) : SurfaceView(context), Runnable, SurfaceHolder.Callback {
    //声明画笔
    private var paint: Paint? = null
    //声明一个画布
    private var canvas: Canvas? = null
    //用于控制SurfaceView
    private var sfh: SurfaceHolder? = null

    private var screenWidth: Int? = 0
    private var screenHeight: Int? = 0

    //声明一条线程
    private var th: Thread? = null
    //线程消亡标识位
    private var flag: Boolean = false


    // 声明游戏需要用到的图片资源（图片声明）
    private var bmpBackGround1: Bitmap? = null// 游戏背景
    private var bmpBackGround2: Bitmap? = null// 游戏背景
    private var bmpBoom: Bitmap? = null// 爆咋效果
    private var bmpBoosBoom: Bitmap? = null// Boos爆咋效果
    private var bmpButton: Bitmap? = null// 游戏开始按钮
    private var bmpButtonPress: Bitmap? = null// 游戏开始按钮被点击
    private var bmpEnemyDuck: Bitmap? = null// 怪物鸭子
    private var bmpEnemyFly: Bitmap? = null// 怪物苍蝇
    private var bmpEnemyBoos: Bitmap? = null// 怪物猪头Boos
    private var bmpGameWin: Bitmap? = null// 游戏胜利背景
    private var bmpGameLost: Bitmap? = null// 游戏失败背景
    private var bmpPlayer: Bitmap? = null// 游戏主角飞机
    private var bmpPlayerHp: Bitmap? = null// 宗教飞机血量
    private var bmpMenu: Bitmap? = null// 菜单背景
    var bmpBullet: Bitmap? = null// 子弹
    var bmpEnemyBullet: Bitmap? = null// 敌机子弹
    var bmpBossBullet: Bitmap? = null// Boss子弹

    // 声明一个菜单对象
    private var gameMenu: GameMenu? = null
    private var gameWin: GameEndPage? = null
    private var gameLost: GameEndPage? = null
    private var gameBg: GameBackground? = null
    private var gamePlayer: GamePlayer? = null
    //boss
    //实例boss对象
    private var gameBoss:GameBoss? = null
    //主角子弹容器
    private var playerBulletContainer = ArrayList<Bullet>()
    private var bossBulletContainer = ArrayList<Bullet>()
    private var timeCountPlayer = 0
    private var timeCountBoss = 0
    //爆炸效果
    private var playerBulletExplosion = ArrayList<Explosion>()
    private var bossBulletExplosion = ArrayList<Explosion>()
    private var mMediaPlayer:MediaPlayer?=null
    private var soundPlayer:SoundPlayer?=null

    init {
        //SurfaceView初始化
        paint = Paint()
        canvas = Canvas()
        sfh = this.holder
        sfh!!.addCallback(this)
        keepScreenOn = true
        gameState = GAME_MENU//每次进来都初始化为游戏菜单界面
    }

    companion object {
        // 定义游戏状态常量
        val GAME_MENU = 0// 游戏菜单 开始
        val GAMEING = 1// 游戏中
        val GAME_WIN = 2// 游戏胜利
        val GAME_LOST = 3// 游戏失败
        val GAME_PAUSE = -1// 游戏菜单 暂停
        // 当前游戏状态（默认初始在游戏菜单界面）
        var gameState = GAME_MENU
    }
    constructor(context: Context?,attributeSet: AttributeSet):this(context)

    override fun run() {
        while (flag) {
            val startMillis = System.currentTimeMillis()
            drawGame()
            logicGame()
            val endMillis = System.currentTimeMillis()
            if (endMillis - startMillis < 50) {
                try {
                    Thread.sleep(50 - (endMillis - startMillis))
                } catch (ex: InterruptedException) {
                    ex.printStackTrace()
                }
            }
        }
    }

    /**
     * 处理游戏逻辑
     */
    private fun logicGame() {
        when (gameState) {
            GAMEING -> {
                gameBg!!.logic()
                gameBoss!!.logic()
                //清除过期的爆炸效果
                for (i in 0 until playerBulletExplosion.size) {
                    try {
                        val item = playerBulletExplosion[i]
                        if (item.playDone) {
                            playerBulletExplosion.remove(item)
                        }else{
                            item.logic()
                        }
                    }catch (e:Exception){
                        e.printStackTrace()
                        continue
                    }
                }
                for (i in 0 until bossBulletExplosion.size) {
                    try {
                        val item = bossBulletExplosion[i]
                        if (item.playDone) {
                            bossBulletExplosion.remove(item)
                        }else{
                            item.logic()
                        }
                    }catch (e:Exception){
                        e.printStackTrace()
                        continue
                    }
                }
                //遍历子弹是不是碰到Boss
                playerBulletContainer.forEach {
                    if (!it.isInvalid) {
                        if(gameBoss!!.isShotByPlayerBullet(it)){
                            it.isInvalid = true
                            //播放动画
                            gameBoss!!.setBossHp(gameBoss!!.getBossHp()-1)
                            if(gameBoss!!.getBossHp()<=0){//dead
                                gameState = GAME_WIN
                            }else{
                            //添加炸弹
                                soundPlayer!!.playSound(3,0)
                                playerBulletExplosion.add(Explosion(bmpBoom, gameBoss!!._pointX+ gameBoss!!._frameWidth/4, gameBoss!!._pointY+gameBoss!!._frameHeight/4,screenWidth,screenHeight,7))
                            }
                        }
                    }
                }
                //遍历子弹是不是碰到player了
                bossBulletContainer.forEach {
                    if (!it.isInvalid) {
                        if(gamePlayer!!.isShotByBossBullet(it)){
                            it.isInvalid = true
                            //播放动画
                            gamePlayer!!.setHp(gamePlayer!!.getHp()-1)
                            if(gamePlayer!!.getHp()<=0){//dead
                                gameState = GAME_LOST
                            }else{
                                //添加炸弹
                                soundPlayer!!.playSound(4,0)
                                bossBulletExplosion.add(Explosion(bmpBoosBoom, gamePlayer!!._pointX+ gamePlayer!!._bitmap.width/4, gamePlayer!!._pointY+gamePlayer!!._bitmap!!.width/4,screenWidth,screenHeight,5))
                            }
                        }
                    }
                }
                //生成主角子弹
                //1s 添加一个子弹
                timeCountPlayer++
                if (timeCountPlayer % 5 == 0) {
                    soundPlayer!!.playSound(1,0)
                    playerBulletContainer.add(Bullet(bmpBullet, gamePlayer!!._pointX + bmpPlayer!!.width / 2 - bmpBullet!!.width / 2, gamePlayer!!._pointY - bmpBullet!!.width, Bullet.BULLET_TYPE_PLAYER,screenWidth!!,screenHeight!!))
                    timeCountPlayer = 0
                }
                timeCountBoss++
                if (timeCountBoss % 25 == 0) {
                    for (i in 0 until 6) {
                        bossBulletContainer.add(Bullet(bmpBossBullet, gameBoss!!._pointX + gameBoss!!._frameWidth / 2 - bmpBossBullet!!.width / 2, gameBoss!!._pointY + gameBoss!!._frameHeight, Bullet.BULLET_TYPE_BOSS, screenWidth!!, screenHeight!!))
                    }
                    timeCountBoss = 0
                }
                for (i in 0 until playerBulletContainer.size) {
                    try {
                        val item = playerBulletContainer[i]
                        if (item.isInvalid) {
                            playerBulletContainer.remove(item)
                        } else {
                            item.logic()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        continue
                    }
                }
                for (i in 0 until bossBulletContainer.size) {
                    try {
                        val item = bossBulletContainer[i]
                        if (item.isInvalid) {
                            bossBulletContainer.remove(item)
                        } else {
                            item.logic()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        continue
                    }
                }
            }
        }
    }

    /**
     * 自定义游戏绘制
     */
    private fun drawGame() {
        try {
            canvas = sfh!!.lockCanvas()
            if (canvas != null) {
                //刷屏幕，白色
                Log.d("yanlonglong", "drawGame")
                canvas!!.drawColor(Color.GREEN)
                when (gameState) {
                    GAME_MENU ->
                        gameMenu!!.draw(canvas!!, paint!!)
                    GAMEING -> {
                        gameBg!!.draw(canvas!!, paint!!)
                        gamePlayer!!.draw(canvas!!, paint!!)
                        gameBoss!!.draw(canvas!!, paint!!)
                        playerBulletContainer.forEach {
                                    if (!it.isInvalid) {
                                        it.draw(canvas!!, paint!!)
                                    }
                                }
                        bossBulletContainer.forEach {
                            if (!it.isInvalid) {
                                it.draw(canvas!!, paint!!)
                            }
                        }
                        playerBulletExplosion.forEach {
                            if (!it.playDone) {
                                it.draw(canvas!!, paint!!)
                            }
                        }
                        bossBulletExplosion.forEach {
                            if (!it.playDone) {
                                it.draw(canvas!!, paint!!)
                            }
                        }

                    }
                    GAME_WIN->{
                        resetStatus()
                        gameWin!!.draw(canvas!!,paint!!)
                    }
                    GAME_LOST->{
                        resetStatus()
                        gameLost!!.draw(canvas!!,paint!!)
                    }
                }

            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            if (canvas != null)
                sfh!!.unlockCanvasAndPost(canvas)
        }
    }

    private fun resetStatus() {
        //为下次restart准备状态
        gamePlayer!!.resetPlayerStatus()
        gameBoss!!.resetBossStatus()
        playerBulletContainer.clear()
        bossBulletContainer.clear()
        playerBulletExplosion.clear()
        bossBulletExplosion.clear()

    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        //do nothing
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        flag = false
        mMediaPlayer!!.stop()
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        screenWidth = width
        screenHeight = height
        Log.d("yanlonglong","width:"+width+" height:"+height)
        // 背景音乐
        mMediaPlayer = MediaPlayer.create(context, R.raw.game)
        mMediaPlayer!!.isLooping = true
        if (!mMediaPlayer!!.isPlaying) {
            mMediaPlayer!!.start()
        }
        soundPlayer = SoundPlayer(context!!)
        initGame() // 自定义初始化函数 便于初始化游戏
        flag = true
        th = Thread(this)
        th!!.start()
    }

    private fun initGame() {
        if (gameState == GAME_MENU) {
            // 加载游戏资源
            bmpBackGround1 = BitmapFactory.decodeResource(resources,
                    R.drawable.bg_01)
            bmpBackGround2 = BitmapFactory.decodeResource(resources,
                    R.drawable.bg_02)
            bmpBoom = BitmapFactory.decodeResource(resources, R.drawable.boom)
            bmpBoosBoom = BitmapFactory.decodeResource(resources,
                    R.drawable.boos_boom)
            bmpButton = BitmapFactory.decodeResource(resources, R.drawable.button)
            bmpButtonPress = BitmapFactory.decodeResource(resources,
                    R.drawable.button_press)
            bmpEnemyDuck = BitmapFactory.decodeResource(resources,
                    R.drawable.enemy_duck)
            bmpEnemyFly = BitmapFactory.decodeResource(resources,
                    R.drawable.enemy_fly)
            bmpEnemyBoos = BitmapFactory.decodeResource(resources,
                    R.drawable.enemy_pig)
            bmpGameWin = BitmapFactory.decodeResource(resources, R.drawable.gamewin)
            bmpGameLost = BitmapFactory
                    .decodeResource(resources, R.drawable.gamelost)
            bmpPlayer = BitmapFactory.decodeResource(resources, R.drawable.player)
            bmpPlayerHp = BitmapFactory.decodeResource(resources, R.drawable.hp)
            bmpMenu = BitmapFactory.decodeResource(resources, R.drawable.menu)
            bmpBullet = BitmapFactory.decodeResource(resources, R.drawable.bullet)
            bmpEnemyBullet = BitmapFactory.decodeResource(resources,
                    R.drawable.bullet_enemy)
            bmpBossBullet = BitmapFactory.decodeResource(resources,
                    R.drawable.boosbullet)
            // 菜单类实例
            gameMenu = GameMenu(bmpMenu, bmpButton, bmpButtonPress, screenWidth, screenHeight)
            // 实例背景
            gameBg = GameBackground(bmpBackGround1,bmpBackGround1, screenWidth, screenHeight)
            // 实例主角
            gamePlayer = GamePlayer(bmpPlayer, bmpPlayerHp, screenWidth, screenHeight)
            //实例化主角
            gameBoss = GameBoss(bmpEnemyBoos, screenWidth, screenHeight)

            val buttonbg = BitmapFactory.decodeResource(resources, R.drawable.buttonbg)
            gameLost = GameEndPage(bmpGameLost,buttonbg,screenWidth,screenHeight)
            gameWin = GameEndPage(bmpGameWin,buttonbg,screenWidth,screenHeight)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (gameState) {
            GAME_MENU -> gameMenu!!.onTouchEvent(event!!,soundPlayer)
            GAMEING -> gamePlayer!!.onTouchEvent(event!!,soundPlayer)
            GAME_LOST->gameLost!!.onTouchEvent(event!!,soundPlayer)
            GAME_WIN->gameWin!!.onTouchEvent(event!!,soundPlayer)
        }
        return true
    }

    fun destory() {
        sfh!!.removeCallback(this)
    }

}