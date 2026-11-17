# 部署后一键验证脚本（PowerShell 5.1+ / 7）
# 用法：
#   powershell -ExecutionPolicy Bypass -File .\scripts\verify-deploy.ps1
#   powershell -ExecutionPolicy Bypass -File .\scripts\verify-deploy.ps1 -BaseUrl http://172.26.56.12:8080

param(
    [string]$BaseUrl = "http://localhost:8080",
    [string]$ContextPath = "/campus-trade-admin",
    [string]$Username = "admin",
    [string]$Password = "123456"
)

$ErrorActionPreference = "Continue"
$fail = 0

function Check($name, $ok, $detail) {
    if ($ok) { Write-Host ("[PASS] " + $name + "  " + $detail) -ForegroundColor Green }
    else { Write-Host ("[FAIL] " + $name + "  " + $detail) -ForegroundColor Red; $script:fail++ }
}

$healthUrl = $BaseUrl + $ContextPath + "/health"
$loginUrl = $BaseUrl + $ContextPath + "/login"
$dashboardUrl = $BaseUrl + $ContextPath + "/admin/dashboard"

# 1. 健康检查
try { $h = Invoke-WebRequest $healthUrl -UseBasicParsing -TimeoutSec 10 } catch { $h = $null }
Check "健康检查 /health" ($h -ne $null -and $h.Content -match '"status"\s*:\s*"UP"') $healthUrl
if ($h) { Check "数据库连通" ($h.Content -match '"ok"\s*:\s*true') "database.ok" }

# 2. 登录页
try { $l = Invoke-WebRequest $loginUrl -UseBasicParsing -TimeoutSec 10 } catch { $l = $null }
Check "登录页可访问" ($l -ne $null -and $l.StatusCode -eq 200) $loginUrl

# 3. 登录并能进入后台
$session = New-Object Microsoft.PowerShell.Commands.WebRequestSession
try {
    $r = Invoke-WebRequest $loginUrl -Method Post -Body @{ username = $Username; password = $Password } `
        -WebSession $session -MaximumRedirection 0 -UseBasicParsing -TimeoutSec 10 -ErrorAction SilentlyContinue
    Check "登录接口" ($r.StatusCode -eq 302) ("302 -> " + $r.Headers["Location"])
} catch {
    Check "登录接口" $false $_.Exception.Message
}

try {
    $d = Invoke-WebRequest $dashboardUrl -WebSession $session -UseBasicParsing -TimeoutSec 10
    Check "后台看板" ($d.StatusCode -eq 200 -and $d.Content -match "数据看板") $dashboardUrl
} catch {
    Check "后台看板" $false $_.Exception.Message
}

Write-Host ""
if ($fail -eq 0) {
    Write-Host ("全部检查通过：" + $BaseUrl + $ContextPath) -ForegroundColor Green
    exit 0
}
Write-Host ("共 " + $fail + " 项失败，请对照 docs/04-部署运行手册.md 的常见问题排查") -ForegroundColor Red
exit 1
