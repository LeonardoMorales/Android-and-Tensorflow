package dev.leonardom.handwrittendigitsrecognition

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dev.leonardom.handwrittendigitsrecognition.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var classifier: Classifier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        classifier = Classifier(this)

        binding.btnDetect.setOnClickListener {
            val bitmap = binding.fingerPaintView.exportToBitmap(Classifier.IMAGE_WIDTH, Classifier.IMAGE_HEIGHT)
            val result = classifier.classify(bitmap)

            binding.textViewPredictionValue.text = "${result.mNumber}"
            binding.textViewProbabilityValue.text = "${result.mProbability}"
            binding.textViewTimeCostValue.text = "${result.mTimeCost}"
        }

        binding.btnClear.setOnClickListener {
            binding.fingerPaintView.clear()
            binding.textViewPredictionValue.text = ""
            binding.textViewProbabilityValue.text = ""
            binding.textViewTimeCostValue.text = ""
        }
    }
}