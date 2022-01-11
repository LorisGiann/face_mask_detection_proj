""" Sample TensorFlow CSV-to-TFRecord converter

usage: generate_tfrecord.py [-h] [-c CSV_DIR] [-l LABELS_PATH] [-o OUTPUT_PATH] [-i IMAGE_DIR] [-c CSV_PATH]

optional arguments:
  -h, --help            show this help message and exit
  -c CSV_DIR, --csv_dir XML_DIR
                        Path to the folder where the input .csv files are stored.
  -l LABELS_PATH, --labels_path LABELS_PATH
                        Path to the labels (.pbtxt) file.
  -o OUTPUT_PATH, --output_path OUTPUT_PATH
                        Path of output TFRecord (.record) file.
  -i IMAGE_DIR, --image_dir IMAGE_DIR
                        Path to the folder where the input image files are stored. Defaults to the same directory as CSV_DIR.
"""

import os
import glob
import pandas as pd
import io
import xml.etree.ElementTree as ET
import argparse
import numpy as np
from os import listdir
from os.path import isfile, join

os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'    # Suppress TensorFlow logging (1)
import tensorflow.compat.v1 as tf
from PIL import Image
from object_detection.utils import dataset_util, label_map_util
from collections import namedtuple

import functools
from object_detection import inputs
from object_detection.core import preprocessor
from object_detection.core import standard_fields as fields
from object_detection.utils import config_util
from object_detection.utils import test_case

#IMG_FILE_PATH = "/media/loris/Rock64/Toshiba500GB/tmp/face_mask_dataset_2/selectedImages"
#CSV_FILE = "/media/loris/Rock64/Toshiba500GB/tmp/face_mask_dataset_2/train.csv"

# Initiate argument parser
parser = argparse.ArgumentParser(
    description="Sample TensorFlow CSV-to-TFRecord converter")
parser.add_argument("-c",
                    "--csv_dir",
                    help="Path to the folder where the input .csv file is stored.",
                    type=str)
parser.add_argument("-l",
                    "--labels_path",
                    help="Path to the labels (.pbtxt) file.", type=str)
parser.add_argument("-o",
                    "--output_path",
                    help="Path of output TFRecord (.record) file.", type=str)
parser.add_argument("-i",
                    "--image_dir",
                    help="Path to the folder where the input image files are stored. "
                         "Defaults to the same directory as CSV_DIR.",
                    type=str, default=None)
args = parser.parse_args()

if args.image_dir is None:
    args.image_dir = args.csv_dir

label_map = label_map_util.load_labelmap(args.labels_path)
label_map_dict = label_map_util.get_label_map_dict(label_map)
labelMapper = { #converts the original labels in the dataset in less labes types
    "face_with_mask": "mask_ok",
    "face_no_mask": "no_mask",
    "face_other_covering": "no_mask",
    "face_with_mask_incorrect": "mask_ok"
}

newWidth = 320
newHeight = 320
# ---------------------------------------------- IMAGE PREPROCESSING ---------------------------------------------------------
data_augmentation_options = [
    (preprocessor.random_horizontal_flip, { #boh sembra non funzionare
    }),
    #(preprocessor.resize_image, {
    #    'new_height': 320,
    #    'new_width': 320
    #}),
    (preprocessor.resize_to_range, {
        'min_dimension': newWidth,
        'max_dimension': newHeight,
        'pad_to_max_dimension': False, #looks like this option does not adjust the boxes positions! to add padding we use the options down here
        'method': 'area'
    }),
    (preprocessor.random_pad_image, {
        'min_image_size': (newWidth,newHeight),
        'max_image_size': (newWidth,newHeight),
        'center_pad': True
    }),
    #(preprocessor.random_crop_image, {
    #    #'min_object_covered': 0.0,
    #    'min_object_covered': 1.0,
    #    #'aspect_ratio_range': (0.75,3.0),
    #    #'area_range': (0.75, 1.0),
    #    'aspect_ratio_range': (1,1),
    #    'area_range': (1, 1),
    #    'overlap_thresh': 0.0
    #})
]
#---------------------------------------------------------------------------------------------------------------------------------

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
        rect = patches.Rectangle((box[1]*w, box[0]*h), (box[3]-box[1])*w, (box[2]-box[0])*h, linewidth=1, edgecolor=(cl[1], cl[0], 0), facecolor='none')
        ax.add_patch(rect)
        count+=1
    plt.show()

