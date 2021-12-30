import os
from os import listdir
from os.path import isfile, join
import random
import re
import math
import shutil
import pandas as pd
from collections import namedtuple
import io
from PIL import Image
import numpy as np
import shutil

#labels: ['balaclava_ski_mask', 'eyeglasses', 'face_no_mask', 'face_other_covering', 'face_shield', 'face_with_mask', 'face_with_mask_incorrect', 'gas_mask', 'goggles', 'hair_net', 'hat', 'helmet', 'hijab_niqab', 'hood', 'mask_colorful', 'mask_surgical', 'other', 'scarf_bandana', 'sunglasses', 'turban']

VISUALIZE_IMGS = False #displays images with groundtruth boxes
FROM = 190
TO = 200

COPY_IMGS_TO_DEST = True #copy the selected images to the destination path
DEST_PATH = "/media/loris/Rock64/Toshiba500GB/tmp/face_mask_dataset_2/selectedImages"

IMG_FILE_PATH = "/media/loris/Rock64/Toshiba500GB/tmp/face_mask_dataset_2/Medical mask/Medical mask/images"
CSV_FILE = "/media/loris/Rock64/Toshiba500GB/tmp/face_mask_dataset_2/train.csv"

imgFiles = [f for f in listdir(IMG_FILE_PATH) if isfile(join(IMG_FILE_PATH, f)) and (f.endswith('.jpg') or f.endswith('.png'))]

df = pd.read_csv(CSV_FILE)
gb = df.groupby("name")
imageData = {filename:gb.get_group(x) for filename, x in zip(gb.groups.keys(), gb.groups)}

color = {
    "face_with_mask": "green",
    "face_no_mask": "red",
    "face_other_covering": "orange", #sometimes no covering at all! sometimes there is some covering + mask ()
    "face_with_mask_incorrect": "yellow"
}

def showImgTensor(image, xminArr, yminArr, xmaxArr, ymaxArr, labelsArr):
    import matplotlib
    matplotlib.use('TkAgg')
    import matplotlib.pyplot as plt
    import matplotlib.patches as patches
    fig, ax = plt.subplots()
    ax.imshow(image)
    i=0
    for label in labelsArr:
        rect = patches.Rectangle((xminArr[i], yminArr[i]), xmaxArr[i]-xminArr[i], ymaxArr[i]-yminArr[i], linewidth=1, edgecolor=color[label], facecolor='none')
        ax.add_patch(rect)
        i+=1
    plt.show()

countFile=0
validImages=0
validBoxes=0
for imageFile in imgFiles:
    try:
        imgData = imageData[imageFile]
    except:
        continue
    if VISUALIZE_IMGS:
        image = np.array(Image.open(IMG_FILE_PATH+"/"+imageFile)).astype(np.float32)/255.0
        width, height = image.shape[1], image.shape[0]
        if countFile<FROM:
            countFile+=1
            continue
        if countFile>TO:
            break
    xminArr = []
    yminArr = []
    xmaxArr = []
    ymaxArr = []
    labelsArr = []
    imageValidBoxes=0
    for index, row in imgData.iterrows():
        if row['classname'] in color:
            xminArr.append(row['x1'])
            yminArr.append(row['x2'])
            xmaxArr.append(row['y1'])
            ymaxArr.append(row['y2'])
            labelsArr.append(row['classname'])
            imageValidBoxes+=1
    #
    if imageValidBoxes<=0:
        continue #this image may contain something weired
    validBoxes+=imageValidBoxes
    validImages+=1
    #
    print("showing" + imageFile)
    if VISUALIZE_IMGS:
        showImgTensor(image, xminArr, yminArr, xmaxArr, ymaxArr, labelsArr)
    if COPY_IMGS_TO_DEST:
        shutil.copyfile(IMG_FILE_PATH+"/"+imageFile , DEST_PATH+"/"+imageFile)

    countFile+=1

print(validImages)
print(validBoxes)
