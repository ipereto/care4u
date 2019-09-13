from torchvision import transforms, utils, datasets, models
import torch
import io
from PIL import Image

class Predict(object):

    def __init__(self, classes = ['chips', 'coke', 'eggs'], 
                       path_model = 'care4u.pth'):
        self.classes = classes
        #classes = ['chips', 'coke', 'eggs']
        self.test_transforms = transforms.Compose([transforms.Resize(224),
                                                transforms.CenterCrop(224),
                                                transforms.ToTensor(),
                                                transforms.Normalize(mean=[0.485, 0.456, 0.406],
                                                        std=[0.229, 0.224, 0.225])
                                            ])
        self.model = torch.load(path_model)
        self.model.eval()

    def transform_image(self, image_bytes):
        image = Image.open(io.BytesIO(image_bytes))
        return self.test_transforms(image).unsqueeze(0)
    
    def predict(self, image_bytes):
        tensor = self.transform_image(image_bytes = image_bytes)
        outputs = self.model.forward(tensor)
        _, y_hat = outputs.max(1)
        return self.classes[y_hat.item()]