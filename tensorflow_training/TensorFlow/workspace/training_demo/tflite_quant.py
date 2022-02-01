from os import listdir
from os.path import isfile, join
import glob
import io
import xml.etree.ElementTree as ET
import cv2
import numpy as np
import tensorflow as tf

IMG_FILE_PATH="images/dataset_2/train"

imgFiles = [f for f in listdir(IMG_FILE_PATH) if isfile(join(IMG_FILE_PATH, f)) and (f.endswith('.jpg') or f.endswith('.png'))]

def representative_data_gen(): #boh, per qualche motivo manda il convertitore in errore
    a = []
    i = 0
    for imgFile in imgFiles:
        if i>1000:
            break

        i+=1
        img = cv2.imread(IMG_FILE_PATH + '/' + imgFile)
        img = cv2.resize(img, (320, 320))
        img = img / 255.0
        img = img.astype(np.float32)
        a.append(img)
    a = np.array(a)
    print(a.shape) # a is np array of 160 3D images
    img = tf.data.Dataset.from_tensor_slices(a).batch(1)
    for i in img.take(1000):
        #print(i)
        yield [i]

def representative_data_gen2():
    images = []
    i = 0
    for xml_file in glob.glob(TRAIN_XML_IMAGES_PATH + '/*.xml'):
        if i>500:
            break

        i+=1
        tree = ET.parse(xml_file)
        root = tree.getroot()
        file_name = root.find('filename').text
        images.append(TRAIN_IMAGES_PATH + "/" + file_name)
    dataset_list = tf.data.Dataset.list_files(images)
    for i in range(100):
        image = next(iter(dataset_list))
        image = tf.io.read_file(image)
        image = tf.io.decode_jpeg(image, channels=3)
        image = tf.image.resize(image, [320, 320])
        image = tf.cast(image / 255., tf.float32)
        image = tf.expand_dims(image, 0)
        yield [image]

#_SAVED_MODEL_PATH = "exported-models/dataset_2_binary/5000_steps/my_model_tflite/saved_model" #in
#_TFLITE_MODEL_PATH = "exported-models/dataset_2_binary/5000_steps/my_model_tflite_quantized/model.tflite" #out
#_ODT_LABEL_MAP_PATH = "annotations/dataset_2_binary/label_map.pbtxt" #in
#_TFLITE_LABEL_PATH = "exported-models/dataset_2_binary/5000_steps/my_model_tflite_quantized/tflite_label_map.txt" #out
#_TFLITE_MODEL_WITH_METADATA_PATH = "exported-models/dataset_2_binary/5000_steps/my_model_tflite_quantized/model_with_metadata.tflite" #out

_SAVED_MODEL_PATH = "exported-models/dataset_2_binary/10000_steps/my_model_tflite/saved_model" #in
_TFLITE_MODEL_PATH = "exported-models/dataset_2_binary/10000_steps/my_model_tflite_quantized/model.tflite" #out
_ODT_LABEL_MAP_PATH = "annotations/dataset_2_binary/label_map.pbtxt" #in
_TFLITE_LABEL_PATH = "exported-models/dataset_2_binary/10000_steps/my_model_tflite_quantized/tflite_label_map.txt" #out
_TFLITE_MODEL_WITH_METADATA_PATH = "exported-models/dataset_2_binary/10000_steps/my_model_tflite_quantized/model_with_metadata.tflite" #out
N_CLASSES = 2

#tesorflow lite conversion
converter = tf.lite.TFLiteConverter.from_saved_model(_SAVED_MODEL_PATH)#, signature_keys={'serving_default': {'inputs': ['image'], 'outputs': ['score', 'location', 'number of detections', 'category']}})
converter.optimizations = [tf.lite.Optimize.DEFAULT]
converter.allow_custom_ops = True
converter.experimental_new_converter = True
converter.target_spec.supported_ops = [
  tf.lite.OpsSet.TFLITE_BUILTINS_INT8, # Ensure that if any ops can't be quantized, the converter throws an error
  tf.lite.OpsSet.TFLITE_BUILTINS, # enable TensorFlow Lite ops.
  tf.lite.OpsSet.SELECT_TF_OPS # enable TensorFlow ops.
]
#converter.inference_input_type = tf.uint8
#converter.inference_output_type = tf.uint8
converter.representative_dataset = representative_data_gen
tflite_model = converter.convert()
with open(_TFLITE_MODEL_PATH, 'wb') as f:
    f.write(tflite_model)


#adding metadata
from object_detection.utils import label_map_util
from object_detection.utils import config_util
from object_detection.builders import model_builder

category_index = label_map_util.create_category_index_from_labelmap(_ODT_LABEL_MAP_PATH)
f = open(_TFLITE_LABEL_PATH, 'w')
for class_id in range(1, 1+N_CLASSES):
    if class_id not in category_index:
        f.write('???\n')
        continue

    name = category_index[class_id]['name']
    f.write(name+'\n')

f.close()

from tflite_support.metadata_writers import object_detector
from tflite_support.metadata_writers import writer_utils
writer = object_detector.MetadataWriter.create_for_inference(
    writer_utils.load_file(_TFLITE_MODEL_PATH), input_norm_mean=[127.5],
    input_norm_std=[127.5], label_file_paths=[_TFLITE_LABEL_PATH])
writer_utils.save_file(writer.populate(), _TFLITE_MODEL_WITH_METADATA_PATH)

from tflite_support import metadata
displayer = metadata.MetadataDisplayer.with_model_file(_TFLITE_MODEL_WITH_METADATA_PATH)
print("Metadata populated:")
print(displayer.get_metadata_json())
print("=============================")
print("Associated file(s) populated:")
print(displayer.get_packed_associated_file_list())
