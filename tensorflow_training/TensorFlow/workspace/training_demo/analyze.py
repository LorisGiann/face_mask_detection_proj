import os
import random
import re
import math
import shutil
import pandas as pd
from collections import namedtuple
import tensorflow.compat.v1 as tf
import io
from PIL import Image
from object_detection.utils import dataset_util, label_map_util
import numpy as np

import functools
from object_detection import inputs
from object_detection.core import preprocessor
from object_detection.core import standard_fields as fields
from object_detection.utils import config_util
from object_detection.utils import test_case

os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'    # Suppress TensorFlow logging (1)

num_classes = 2
image_num = 5#66
#attenzione: se l'immagine non contine l'oggetto viene lanciata eccezione

TFRecords_file = "annotations/dataset_2_binary/test.record" #output file for TF record file

def parse_record(record):
    name_to_features = {
        'image/height': tf.io.FixedLenFeature([], tf.int64),
        'image/width': tf.io.FixedLenFeature([], tf.int64),
        'image/filename': tf.io.FixedLenFeature([], tf.string, default_value=''),
        'image/source_id': tf.io.FixedLenFeature([], tf.string, default_value=''),
        'image/encoded': tf.io.FixedLenFeature([], tf.string, default_value=''),
        'image/format': tf.io.FixedLenFeature([], tf.string, default_value=''),
        'image/object/bbox/xmin': tf.io.VarLenFeature(dtype=tf.float32),#VarLenFeature(dtype=tf.float32),
        'image/object/bbox/xmax': tf.io.VarLenFeature(dtype=tf.float32),#VarLenFeature(dtype=tf.float32),
        'image/object/bbox/ymin': tf.io.VarLenFeature(dtype=tf.float32),#VarLenFeature(dtype=tf.float32),
        'image/object/bbox/ymax': tf.io.VarLenFeature(dtype=tf.float32),#VarLenFeature(dtype=tf.float32),
        'image/object/class/text': tf.io.VarLenFeature(tf.string),
        'image/object/class/label': tf.io.VarLenFeature(tf.int64),
    }
    return tf.io.parse_single_example(record, name_to_features)

def _decode_image(parsed_record):
    """Decodes the image and set its static shape."""
    image = tf.io.decode_image(parsed_record['image/encoded'], channels=3)
    image.set_shape([None, None, 3])
    return image

def _decode_boxes(parsed_record):
    """Concat box coordinates in the format of [ymin, xmin, ymax, xmax]."""
    xmin = parsed_record['image/object/bbox/xmin'].values.numpy()
    xmax = parsed_record['image/object/bbox/xmax'].values.numpy()
    ymin = parsed_record['image/object/bbox/ymin'].values.numpy()
    ymax = parsed_record['image/object/bbox/ymax'].values.numpy()
    return zip(ymin, xmin, ymax, xmax)


def decode_record(parsed_record):
    image = _decode_image(parsed_record)
    w, h = image.shape[1], image.shape[0]
    boxes = _decode_boxes(parsed_record)
    #fig, ax = plt.subplots()
    #ax.imshow(image.numpy())
    #for box in boxes:
    #    rect = patches.Rectangle((box[1]*w, box[0]*h), (box[3]-box[1])*w, (box[2]-box[0])*h, linewidth=1, edgecolor='r', facecolor='none')
    #    ax.add_patch(rect)
    #plt.show()
    tensor_dict = {
        fields.InputDataFields.image: tf.constant(image.numpy().astype(np.float32)/255.0),
        fields.InputDataFields.groundtruth_boxes: tf.constant(np.array(np.array(list(boxes), np.float32))),
        fields.InputDataFields.groundtruth_classes: tf.one_hot( parsed_record['image/object/class/label'].values.numpy()-1, num_classes)
    }
    return tensor_dict

def showImgTensor(tensor_dict):
    image=tensor_dict[fields.InputDataFields.image]
    boxes=tensor_dict[fields.InputDataFields.groundtruth_boxes]
    classes=tensor_dict[fields.InputDataFields.groundtruth_classes]
    w, h = image.shape[1], image.shape[0]
    import matplotlib
    matplotlib.use('TkAgg')
    import matplotlib.pyplot as plt
    import matplotlib.patches as patches
    fig, ax = plt.subplots()
    ax.imshow(image)
    #print(tf.io.encode_png((image.numpy()*255).astype(np.uint8)).numpy()[:100])

    count=0
    for box in boxes:
        cl=classes[count].numpy()
        rect = patches.Rectangle((box[1]*w, box[0]*h), (box[3]-box[1])*w, (box[2]-box[0])*h, linewidth=1, edgecolor=(cl[1], cl[0], 0, 1), facecolor='none')
        ax.add_patch(rect)
        count+=1
    plt.show()



dataset = tf.data.TFRecordDataset(TFRecords_file, buffer_size=100)
FLAGS = tf.flags.FLAGS
#tf.disable_eager_execution()

for record in dataset.take(image_num):
    parsed_record = parse_record(record)

tensor_dict=decode_record(parsed_record)
#showImgTensor(tensor_dict)
class DataAugmentationFnTest(test_case.TestCase):
    def test_apply_image_and_box_augmentation(self):
        data_augmentation_options = [
            (preprocessor.random_horizontal_flip, { #boh sembra non funzionare
            }),
            #(preprocessor.resize_to_range, {
            #    'min_dimension': 320,
            #    'max_dimension': 320,
            #    'pad_to_max_dimension': False #looks like this option does not adjust the boxes #positions! to add padding we use the options down here
            #}),
            #(preprocessor.random_pad_image, {
            #    'min_image_size': (320,320),
            #    'max_image_size': (320,320),
            #    'center_pad': True
            #}),
            #(preprocessor.random_crop_image, {
            #    'min_object_covered': 0.0,
            #    #'aspect_ratio_range': (0.75,3.0),
            #    'area_range': (0.3, 1),
            #    'aspect_ratio_range': (0.8,1.2),
            #    'overlap_thresh': 0.3
            #}),
            #(preprocessor.resize_image, {
            #    'new_height': 320,
            #    'new_width': 320,
            #    'method': 'area'
            #})
        ]
        data_augmentation_fn = functools.partial(inputs.augment_input_data, data_augmentation_options=data_augmentation_options)
        augmented_tensor_dict = data_augmentation_fn(tensor_dict=tensor_dict)
        #with self.session() as sess:
        #    augmented_tensor_dict_out = sess.run(augmented_tensor_dict)
        showImgTensor(augmented_tensor_dict)

tf.test.main()





