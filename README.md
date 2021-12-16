# Face mask detection
## training the model

In this project we took advantage of the [Object detection API](https://tensorflow-object-detection-api-tutorial.readthedocs.io/en/latest/index.html) from TensorFlow 2.
To train this model follow the steps explained here.

#### Download and prepare the data
Downoad this repo:
```sh
git colone https://github.com/LorisGiann/face_mask_detection_proj.git
```
Download the Mask Dataset} from [Kaggle](https://www.kaggle.com/andrewmvd/face-mask-detection) or from (makeml site)[https://makeml.app/datasets/mask] .
Now unpack the archive, you'll notice that for each image in _images_ there is a correspondong .xml file in _annotations_. We need to populate the _train_ and _test_ folders in _tensorflow_training/TensorFlow/workspace/training_demo/images/_, so grab a large portion of the photos with the respective xml files, and put them in the _train_ folder. Then copy the remaining files to the _test_ folder.

#### Install packages
this is a summary of the [installation page of the object detection api]([https://tensorflow-object-detection-api-tutorial.readthedocs.io/en/latest/install.html).
cd to _tensorflow_training_ and follow theese steps:
```sh
cd tensorflow_training
virtualenv -p python3 venv #creates a virtual environment
source venv/bin/activate
pip install --ignore-installed --upgrade tensorflow==2.7.0
python -c "import tensorflow as tf;print(tf.reduce_sum(tf.random.normal([1000, 1000])))" #test tensorflow: may report warnings and info, but terminate correctly
```
We now need to install protobuf. To do that refer to [this paragraph](https://tensorflow-object-detection-api-tutorial.readthedocs.io/en/latest/install.html#protobuf-installation-compilation) of the installation page.
If you are on ubuntu, you can set the invironment with the following commands (please chenge _BASE_PROTOC_DIR_ to match the location of your binary):
```sh
BASE_PROTOC_DIR='/home/loris/bin/google_protobuf/protoc-3.19.1-linux-x86_64'
PROFILE_FILE='export PATH='"$BASE_PROTOC_DIR/bin"':$PATH'"\\\n"
sudo bash -c 'echo -e '"$PROFILE_FILE"' > /etc/profile.d/google_protobuf.sh'
```
Now logout, login and test the setup by typing "protoc" on a terminal: a usage screen should appear. You can also run the following commands to make extra sure things are working properly with protobuf:
```sh
cd TensorFlow/models/research
protoc object_detection/protos/*.proto --python_out=. #test protobuf
cd -
```
Now we install pycoctools, which will be useful to benchmark the network at finding objects.
```sh
cd TensorFlow/models/research
git clone https://github.com/cocodataset/cocoapi.git #already included in this repo
cd cocoapi/PythonAPI
make
cp -r pycocotools ../..
cd ../..
python -m pip install --use-feature=2020-resolver .
python object_detection/builders/model_builder_tf2_test.py #test the installation
```
#### Generating tfrecords
Tensorflow object detection api needs image files and labels to be in a .tfrecord file format. We can use the custom modified script to generate those starting from the train and test folders we previously filled up.
```sh
cd TensorFlow/workspace/training_demo
python generate_tfrecord.py -x images/train -l annotations/label_map.pbtxt -o annotations/train.record
python generate_tfrecord.py -x images/test -l annotations/label_map.pbtxt -o annotations/test.record
```
NOTE: this scripts also crops and add paddigng to images to make them 230x230 (which is not done in the original script in the guide).
You can see a few images inside those files by using the _analyze_ script (just modify the .tfrecord file and the image index at the beginning of the script).
```sh
#sudo apt-get install python3-tk  #you may need this
python3 analyze.py
```

#### Training process
It's time to train our network! Use the command:
```sh
#cd TensorFlow/workspace/training_demo
python model_main_tf2.py --model_dir=models/ssd_mobilenet_v2_fpnlite_320x320_coco --pipeline_config_path=models/ssd_mobilenet_v2_fpnlite_320x320_coco/pipeline.config
```
You can monitor the progress of your network using pycocotools and/or Tensorboard:
```sh
python model_main_tf2.py --model_dir=models/ssd_mobilenet_v2_fpnlite_320x320_coco --pipeline_config_path=models/ssd_mobilenet_v2_fpnlite_320x320_coco/pipeline.config --checkpoint_dir=models/ssd_mobilenet_v2_fpnlite_320x320_coco
tensorboard --logdir=models/ssd_mobilenet_v2_fpnlite_320x320_coco --bind #http://localhost:6006/
```
#### Exporting the model
To export the model in pb format, use the command:
```sh
python ./exporter_main_v2.py --input_type image_tensor --pipeline_config_path ./models/ssd_mobilenet_v2_fpnlite_320x320_coco/pipeline.config --trained_checkpoint_dir ./models/ssd_mobilenet_v2_fpnlite_320x320_coco/ --output_directory ./exported-models/my_model
```
#### Export in tflite format
To export the model in tflite format, you first need to use this command (it generates an intermediate SavedModel which can then be used with the TFLite Converter)
```sh
python export_tflite_graph_tf2.py --pipeline_config_path=./models/ssd_mobilenet_v2_fpnlite_320x320_coco/pipeline.config --trained_checkpoint_dir=./models/ssd_mobilenet_v2_fpnlite_320x320_coco/ --output_directory=./exported-models/my_model_tflite
```
Once this is done, we install a python package, then we can use a ptyhon3 shell to paste the blocks of code presented here:
```sh
pip install tflite-support
python3
```
This is the python code (chenge the path constant in capital letters if you need). The script can also be adapted to use quantization, or other options, see this [google colab sketch](https://colab.research.google.com/github/tensorflow/models/blob/master/research/object_detection/colab_tutorials/convert_odt_model_to_TFLite.ipynb).
