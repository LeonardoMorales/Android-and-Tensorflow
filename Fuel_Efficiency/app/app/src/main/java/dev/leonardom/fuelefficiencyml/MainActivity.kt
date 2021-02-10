package dev.leonardom.fuelefficiencyml

import android.content.res.AssetFileDescriptor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import dev.leonardom.fuelefficiencyml.databinding.ActivityMainBinding
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val mean = arrayListOf(5.477707f, 195.318471f, 104.869427f, 2990.251592f, 15.559236f, 75.898089f, 0.624204f, 0.178344f, 0.197452f)
    private val std = arrayListOf(1.699788f, 104.331589f, 38.096214f, 843.898596f, 2.789230f, 3.675642f, 0.485101f, 0.383413f, 0.398712f)

    private lateinit var interpreter: Interpreter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            interpreter = Interpreter(loadModelFile(), null)
        } catch(exception: Exception) {
            Toast.makeText(this, "Error cargando modelo de ML", Toast.LENGTH_SHORT).show()
            Toast.makeText(this, "Exception: $exception", Toast.LENGTH_SHORT).show()
        }

        setupAutoCompleteTextView()

        binding.btnPredict.setOnClickListener {
            val input = normalizeValues()
            val result = doInference(input).toString()

            binding.textViewResult.text = result
        }

    }

    private fun normalizeValues(): Array<FloatArray> {
        val floats = Array(1){ FloatArray(9) }
        floats[0][0] = ((binding.textInputEditTextCylinders.text.toString().toFloat() - mean[0])/std[0])
        floats[0][1] = ((binding.textInputEditTextDisplacement.text.toString().toFloat() - mean[1])/std[1])
        floats[0][2] = ((binding.textInputEditTextHorsePower.text.toString().toFloat() - mean[2])/std[2])
        floats[0][3] = ((binding.textInputEditTextWeight.text.toString().toFloat() - mean[3])/std[3])
        floats[0][4] = ((binding.textInputEditTextAcceleration.text.toString().toFloat() - mean[4])/std[4])
        floats[0][5] = ((binding.textInputEditTextModelYear.text.toString().toFloat() - mean[5])/std[5])

        when(binding.autoCompleteTextViewOrigin.editableText.toString()){
            "USA" -> {
                floats[0][6] = (1 - mean[6]) / std[6]
                floats[0][7] = (0 - mean[7]) / std[7]
                floats[0][8] = (0 - mean[8]) / std[8]
            }

            "Europe" -> {
                floats[0][6] = (0 - mean[6]) / std[6]
                floats[0][7] = (1 - mean[7]) / std[7]
                floats[0][8] = (0 - mean[8]) / std[8]
            }

            "Japan" -> {
                floats[0][6] = (0 - mean[6]) / std[6]
                floats[0][7] = (0 - mean[7]) / std[7]
                floats[0][8] = (1 - mean[8]) / std[8]
            }
        }

        return floats
    }

    private fun setupAutoCompleteTextView() {
        val items = listOf("USA", "Europe", "Japan")
        val adapter = ArrayAdapter(this, R.layout.list_item, items)
        (binding.autoCompleteTextViewOrigin as? AutoCompleteTextView)?.setAdapter(adapter)
    }

    private fun doInference(input: Array<FloatArray>): Float {
        val output = Array(1){ FloatArray(1) }
        interpreter.run(input, output)

        return output[0][0]
    }

    @Throws(IOException::class)
    private fun loadModelFile(): MappedByteBuffer {
        val assetFileDescriptor: AssetFileDescriptor = this.assets.openFd("automobile.tflite")
        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel: FileChannel = fileInputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val length = assetFileDescriptor.length
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, length)
    }
}