def class_text_to_int(row_label):
    return label_map_dict[row_label]

def create_tf_example(filePath, imgData):
    with tf.gfile.GFile(filePath, 'rb') as fid:
        encoded_jpg = fid.read()
    encoded_jpg_io = io.BytesIO(encoded_jpg)
    image = Image.open(encoded_jpg_io)
    width, height = image.size

    filename = os.path.basename(filePath)
    image_format = b'jpg'
    xmins = []
    xmaxs = []
    ymins = []
    ymaxs = []
    classes_text = []
    classes = []

    imageValidBoxes=0
    for index, row in imgData.iterrows():
        if row['classname'] in labelMapper:
            xmins.append(row['x1'] / width)
            xmaxs.append(row['y1'] / width)
            ymins.append(row['x2'] / height)
            ymaxs.append(row['y2'] / height)
            classname = labelMapper[row['classname']]
            classes_text.append(classname.encode('utf8'))
            classes.append(class_text_to_int(classname))
            imageValidBoxes+=1
    if imageValidBoxes<=0:
        return None #this image does not contain any box that we need!

    boxes = zip(ymins, xmins, ymaxs, xmaxs)
    tensor_dict = {
        fields.InputDataFields.image: tf.constant(np.array(image).astype(np.float32)/255.0),
        fields.InputDataFields.groundtruth_boxes: tf.constant(np.array(list(boxes), np.float32)),
        fields.InputDataFields.groundtruth_classes: tf.one_hot(np.array(classes)-1 , len(label_map_dict))
    }
    #showImgTensor(tensor_dict)
    data_augmentation_fn = functools.partial(inputs.augment_input_data, data_augmentation_options=data_augmentation_options)
    augmented_tensor_dict = data_augmentation_fn(tensor_dict=tensor_dict)
    image=augmented_tensor_dict[fields.InputDataFields.image].numpy()*255
    boxes=augmented_tensor_dict[fields.InputDataFields.groundtruth_boxes].numpy()
    new_classes=augmented_tensor_dict[fields.InputDataFields.groundtruth_classes].numpy()

    classes = []
    classes_text = []
    for cl in new_classes:
        tmp=0
        for j in range(len(cl)):
            if cl[j]==1.0:
                tmp=j
        classes.append(tmp+1)
        for i,num in enumerate(label_map_dict.values()):
            if num==tmp+1:
                break
        classes_text.append(list(label_map_dict.keys())[i].encode('utf8'))

    tf_example = tf.train.Example(features=tf.train.Features(feature={
        'image/height': dataset_util.int64_feature(newHeight),
        'image/width': dataset_util.int64_feature(newWidth),
        'image/filename': dataset_util.bytes_feature(filename.encode('utf8')),
        'image/source_id': dataset_util.bytes_feature(filename.encode('utf8')),
        'image/encoded': dataset_util.bytes_feature(tf.io.encode_png(image.astype(np.uint8)).numpy()),
        'image/format': dataset_util.bytes_feature(image_format),
        'image/object/bbox/xmin': dataset_util.float_list_feature(boxes[...,1]),
        'image/object/bbox/xmax': dataset_util.float_list_feature(boxes[...,3]),
        'image/object/bbox/ymin': dataset_util.float_list_feature(boxes[...,0]),
        'image/object/bbox/ymax': dataset_util.float_list_feature(boxes[...,2]),
        'image/object/class/text': dataset_util.bytes_list_feature(classes_text),
        'image/object/class/label': dataset_util.int64_list_feature(classes),
    }))
    return tf_example


def main(_):
    writer = tf.python_io.TFRecordWriter(args.output_path)
    imgFiles = [f for f in listdir(args.image_dir) if isfile(join(args.image_dir, f)) and (f.endswith('.jpg') or f.endswith('.png')) ]

    df = pd.read_csv(args.csv_dir)
    gb = df.groupby("name")
    imageData = {filename:gb.get_group(x) for filename, x in zip(gb.groups.keys(), gb.groups)}

    for imageFile in imgFiles:
        tf_example = create_tf_example(args.image_dir+"/"+imageFile, imageData[imageFile])
        if (tf_example is not None):
            writer.write(tf_example.SerializeToString())
    writer.close()
    print('Successfully created the TFRecord file: {}'.format(args.output_path))

if __name__ == '__main__':
    tf.app.run()
