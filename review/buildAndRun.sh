#!/bin/sh
mvn clean package && docker build -t com.manura.foodapp.reviews/review .
docker rm -f review || true && docker run -d -p 8080:8080 -p 4848:4848 --name review com.manura.foodapp.reviews/review 
