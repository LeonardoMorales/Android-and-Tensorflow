# -*- coding: utf-8 -*-
"""
Created on Mon Feb  1 10:45:06 2021

@author: leona
"""

import tensorflow as tf

@tf.function
def pow(x):
    return x ** 2

#Construct a basic model
root = tf.train.Checkpoint()
root.power = pow

#Create the concrate function
input_data = tf.constant(1., shape=[1])
concrete_function = root.power.get_concrete_function(input_data)

#Converte the model
converter = tf.lite.TFLiteConverter.from_concrete_functions([concrete_function])
tflite_model = converter.convert()
open('concrete_function.tflite', 'wb').write(tflite_model)