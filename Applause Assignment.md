# Applause Assignment

## The Basics

**Applause-Assignment** is a Spring-Boot application that enables searching testers in **.csv** datasets. This application exposes a **REST** API to perform searches where search results are ranked by experience. Currently, it supports searches for following filters:

| Filter               | Possible Values                                                                                |
| ------------------|----------------------------------------------------------------------------------|
| country | Either `ALL` or combination of any of the following `US, GB, JP`. Multiple values must be seperated by a comma. These values are case-sensitive.                                                      |
| device  | Either `ALL` or combination of any of the following `iPhone 3, Galaxy S3, Galaxy S4, Nexus 4, iPhone 5, iPhone4, iPhone 4S, Droid DNA, Droid Razor, HTC One`. Multiple values must be seperated by a comma. These values are case-sensitive.                                                                         |
***NOTE: If no filters are supplied, it will default to ALL for both device & country.***

## Getting Started

In order to run this application from your favorite IDE, simply run the `Application.java` file contained in the package `com.bytekoder` after resolving maven dependencies.

If you are a command line lover, then by all means go ahead and issue the following command in the project directory: ***(NOTE: This requires maven to be installed)***

```
$ mvn spring-boot:run

```
Once the application is up and running, you should see the following logs:

```
INFO 20755 --- [main] s.w.ClassOrApiAnnotationResourceGrouping : Group for method searchTesters was applause-search-rest
INFO 20755 --- [main] s.b.c.e.t.TomcatEmbeddedServletContainer : Tomcat started on port(s): 8080 (http)
INFO 20755 --- [main] com.bytekoder.Application                : Started Application in 6.496 seconds (JVM running for 7.172)
INFO 20755 --- [main] com.bytekoder.query.ApplauseDataSetup    : Data is ready to be queried...
```
***NOTE: Pay attention to the Tomcat port (8080). You may have to change this if you are already using this port.***

IF you like to simply use browser for testing the service, following is the URL to Tester Search service:

```
http://localhost:8080/search/testers
```
For command line lovers:

```
curl -vk GET http://localhost:8080/search/testers?country=ALL&device=iPhone 5 | jsonpp
```
Alternatively, you could also go to:

```
http://localhost:8080/swagger-ui.html
```
This page provides a ready-to-test API service exposed through swagger. All you need to do is supply the filters.

## Examples


```
http://localhost:8080/search/testers?country=ALL&device=iPhone 3
```
```
curl -X GET --header "Accept: application/json" "http://localhost:8080/search/testers?country=ALL&device=iPhone 3"
```

```
{
  "statusType": "OK",
  "entity": [
    {
      "name": "Miguel Bautista",
      "country": "US",
      "bugs": 35,
      "device": "iPhone 3"
    },
    {
      "name": "Mingquan Zheng",
      "country": "JP",
      "bugs": 19,
      "device": "iPhone 3"
    },
    {
      "name": "Sean Wellington",
      "country": "JP",
      "bugs": 18,
      "device": "iPhone 3"
    }
  ],
  "entityType": "java.util.ArrayList",
  "status": 200,
  "metadata": {}
}
```

For any questions, please contact [Bhavani Shekhawat](bshekhawat@g.harvard.edu) 