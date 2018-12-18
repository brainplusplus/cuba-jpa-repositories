# Spring Data JPA Usage

This component adds Spring Data JPA repositories to CUBA platform.

Before usage, install it to the local repository:
* Create database and build the artefact `gradlew startDb createDb build`
* Install artefact to a local repository `gradlew publishToMavenLocal`

Add the module to build file as an additional component:
```
appComponent("com.haulmont.addons.cuba.jpa.repositories:cuba-jpa-repositories-global:0.1-SNAPSHOT")
```

See [blog post](https://www.cuba-platform.com/blog/spring-query-interfaces-in-cuba) about this component in more details.

Limitations:
* There may be issues if you use entity names that may clash with SQL reserved words: order, like, select, etc.
* Custom queries has limited abilities, we do not match method parameter names and custom query parameter names.
* If you want to specify like clause with wildcards and parameters in query annotation, please use concat function and named parameters like in the example: `select ... where ... like concat('?', :name, '%')`
