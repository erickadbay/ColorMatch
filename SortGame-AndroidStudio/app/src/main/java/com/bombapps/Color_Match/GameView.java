package com.bombapps.Color_Match;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * GameView class represents the application view.
 *
 * This is a subclass of SurfaceView and implements
 * the interface SurfaceHolder.Callback
 *
 * @see android.view.SurfaceView
 * @see android.view.SurfaceHolder.Callback
 *
 * @author Robert
 * @version 1.2
 * @since 2015-02-27
 */

public class GameView extends SurfaceView implements SurfaceHolder.Callback
{
    private final boolean stroopMode,impossibleMode;
    private SurfaceHolder surfaceHolder;
    private GameLoopThread gameLoopThread;
    private GameManager gameManager;
    private long gameTime;


    public static float WIDTH, HEIGHT;

    public static Typeface typeface;

    public static AdActivity activity;

    private boolean displayInterstitial;

    public GameView(AdActivity appActivity, long gameTime, boolean stroopMode, boolean impossibleMode)
    {
        super(appActivity);
        this.stroopMode = stroopMode;
        this.impossibleMode = impossibleMode;
        typeface = Typeface.createFromAsset(appActivity.getAssets(), "fonts/calibribold.ttf");

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        ColorsLoader.setResources(getResources());

        MediaSounds.setContext(getContext());
        MediaSounds.initializeSoundPool();

        Vibrate.setContext(getContext());
        Vibrate.initializeVibrate();

        activity = appActivity;

        this.gameTime = gameTime;
    }


    /**
     * @see SurfaceView
     * @param canvas Canvas instance representing android.graphics.Canvas
     */

    @Override
    public void draw(Canvas canvas)
    {
        if (canvas != null)
        {
            canvas.drawColor(ColorsLoader.loadColorByName("menubackground"));
            if(displayInterstitial){
                canvas.drawColor(Color.BLACK);
            } else {
                gameManager.draw(canvas);
            }
        }
    }

    /**
     * Checks whether or not an motion event has occurred. It is
     * also responsible for determining a correct, incorrect match or
     * a time out has occurred depending on the motion event.
     *
     * @see android.view.MotionEvent;
     *
     * @param event MotionEvent instance representing the
     * motion of event that has occurred.
     *
     * @return boolean representing whether or not an event has occurred.
     */

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {

        return displayInterstitial || gameManager.onTouchEvent(event);

    }


    /**
     * @see SurfaceHolder.Callback
     * @param holder The SurfaceHolder whose surface is being created.
     */

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        WIDTH = getWidth();
        HEIGHT = getHeight();

        gameLoopThread = new GameLoopThread(this);
        this.gameManager = new GameManager(gameTime,stroopMode,impossibleMode);

        gameLoopThread.setRunning(true);
        gameLoopThread.start();

    }


    /**
     * @see SurfaceHolder.Callback
     * @param holder The SurfaceHolder whose surface is being created.
     * @param format The new PixelFormat of the surface.
     * @param width The new width of the surface.
     * @param height The new height of the surface.
     */

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        WIDTH = width;
        HEIGHT = height;
    }

    /**
     * @see SurfaceHolder.Callback
     * @param holder The SurfaceHolder whose surface is being created.
     */

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        gameLoopThread.setRunning(false);
    }

    public boolean getGameOver(){
        return gameManager.getGameOver();
    }

    public void setDisplayInterstitial(boolean bool){
        displayInterstitial = bool;
    }
}
