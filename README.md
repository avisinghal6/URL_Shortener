# URL_Shortener
We have taken upon the challenge of building a URL Shortener service with the intention of scalability at the top. This application serves the purpose with the help of SpringBoot and Bigtable. Google OAuth has been securely implemented to handle the recovery of user data. The app extends this as an attempt to compete with the market through unique features such as Barcode Generation and Magic AI built on top of ChatGPT.

Note: There is a CI pipeline in place that tests for build. While it provides reliability to the application, it does not mean that the application will run on your system.
## Pre-requisites

 - You'll need to acquire or create an application.properties file in ```url-shortener-backend/src/main/resources/application.properties``` to get all the required API keys and variables to act upon
 - This app requires Java v17, maven v3.8.8

## How to run

```
mvn dependency:resolve
mvn spring-boot:run
```

## Major contributors

The initial project was built by Ishita Kundaliya, Avi Singhal, and Hemanth Kumar Jayakumar as part of our project in COMP539 at Rice University. Additional thanks to Prof. Alexei Stolboushkin, Alex Hochstein, and Heather McIntyre for helping us frame the design of the project. 
