 
import time
from object_detection.utils import label_map_util
from object_detection.utils import visualization_utils as viz_utils
import numpy as np
from PIL import Image
import os
from os import listdir
from os.path import isfile, join
import matplotlib
matplotlib.use('TkAgg')
import matplotlib.pyplot as plt
import warnings
import tensorflow as tf
tf.get_logger().setLevel('ERROR')

PATH_TO_MODEL_DIR="/home/loris/Desktop/python_tests/face_mask_detection_proj/tensorflow_training/TensorFlow/workspace/training_demo/exported_models/dataset_2/my_model"
PATH_TO_SAVED_MODEL = PATH_TO_MODEL_DIR + "/saved_model"
PATH_TO_LABELS = "/home/loris/Desktop/python_tests/face_mask_detection_proj/tensorflow_training/TensorFlow/workspace/training_demo/annotations/dataset_2/label_map.pbtxt"

IMG_FILE_PATH = "."
imgFiles = [f for f in listdir(IMG_FILE_PATH) if isfile(join(IMG_FILE_PATH, f)) and (f.endswith('.jpg') or f.endswith('.png'))]

print('Loading model...', end='')
start_time = time.time()
# Load saved model and build the detection function
detect_fn = tf.saved_model.load(PATH_TO_SAVED_MODEL)
end_time = time.time()
elapsed_time = end_time - start_time
print('Done! Took {} seconds'.format(elapsed_time))

category_index = label_map_util.create_category_index_from_labelmap(PATH_TO_LABELS, use_display_name=True)


for imageFile in imgFiles:

    image_path=IMG_FILE_PATH+"/"+imageFile
    print('Running inference for {}... '.format(image_path), end='')

    image_np = np.array(Image.open(image_path)) #load_image_into_numpy_array(image_path)

    # Things to try:
    # Flip horizontally
    # image_np = np.fliplr(image_np).copy()

    # Convert image to grayscale
    # image_np = np.tile(
    #     np.mean(image_np, 2, keepdims=True), (1, 1, 3)).astype(np.uint8)

    # The input needs to be a tensor, convert it using `tf.convert_to_tensor`.
    input_tensor = tf.convert_to_tensor(image_np)
    # The model expects a batch of images, so add an axis with `tf.newaxis`.
    input_tensor = input_tensor[tf.newaxis, ...]

    # input_tensor = np.expand_dims(image_np, 0)
    detections = detect_fn(input_tensor)

    # All outputs are batches tensors.
    # Convert to numpy arrays, and take index [0] to remove the batch dimension.
    # We're only interested in the first num_detections.
    num_detections = int(detections.pop('num_detections'))
    detections = {key: value[0, :num_detections].numpy()
                   for key, value in detections.items()}
    detections['num_detections'] = num_detections

    # detection_classes should be ints.
    detections['detection_classes'] = detections['detection_classes'].astype(np.int64)

    image_np_with_detections = image_np.copy()

    viz_utils.visualize_boxes_and_labels_on_image_array(
          image_np_with_detections,
          detections['detection_boxes'],
          detections['detection_classes'],
          detections['detection_scores'],
          category_index,
          use_normalized_coordinates=True,
          max_boxes_to_draw=200,
          min_score_thresh=.30,
          agnostic_mode=False)

    plt.figure()
    plt.imshow(image_np_with_detections)
    print('Done')
plt.show()

# sphinx_gallery_thumbnail_number = 2
