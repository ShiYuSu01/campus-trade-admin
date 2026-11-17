# 健康检查脚本：只查 /health，便于部署后或巡检时快速判断服务状态
param(
    [string]$BaseUrl = "http://localhost:8080",
    [string]$ContextPath = "/campus-trade-admin"
)

$url = $BaseUrl + $ContextPath + "/health"
try {
    $resp = Invoke-WebRequest $url -UseBasicParsing -TimeoutSec 8
    $json = $resp.Content | ConvertFrom-Json
    Write-Host ("应用：" + $json.app + "    状态：" + $json.status) -ForegroundColor Green
    Write-Host ("容器：" + $json.serverInfo + "    JDK：" + $json.javaVersion)
    if ($json.database.ok) {
        Write-Host ("数据库：连通（" + $json.database.costMs + " ms）") -ForegroundColor Green
        exit 0
    }
    Write-Host ("数据库：异常 —— " + $json.database.message) -ForegroundColor Red
    exit 2
} catch {
    Write-Host ("健康检查不可达：" + $url) -ForegroundColor Red
    Write-Host $_.Exception.Message
    exit 1
}
