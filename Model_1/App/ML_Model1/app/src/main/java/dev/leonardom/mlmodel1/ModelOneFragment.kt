package dev.leonardom.mlmodel1

import android.content.res.AssetFileDescriptor
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import dev.leonardom.mlmodel1.databinding.FragmentModelOneBinding
import dev.leonardom.mlmodel1.ml.LinearModel
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.tensorflow.lite.support.tensorbuffer.TensorBufferFloat
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel


class ModelOneFragment : Fragment() {

    private var _binding: FragmentModelOneBinding? = null
    private val binding get() = _binding!!

    private lateinit var interpreter: Interpreter

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentModelOneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            interpreter = Interpreter(loadModelFile(), null)
        } catch (e: Exception){
            Toast.makeText(requireContext(), "Error cargando modelo de ML", Toast.LENGTH_SHORT).show()
            Toast.makeText(requireContext(), "Exception: $e", Toast.LENGTH_SHORT).show()
        }

        binding.btn.setOnClickListener {
            val num = binding.textInputEditText.text.toString()
            val f = doInference(num)
            binding.textView2.text = "y = $f"
            binding.textInputEditText.setText("")
        }

        /*binding.btn.setOnClickListener {
            val num = binding.textInputEditText.text.toString()
            val f = newWayML(num)
            binding.textView2.text = "y = $f"
            binding.textInputEditText.setText("")
        }*/

    }

    /*private fun newWayML(num: String): Float {
        val linearModel = LinearModel.newInstance(requireContext())

        val input = IntArray(1)
        input[0] = num.toInt()
        Log.d("ModelOneFragment", "Size: ${input.size}")

        val tensorBuffer = TensorBuffer.createDynamic(DataType.INT32)
        tensorBuffer.loadArray(input)

        val outputs = linearModel.process(tensorBuffer)
        return outputs.outputFeature0AsTensorBuffer.floatArray[0]
    }*/

    @Throws(IOException::class)
    private fun loadModelFile(): MappedByteBuffer {
        val assetFileDescriptor: AssetFileDescriptor = requireActivity().assets.openFd("linear.tflite")
        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel: FileChannel = fileInputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val length = assetFileDescriptor.length
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, length)
    }

    private fun doInference(num: String): Float {
        val input = FloatArray(1)
        input[0] = num.toFloat()

        val output = Array(1) { FloatArray(1) }
        interpreter.run(input, output)

        return output[0][0]
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}