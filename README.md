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

## Documents:
Project presentation slides: [PresentazioneSistDig.pdf](documents/PresentazioneSistDig.pdf)

Full report: [relazioneEsameSistemiDigitali.pdf](documents/relazioneEsameSistemiDigitali.pdf)


## images/videos:
![before_crossing](https://user-images.githubusercontent.com/42840531/220483289-9acf5aeb-9791-4aac-bba8-6ca923508229.jpg)
![after_crossing](https://user-images.githubusercontent.com/42840531/220483306-1260333c-7f76-46e1-a888-686bc3f2a41d.jpg)
![multiple_people](https://user-images.githubusercontent.com/42840531/220483345-131e5b81-5d8a-412e-8ee9-8a4ee55c50b5.jpg)

https://user-images.githubusercontent.com/42840531/220480746-014c3764-3e37-4a7c-b237-75bb42a96aa4.mp4

https://user-images.githubusercontent.com/42840531/220482039-31768eef-8b49-4887-913e-2c27daa46626.mp4





