# wos-media-teletext [![Build Status](https://travis-ci.org/stefankruijt/wos-media-teletext.svg?branch=master)](https://travis-ci.org/stefankruijt/wos-media-teletext) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/c7ae5e6e781a4a3b88adc9934db0f7ff)](https://www.codacy.com/app/stefankruijt1991/wos-media-teletext?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=stefankruijt/wos-media-teletext&amp;utm_campaign=Badge_Grade)

Teletext is a television information retrieval service.
This application is capable of keeping news, weather, sport scores, and departure times of train stations up to date 24/7 for a Dutch TV channel.

![Index page of teletext](github-screenshots/page100-index.png?raw=true)
![Departures at train stations](github-screenshots/page715-train-departures.png?raw=true)

### Requirements to develop application
1. Java8 JDK (tested with 1.8.0_111)
2. Maven3 (tested with 3.3.9)

### Requirements to run application

1. Java 8 JRE (tested with 1.8.0_111)
2. MySQL database (To-do: Add database create scripts to Gitub)

After compiling the application teletext-core.war contains an embedded Apache Tomcat 7 application server.

Teletext-mock-server can be started to simulate a teletext server for running automated tests.
The mock-server will automatically be started when you run tests of teletext-core on port 5968 and 5969

### Deployment

Copy teletext-core.war to production server (to /apps/teletext). Do not overwrite the current teletext-core file.
In /apps/teletext is a symbolic link named "teletext", this symbolic link needs to be changed to link to the new file.
(sudo ln -sf teletext-core-version.jar teletext)

Finally, reboot the teletext application by stopping the service.
1. sudo systemctl stop teletext.service
2. sudo systemctl start teletext.service

Check the log files to make sure application is running succesfully. In case of a problem, change symbolic link back to old file and restart teletext service.
