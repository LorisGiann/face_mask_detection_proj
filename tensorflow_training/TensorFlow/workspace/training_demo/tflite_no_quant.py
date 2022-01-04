import tensorflow as tf
_SAVED_MODEL_PATH = "exported-models/dataset_2_binary/10000_steps/my_model_tflite/saved_model" #in
_TFLITE_MODEL_PATH = "exported-models/dataset_2_binary/10000_steps/my_model_tflite/model.tflite" #out
_ODT_LABEL_MAP_PATH = "annotations/dataset_2_binary/label_map.pbtxt" #in
_TFLITE_LABEL_PATH = "exported-models/dataset_2_binary/10000_steps/my_model_tflite/tflite_label_map.txt" #out
_TFLITE_MODEL_WITH_METADATA_PATH = "exported-models/dataset_2_binary/10000_steps/my_model_tflite/model_with_metadata.tflite" #out

#_SAVED_MODEL_PATH = "exported-models/dataset_2_binary/5000_steps/my_model_tflite/saved_model" #in
#_TFLITE_MODEL_PATH = "exported-models/dataset_2_binary/5000_steps/my_model_tflite/model.tflite" #out
#_ODT_LABEL_MAP_PATH = "annotations/dataset_2_binary/label_map.pbtxt" #in
#_TFLITE_LABEL_PATH = "exported-models/dataset_2_binary/5000_steps/my_model_tflite/tflite_label_map.txt" #out
#_TFLITE_MODEL_WITH_METADATA_PATH = "exported-models/dataset_2_binary/5000_steps/my_model_tflite/model_with_metadata.tflite" #out


converter = tf.lite.TFLiteConverter.from_saved_model(_SAVED_MODEL_PATH)#, signature_keys={'serving_default': {'inputs': ['image'], 'outputs': ['score', 'location', 'number of detections', 'category']}})
#converter.optimizations = [tf.lite.Optimize.DEFAULT]
tflite_model = converter.convert()
with open(_TFLITE_MODEL_PATH, 'wb') as f:
      f.write(tflite_model)

#adding metadata
from object_detection.utils import label_map_util
from object_detection.utils import config_util
from object_detection.builders import model_builder
N_CLASSES = 2

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
