# Face mask detection
This repository is for the project of the "Sistemi digitali M" (Digital Systems M) master course exam at Unibo.

In this project, we took advantage of TensorFlow 2 [object detection API](https://tensorflow-object-detection-api-tutorial.readthedocs.io/en/latest/index.html) to keep track of the number of people entering an environment with and without a protective facial mask against COVID-19 virus.

First, a neural network (an _SSD MobileNet V2
FPNLite 320x320_) has been trained to locate and distinguish between 2 different categories of objects:
- human faces (without a mask)
- human faces wearing a mask

To do that the following datasets have been used:
- https://www.kaggle.com/andrewmvd/face-mask-detection
- https://www.kaggle.com/wobotintelligence/face-mask-detection-dataset

Then, a simple - yet effective - tracking algorithm has been implemented in order to follow the movements of the objects over successive frames. This way we can determine whether a person enters the room, incrementing the counter corresponding to the object category if a virtual line is crossed.

## Documents & images/videos:
Project presentation slides: [PresentazioneSistDig.pdf](documents/PresentazioneSistDig.pdf)

Full report: [relazioneEsameSistemiDigitali.pdf](documents/relazioneEsameSistemiDigitali.pdf)

![before_crossing](https://user-images.githubusercontent.com/42840531/220480114-2aa83b1a-e254-466b-bb28-186a43795bc3.png)
![after_crossing](https://user-images.githubusercontent.com/42840531/220480139-f82c58cf-b625-4389-9653-d9dd00c06121.png)
![multiple_people](https://user-images.githubusercontent.com/42840531/220480155-98d1784b-c754-4085-a317-35c096693a98.png)

![single_passing_with_and_without_mask](https://user-images.githubusercontent.com/42840531/220480746-014c3764-3e37-4a7c-b237-75bb42a96aa4.mp4)
![multiple_passing_with_and_without_mask](https://user-images.githubusercontent.com/42840531/220480392-1a7d6244-7a6a-4b00-987b-e1690efea9c1.png)




