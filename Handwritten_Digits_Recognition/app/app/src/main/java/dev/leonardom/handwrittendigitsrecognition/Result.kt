package dev.leonardom.handwrittendigitsrecognition

class Result(
    private var probs: FloatArray,
    private var timeCost: Long
) {

    var mNumber = argmax(probs)
    var mProbability = probs[mNumber]
    var mTimeCost = timeCost

    private fun argmax(probs: FloatArray): Int {
        var maxIdx = -1
        var maxProb = 0.0f
        for(i in probs.indices) {
            if(probs[i] > maxProb){
                maxProb = probs[i]
                maxIdx = i
            }
        }

        return maxIdx
    }

}