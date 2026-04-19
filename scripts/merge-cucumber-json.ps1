param(
    [string]$RawDirectory = "target/cucumber/raw",
    [string]$OutputFile = "target/cucumber/cucumber.json"
)

mvn -q test-compile exec:java `
  -Dexec.classpathScope=test `
  -Dexec.mainClass=com.bookstore.utils.CucumberJsonMerger `
  -Dexec.args="`"$RawDirectory`" `"$OutputFile`""
