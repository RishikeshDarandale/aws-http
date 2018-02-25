# aws-http

[![Build Status](https://api.travis-ci.org/RishikeshDarandale/aws-http.svg?branch=master)](https://travis-ci.org/RishikeshDarandale/aws-http)
[![codecov](https://codecov.io/gh/RishikeshDarandale/aws-http/branch/master/graph/badge.svg)](https://codecov.io/gh/RishikeshDarandale/aws-http)
[![Dependency Status](https://www.versioneye.com/user/projects/5a6e9e390fb24f497047f924/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/5a6e9e390fb24f497047f924)

[![Maven Central](https://img.shields.io/maven-metadata/v/http/central.maven.org/maven2/io/github/rishikeshdarandale/aws-http/maven-metadata.xml.svg)](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22aws-http%22)
[![Javadocs](http://www.javadoc.io/badge/io.github.rishikeshdarandale/aws-http.svg)](http://www.javadoc.io/doc/io.github.rishikeshdarandale/aws-http)
[![License MIT](http://img.shields.io/badge/license-MIT-green.svg)](https://github.com/rishikeshdarandale/aws-http/blob/master/LICENSE)


A fluent http client library for aws

### Requirement

* java 8

### Usage

Add the dependency to your project as below:

* maven

```
<dependency>
  <groupId>io.github.rishikeshdarandale</groupId>
  <artifactId>aws-http</artifactId>
  <version>1.0.0</version>
<dependency>
```

* gradle

```
dependencies {
  compile 'io.github.rishikeshdarandale:aws-http:1.0.0'
}
```

Here is example:

```
        MyClass myClassObject = new JdkRequest("https://www.somehost.com")
                .method(RequestMethod.GET)
                .path("/mypath")
                .queryParams("message", "hello*world")
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .body("{}")
                .sign(AwsSignParams("myAccessKey", "MySecretId", "es"))
                .execute()
                .getAs(MyClass.class);
```

`aws-http` is flexible library and you can use either of following http libraries:

* jersey-client

Add `jersey-client` library to your project along with `aws-http` as below:

  * maven

```
<dependency>
  <groupId>io.github.rishikeshdarandale</groupId>
  <artifactId>aws-http</artifactId>
  <version>1.0.0</version>
<dependency>
<dependency>
  <groupId>org.glassfish.jersey.core</groupId>
  <artifactId>jersey-client</artifactId>
  <version>2.26</version>
  <scope>runtime</scope>
<dependency>
```

  * gradle

```
dependencies {
  compile 'io.github.rishikeshdarandale:aws-http:1.0.0'
  runtime 'org.glassfish.jersey.core:jersey-client:2.26'
}
```

Here is example:

```
        MyClass myClassObject = new JerseyRequest("https://www.somehost.com")
                .method(RequestMethod.GET)
                .path("/mypath")
                .queryParams("message", "hello*world")
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .body("{}")
                .sign(AwsSignParams("myAccessKey", "MySecretId", "es"))
                .execute()
                .getAs(MyClass.class);
```

* http client

[WIP]

### Contribute

Welcome! You can absolutely contribute to this project. Please fork the repository, make the necessary changes, validate and create a pull request.

### Verify the build locally

```
./gradlew build
```
