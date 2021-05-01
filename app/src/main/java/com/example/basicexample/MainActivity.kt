package com.example.basicexample

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class MainActivity : AppCompatActivity() {
    var interpreter: Interpreter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            interpreter = Interpreter(loadModelFile()!!)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val result: TextView = findViewById(R.id.textView)
        val editText: EditText = findViewById(R.id.editText)
        val run: Button = findViewById(R.id.button)

        run.setOnClickListener {
            val f = doInference(editText.text.toString())
            result.text = f.toString() + ""
        }
    }

    fun doInference(value: String): Float {
        val input = FloatArray(1)
        input[0] = value.toFloat()
        val output =  Array(1) { FloatArray(1) }
        interpreter?.run(input, output)
        return output[0][0]
    }

    @Throws(IOException::class)
    private fun loadModelFile(): MappedByteBuffer? {
        val assetFileDescriptor = this.assets.openFd("linear.tflite")
        val fileInputStream =
            FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = fileInputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val length = assetFileDescriptor.length
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, length)
    }

}