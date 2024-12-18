A sample repo that publishes Apollo metadata data from the `schema` and `fragments` module and re-uses those publications from the `consumer` module:

```
./gradlew publishAllPublicationsToPluginTestRepository
./gradlew build
```