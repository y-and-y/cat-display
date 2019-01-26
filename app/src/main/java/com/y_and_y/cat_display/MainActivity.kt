package com.y_and_y.cat_display

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.qualifiedName
        private const val REQUEST_OVERLAY_PERMISSION = 1
        var catType = 0
    }

    private var enabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //onOff Switch

        //cat type select
        catTypeSelect.setOnClickListener {
            val items = arrayOf("三毛猫", "黒猫")
            AlertDialog.Builder(this)
                    .setTitle("猫の種類を選択")
                    .setItems(items) { dialog, witch ->
                        catTypeSelect(witch)
                    }
                    .show()
        }
    }

    private fun catTypeSelect(witch: Int) {
        catType = witch
    }

    override fun onStart() {
        super.onStart()
        if (hasOverlayPermission()) {
            val intent = Intent(this, FloatingAppService::class.java)
                    .setAction(FloatingAppService.ACTION_STOP)
            startService(intent)
        } else {
            requestOverlayPermission(REQUEST_OVERLAY_PERMISSION)
        }
    }

    override fun onStop() {
        super.onStop()
        if (enabled && hasOverlayPermission()) {
            val intent = Intent(this, FloatingAppService::class.java)
                    .setAction(FloatingAppService.ACTION_START)
            startService(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_OVERLAY_PERMISSION -> Log.d(TAG, "enable overlay permission")
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        enabled = false
        return super.onTouchEvent(event)
    }
}