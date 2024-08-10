This project is an example of scalable architecture. This project aims to produce a working example of horizontal scalability for the presentation and application layers within the architecture.

The program is a simple table with a couple of mutable fields. Rows can be added removed and updated.
- The front end is very simple web page which uses no external libraries.
- The "presentation" layer uses a jakarta server to serve the static resources.
- The "application" layer uses a jakarta server to handle API requests, hk2 for service injection and mybatis for database integration.
- The "database" layer used in this project is a postgres 14 database.

Project requirements:
- java 17
- nginx 1.26.1 or later
- postgres 14

Configuration Instructions:
This section pertains to only the config.json file. It is recommended you look at the official documentation for nginx.
URL configuration:
- Server url and port, these config values are self-explanatory.
Layer configuration:
- This value can be one of the following:
  - "presentation": The program will only serve the static content in the "pub" folder.
  - "application": The program will only handle API requests and communicate with the database in an event-driven manner. 
  - "database": The program will apply any new database migrations and then close.
  - "allInOne": The program will perform all functions outlined above.
Backend ID configuration:
- This value should be different in every "application" instance of the program. The backendId value will be shown on the UI so you know which application server nginx has redirected you to for the http request which gets the backendId value.

Database setup:
You will need a postgres 14 instance running at localhost:5432 with a database called "scalability_example" created.
All application layers need to run on the same environment as the postgres database and the root user "postgres" will need to have the password "postgres" because the database connection config is hard-coded.
If you wish to circumvent these constraints by making a code change they can be done in "DataSourceConfigFactory", see the usages of "SimpleConfigUtil" to get an idea of how to easily add your variables to config.json.

Build and deployment instructions:
1. Run "mvn clean package install" to build the project. The built executable and libraries will exist in the maven build directory (usually "target").
2. Copy scalability-*.jar and lib/ from the maven build directory to wherever you wish to deploy.
3. Copy config.json and pub/ (pub/ is only needed for "presentation" and "allInOne" instances of the program) from the project's root directory to wherever you wish to deploy.
4. See the configuration instructions above for the type of deployment of each instance you wish to deploy. If your deployment type is"allInOne" you can skip to step 7.
5. Download Apache nginx and set it up as a load balancer. There is a nginx.conf provided in this project which configures a setup using 4 static content servers and 2 application servers which all run on localhost with different ports.
6. Run nginx.
7. Run each instance of the program you have setup using java: "java -jar scalability-*.jar".
