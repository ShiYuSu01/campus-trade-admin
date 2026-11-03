param(
    [string]$TomcatHome = "D:\apache-tomcat-11.0.22",
    [string]$JavaHome = "D:\Program Files\Java\jdk-25.0.4.1",
    [int]$Port = 8080
)

$ErrorActionPreference = "Stop"
$project = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
$base = Join-Path $project ".tomcat"
$war = Join-Path $project "target\campus-trade-admin.war"

Write-Host "==> Project: $project"
Write-Host "==> Tomcat : $TomcatHome"
Write-Host "==> Java   : $JavaHome"
Write-Host "==> Base   : $base"

if (-not (Test-Path (Join-Path $TomcatHome "bin\catalina.bat"))) {
    throw "Tomcat not found: $TomcatHome\bin\catalina.bat"
}
if (-not (Test-Path (Join-Path $JavaHome "bin\java.exe"))) {
    throw "JDK not found: $JavaHome\bin\java.exe"
}
if (-not (Test-Path $war)) {
    throw "WAR not found: $war. Run scripts\build.bat first."
}

foreach ($dir in @("conf", "logs", "temp", "webapps", "work", "bin")) {
    New-Item -ItemType Directory -Force (Join-Path $base $dir) | Out-Null
}

if (-not (Test-Path (Join-Path $base "conf\web.xml"))) {
    Copy-Item (Join-Path $TomcatHome "conf\*") (Join-Path $base "conf") -Recurse -Force
}

Copy-Item (Join-Path $PSScriptRoot "tomcat-server.xml") (Join-Path $base "conf\server.xml") -Force
if ($Port -ne 8080) {
    $serverFile = Join-Path $base "conf\server.xml"
    $server = Get-Content $serverFile -Raw
    $server = $server -replace 'port="8080"', "port=`"$Port`""
    Set-Content $serverFile $server -Encoding UTF8
}

$localhostDir = Join-Path $base "conf\Catalina\localhost"
New-Item -ItemType Directory -Force $localhostDir | Out-Null
Get-ChildItem $localhostDir -File -ErrorAction SilentlyContinue | Remove-Item -Force

# The bundled catalina.bat / setclasspath.bat have a hard-coded JRE_HOME line.
# Copy them locally and remove that line so JAVA_HOME is used.
$catalinaLines = Get-Content (Join-Path $TomcatHome "bin\catalina.bat")
$catalinaLines = $catalinaLines | Where-Object { $_ -notmatch '^\s*set\s+JRE_HOME=' }
$catalinaLines = $catalinaLines -replace [regex]::Escape('%CATALINA_HOME%\bin\setclasspath.bat'), '%CATALINA_BASE%\bin\setclasspath-local.bat'
Set-Content (Join-Path $base "bin\catalina-local.bat") $catalinaLines -Encoding ASCII

$setclasspathLines = Get-Content (Join-Path $TomcatHome "bin\setclasspath.bat")
$setclasspathLines = $setclasspathLines | Where-Object { $_ -notmatch '^\s*set\s+JRE_HOME=' }
Set-Content (Join-Path $base "bin\setclasspath-local.bat") $setclasspathLines -Encoding ASCII

$appDir = Join-Path $base "webapps\campus-trade-admin"
if (Test-Path $appDir) {
    Remove-Item $appDir -Recurse -Force
}
Copy-Item $war (Join-Path $base "webapps\campus-trade-admin.war") -Force

Write-Host "==> Deployed. Start with: scripts\start-server.bat"
Write-Host "==> URL: http://localhost:$Port/campus-trade-admin/"
