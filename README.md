# Code U 2019 Team 9

## Local Development Instructions

### Build

```
mvn package
```

### Start Local Development Server

```
mvn appengine:start
```

### Stop Local Development Server

```
mvn appengine:stop
```

## Deployment Instructions

To deploy a new version of the application, use:

```
mvn package appengine:deploy
```

Note that this command determines which project to
deploy to using the `$GOOGLE_CLOUD_PROJECT` environment
variable, which should allow you to deploy to a personal
test environment if you so choose before deploying to
the shared `sp19-codeu-9-3673` team project.

## Additional Options / Instructions

See the [App Engine Maven Plugin Reference](https://cloud.google.com/appengine/docs/standard/java/tools/maven-reference).
