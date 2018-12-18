# Spring Data JPA Usage

This component adds Spring Data JPA repositories to CUBA platform. This is an experimental feature.

Before usage, install it to the local repository:
* Create database and build the artifact `gradlew startDb createDb build` - you need database for tests.
* Install artifact to a local repository `gradlew publishToMavenLocal`

Add the module to build file as an additional component in your `build.gradle`:
```
dependencies {
    appComponent("com.haulmont.cuba:cuba-global:$cubaVersion")
    appComponent("com.haulmont.addons.cuba.jpa.repositories:cuba-jpa-repositories-global:0.1-SNAPSHOT")
}
```

See [blog post](https://www.cuba-platform.com/blog/spring-query-interfaces-in-cuba) about using this component in more details.

Limitations:
* There might be some issues if you use entity names that may clash with SQL reserved words: order, like, select, etc.
* Custom queries has limited abilities, we do not match method parameter names and custom query parameter names.
* If you want to specify like clause with wildcards and parameters in query annotation, please use concat function and named parameters like in the example: `select ... where ... like concat('?', :name, '%')`
