## 

Run this class from IDE:

```text
com.gym.crm.platform.systemtests.CucumberTest
```

Run all system tests from console:

```bash
mvn -pl system-tests test -DskipSystemTests=false
```

Run component tests only:

```bash
mvn -pl system-tests test -DskipSystemTests=false -Dcucumber.filter.tags="@component"
```

Run integration tests only:

```bash
mvn -pl system-tests test -DskipSystemTests=false -Dcucumber.filter.tags="@integration"
```

Run tests for one endpoint group:

```bash
mvn -pl system-tests test -DskipSystemTests=false -Dcucumber.filter.tags="@auth-login"
mvn -pl system-tests test -DskipSystemTests=false -Dcucumber.filter.tags="@workload-update"
```