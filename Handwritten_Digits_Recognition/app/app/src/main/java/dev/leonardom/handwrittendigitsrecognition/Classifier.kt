package dev.leonardom.handwrittendigitsrecognition

import android.app.Activity
import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.widget.Toast
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class Classifier(activity: Activity) {

    companion object {
        const val MODEL_NAME = "digit.tflite"
        const val BATCH_SIZE = 1 // We'll display only one image
        const val IMAGE_HEIGHT = 28 // Pixel's Height of the image
        const val IMAGE_WIDTH = 28 // Pixel's Width of the image
        const val NUM_CHANNEL = 1 // Color Channel (Green Channel)
        const val NUM_CLASSES = 10 // Num Labels - Digits from 0 to 9
    }

    private val options = Interpreter.Options()
    private lateinit var interpreter: Interpreter
    private lateinit var imageData: ByteBuffer
    private val imagePixels = IntArray(IMAGE_HEIGHT * IMAGE_WIDTH)

    private val result = Array(1){ FloatArray(NUM_CLASSES) }

    init {
        try {
            interpreter = Interpreter(loadModelFile(activity), options)
            imageData = ByteBuffer.allocateDirect(4 * BATCH_SIZE * IMAGE_HEIGHT * IMAGE_WIDTH * NUM_CHANNEL)
            imageData.order(ByteOrder.nativeOrder())
        } catch (exception: Exception) {
            Toast.makeText(activity, "Error cargando modelo de ML", Toast.LENGTH_SHORT).show()
            Toast.makeText(activity, "Exception: $exception", Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    private fun loadModelFile(activity: Activity): MappedByteBuffer {
        val assetFileDescriptor: AssetFileDescriptor = activity.assets.openFd(MODEL_NAME)
        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel: FileChannel = fileInputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val length = assetFileDescriptor.length
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, length)
    }

    fun classify(bitmap: Bitmap): Result {
        convertBitmapToByteBuffer(bitmap)
        interpreter.run(imageData, result)

        return Result(result[0], 0L)
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap) {
        imageData.rewind()
        bitmap.getPixels(
            imagePixels, 0, bitmap.width, 0, 0,
            bitmap.width, bitmap.height
        )
        var pixel = 0
        for (i in 0 until IMAGE_WIDTH) {
            for (j in 0 until IMAGE_HEIGHT) {
                val value: Int = imagePixels[pixel++]
                imageData.putFloat(convertPixel(value))
            }
        }
    }

    private fun convertPixel(color: Int): Float {
        return (255 - ((color shr 16 and 0xFF) * 0.299f + (color shr 8 and 0xFF) * 0.587f + (color and 0xFF) * 0.114f)) / 255.0f
    }

}










