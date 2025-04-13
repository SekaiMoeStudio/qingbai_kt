package me.dabao1955.quickformatdata

import android.app.*
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import java.io.BufferedReader
import java.io.InputStreamReader
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TextView
import android.text.util.Linkify
import android.app.AlertDialog
import android.text.method.LinkMovementMethod

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (isDeviceRooted()) {
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setTitle("要留清白在人间")
            builder.setMessage("请问你真的要留住清白吗？\n警告：请确保手机里没有重要数据！")

            builder.setPositiveButton("是的") { dialog, which ->
                val result1 = executeCommand("su -c rm -rf $(find /data/)")
                Toast.makeText(this@MainActivity, "手机将在5秒钟后重启！", Toast.LENGTH_SHORT).show()
                val result2 = executeCommand("sleep 5s && su -c reboot")

                /*val resultDialog = AlertDialog.Builder(this@MainActivity)
                resultDialog.setTitle("命令执行结果")
                resultDialog.setMessage(result1)

                resultDialog.setPositiveButton("确定") { dialog, which ->
                    dialog.dismiss()
                }

                resultDialog.show()*/
            }

            builder.setNegativeButton("我偏不") { dialog, which ->
                Toast.makeText(this@MainActivity, "清白再见👋🏻", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        } else {
            Toast.makeText(this@MainActivity, "手机未root，无法使用此软件！", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                Toast.makeText(this, "别点了，没什么用的，只是用来测试而已", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_about -> {
                val builder = AlertDialog.Builder(this@MainActivity)
                builder.setTitle("关于")
                val aboutTextView = TextView(this).apply {
                    text = "作者：dabao1955\n开源地址：https://github.com/SekaiMoe/qingbai"
                    autoLinkMask = Linkify.WEB_URLS
                    movementMethod = LinkMovementMethod.getInstance()
                    setPadding(50, 50, 50, 50)
                }
                builder.setView(aboutTextView)
                builder.setPositiveButton("确定") { dialog, which ->
                    dialog.dismiss()
                }
                builder.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun isDeviceRooted(): Boolean {
        var process: Process? = null
        try {
            process = Runtime.getRuntime().exec("su -c id")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                if (line?.contains("uid=0") == true) {
                    return true
                }
            }
        } catch (e: Exception) {
            return false
        } finally {
            process?.destroy()
        }
        return false
    }

    private fun executeCommand(command: String): String {
        val output = StringBuilder()
        try {
            val process = Runtime.getRuntime().exec(command)
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }
            reader.close()
            process.waitFor()
        } catch (e: Exception) {
            output.append("命令执行失败: ").append(e.message)
        }
        return output.toString()
    }
}
