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
