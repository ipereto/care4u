# CARE4U
## Inspiration
Some of us suffer an illness related to bad feed habits, and sometimes we don’t know exactly what we are eating and what are the consequences of eating a certain kind of food. That’s why we believe that we need to know, using care4u, how healthy or not the food we are eating every day and if it probability can worst our illnesses like high pressure, diabetes, obesity and others.

## What it does
Care4u identifies through the camera how healthy is the food you are about to eat regarding your current health condition that you already let us know in the app at the beginning in a form, this means it keeps the tracking for different kinds of nutrients, fat, cholesterol, and others of what you want to eat. In this way, in order to what might be bad food for your health we can create an alert of the product.

## How we built it
We have an application with a login view with Facebook, a form and the widget to scan the food with a camera, all this was built using Kotlin and a CNN - RestNet50 has an endpoint which receives a picture to be scanned and the algorithm identifies with some datasets of images we set up previously and it matches regarding food’s properties if that food is bad or not for your current health.

## Challenges we ran into
We ran into some challenges for us like the deadline because we started with 14 days left. On the other hand, the technology we were integrating like android, PyTorch and the building of datasets as an input for the algorithm in PyTorch was a challenge for us because it was something new that we had never done before. Finally, working in a team always will be a challenge we had to sync our work daily in order to take advance every day in the project.

## Accomplishments that we're proud of
We were able to sync our talents in order to finish the project in the established deadline and despite the obstacles we had working as a team were face them with courage.

## What we learned
* We learned how to build a CNN with pytorch.
* How to train the CNN to identify from datasets previously build them to identify real objects.
* How to integrate the CNN with an App which works to get the input information for the CNN.

## What's next for CARE4U
Next for CARE4U is adding the function to detect decomposed food in order to prevent the user to eat it and the function to integrate with real clinical historial.

## Built With
1. App was built with [ionic framework](https://ionicframework.com/).
2. CNN was built with [pytorch](https://pytorch.org/).
3. Datasets were built with images found in [Google images](https://www.google.com.co/imghp?hl=en).
