# AirGazer
## Description
Background: Air quality has become a big problem not only for people with lung illness, heart disease, and the elderly, but also for the young and fit generation. Without a doubt, technology is progressing and concerned authorities are taking initiatives to improve air quality at their level, but at some point, the air quality degrades so fast that long-term exposure causes respiratory sickness.
Objective: This work has come up with a solution to indoor bad air quality. An android application, AirGazer is used to monitor Indoor Air Quality in real-time. The data is obtained from an IoT system having an MQ135 sensor.
Methods: IoT model consisting of MQ135 sensor and ESP8266 WiFi Module to track Air Quality in real-time. Android application to monitor the air quality with the data obtained from the IoT model. Firebase for data storage. Machine Learning for AQI and AQI Bucket prediction. Dialogflow chatbot to answer general air pollution and air quality-related queries.
Results: The AQI was updated regularly on the app for smooth monitoring. The user was sent an alert when AQI crossed 300 ppm. AQI bucket and AQI prediction had an accuracy of ≈ 99.99%. The user could update his credentials with success. 
Conclusion: The user could successfully monitor the air quality in real-time which was very useful in case of polluted air so that the user can take preventive measures.

## System Flow
The real-time Air Quality was tracked with the IoT model built using NodeMCU ESP8266 WiFi Module and the MQ135 Gas sensor. All the real-time data was sent to Firebase's Realtime database as well as saved in a CSV file. The data sent on firebase was then sent to the AirGazer Android Application where the user monitors the Air Quality Index in real-time. The app also shows the harmful effects and preventions based on the current AQI and also plots the real-time data. From the CSV file, the dataset was used to train two machine learning models, one to predict the AQI Bucket and the second to predict the Air Quality Index. Dialogflow CX agent was used to build a chatbot that could answer general queries related to Air Quality and Air Pollution.
![System Flow](https://user-images.githubusercontent.com/76195277/235303600-637af0d7-2553-4888-a065-5d6894f5ce69.png)

## Application
![Picture1](https://user-images.githubusercontent.com/76195277/235303613-0d5238fe-7270-43b9-9bbb-2e3c79781f4a.png)
