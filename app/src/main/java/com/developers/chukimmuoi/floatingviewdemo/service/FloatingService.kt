package com.developers.chukimmuoi.floatingviewdemo.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.*
import com.developers.chukimmuoi.floatingviewdemo.MainActivity
import com.developers.chukimmuoi.floatingviewdemo.R
import kotlinx.android.synthetic.main.item_collapsed.view.*
import kotlinx.android.synthetic.main.item_expanded.view.*


/**
 * @author  : Hanet Electronics
 * @Skype   : chukimmuoi
 * @Mobile  : +84 167 367 2505
 * @Email   : muoick@hanet.com
 * @Website : http://hanet.com/
 * @Project : FloatingViewDemo
 * Created by chukimmuoi on 04/08/2017.
 */

/**
 * STARTED: Một service được gọi là STARTED khi một thành phần của ứng dụng,
 *          chẳng hạn như là activity, start nó bằng cách gọi phương thức startService().
 *          Mỗi lần được started, service chạy bên dưới vô thời hạn,
 *          thậm chí ngay cả khi thành phần đã started nó bị hủy.
 *          StartService --> onCreate() --> onStartCommand --> (Run) --> onDestroy --> ServiceShutDown.
 *
 * BOUND  : Một service được gọi là bound khi một thành phần ứng dụng
 *          liên kết với nó bằng cách gọi phương thức bindService().
 *          Một dịch vụ ràng buộc cung cấp một giao diện client-server
 *          cho phép các thành phần tương tác với service, gửi yêu cầu, nhận kết quả,
 *          thậm chí tương tự trong việc giao tiếp với interprocess (IPC).
 *          BindService --> onCreate --> onBind --> (Run) --> onUnbind() <-> onRebind --> onDestroy --> ServiceShutDown.
 *
 * Stop service: call stopShell() or stopService()
 *
 * 2 loại Service: Local Service & Remote Service
 *
 *
 * @see https://www.androidhive.info/2016/11/android-floating-widget-like-facebook-chat-head/
 * */

class FloatingService : Service() {

    private val TAG = FloatingService::class.java.simpleName

    private lateinit var mWindowManager: WindowManager

    private lateinit var mFloatingView: View

    override fun onCreate() {
        super.onCreate()

        mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        mFloatingView  = LayoutInflater.from(this).inflate(R.layout.layout_floating, null)

        val params = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT)

        params.gravity = Gravity.TOP or Gravity.LEFT
        params.x = 0
        params.y = 100

        mWindowManager.addView(mFloatingView, params)

        mFloatingView.mCollapsedImgClose.setOnClickListener { stopSelf() }

        mFloatingView.mExpandedImgClose.setOnClickListener {
            mFloatingView.mCollapsedView.visibility = View.VISIBLE
            mFloatingView.mExpandedView.visibility = View.GONE
        }

        mFloatingView.mExpandedImgOpen.setOnClickListener {
            val intent = Intent(this@FloatingService, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

            stopSelf()
        }

        mFloatingView.setOnTouchListener(object : View.OnTouchListener {
            private var initialX: Int = 0
            private var initialY: Int = 0

            private var initialTouchX: Float = 0.toFloat()
            private var initialTouchY: Float = 0.toFloat()

            override fun onTouch(view: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // TODO: Lay vi tri ban dau.
                        initialX = params.x
                        initialY = params.y

                        // TODO: Lay vi tri cam ung.
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY

                        return true
                    }
                    // TODO: OnClick
                    MotionEvent.ACTION_UP -> {
                        val XDiff = (event.rawX - initialTouchX).toInt()
                        val YDiff = (event.rawY - initialTouchY).toInt()

                        if (XDiff < 10 && YDiff < 10) {
                            if (isViewCollapsed()) {
                                mFloatingView.mCollapsedView.visibility = View.GONE
                                mFloatingView.mExpandedView.visibility = View.VISIBLE
                            }
                        }

                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        // TODO: Tinh lai toa do.
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()

                        // TODO: Thiet lap lai vi tri.
                        mWindowManager.updateViewLayout(mFloatingView, params)

                        return true
                    }
                }
                return false
            }
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Tạo lại các dịch vụ sau khi đã có đủ bộ nhớ
        // Và gọi onStartCommand() một lần nữa với một Intent null.
        return Service.START_STICKY

        // Không bận tâm tái tạo các dịch vụ một lần nữa.
        //return Service.START_NOT_STICKY

        // Tạo lại các dịch vụ
        // Và truyền một Intent tương tự cho onStartCommand().
        //return Service.START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent?): IBinder {
        TODO("not implemented")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
    }

    fun isViewCollapsed(): Boolean {
        return mFloatingView != null || mFloatingView.mCollapsedView.visibility == View.VISIBLE
    }

    override fun onDestroy() {
        mFloatingView?.let { mWindowManager.removeView(mFloatingView) }
        super.onDestroy()
    }
}