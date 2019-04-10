#!/usr/bin/groovy

/**
 * Script to generate a static list of insecure resource paths that are accessible without requiring authentication.
 *
 * It contains a combination of paths making up the AngularJS application and any additional resources specified
 * on a property to the groovy-maven-plugin configuration named 'insecurePaths'.
 */

def dist = properties.get("distDir")
def insecurePaths = properties.get("insecurePaths")
def securePaths = [
  "index.html",
  "META-INF",
  "WEB-INF",
]
def resourceList = new File(properties.get("outputFile"))
def resources = new StringBuilder()

insecurePaths.split(",").each {
  resources.append("${it.trim()}${System.lineSeparator()}")
}

new File(dist).eachFileRecurse { file ->
  if (file.isFile() && !securePaths.contains(file.name)) {
    def path = file.path.replace(dist, "")
    resources.append("${path}\n")
  }
}

if (resources.length() == 0) {
  fail("Error generating resourceList.txt. The file has no content!")
}

resourceList.write(resources.toString())
