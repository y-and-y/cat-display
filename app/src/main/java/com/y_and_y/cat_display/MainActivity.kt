package com.y_and_y.cat_display

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.qualifiedName
        private const val REQUEST_OVERLAY_PERMISSION = 1
        // 保存された猫種を読み込み、ない場合は三毛猫
        var catType = 0
    }

    private var enabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        //onOff Switch
        //設定をインポート
        onOff.isChecked = sharedPreferences.getBoolean(getString(R.string.service_key), true)

        onOff.setOnCheckedChangeListener { buttonView, isChecked ->
            //設定を保存
            sharedPreferences.edit().apply {
                putBoolean(getString(R.string.service_key), isChecked)
                apply()
            }
        }

        //cat type select
        //設定をインポート
        catType = sharedPreferences.getInt(getString(R.string.cat_key), 0)
        //選択内容を「現在の猫種」に表示
        nowCatTypeEdit()

        catTypeSelect.setOnClickListener {
            val items = resources.getStringArray(R.array.cat_type_array)

            AlertDialog.Builder(this)
                .setTitle(getString(R.string.cat_type_select))
                .setItems(items) { dialog, witch ->
                    catTypeSelect(witch, sharedPreferences)
                }
                .show()
        }
    }

    private fun catTypeSelect(witch: Int, sharedPreferences: SharedPreferences) {
        catType = witch
        //選択内容を保存
        sharedPreferences.edit().apply {
            putInt(getString(R.string.cat_key), catType)
            apply()
        }
        //選択内容を「現在の猫種」に表示
        nowCatTypeEdit()
    }

    private fun nowCatTypeEdit() {
        nowCatType.text = resources.getStringArray(R.array.cat_type_array)[catType]
    }

    override fun onStart() {
        super.onStart()
        if (hasOverlayPermission()) {
            val intent = Intent(this, FloatingAppService::class.java)
                .setAction(FloatingAppService.ACTION_STOP)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                startForegroundService(intent)
            else
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

        //offの時にサービスを停止する
        if (!onOff.isChecked) {
            stopService(Intent(application, FloatingAppService::class.java))
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