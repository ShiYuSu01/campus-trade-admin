# 数据库初始化脚本：按顺序执行 schema.sql 与 data.sql
# 用法：powershell -ExecutionPolicy Bypass -File .\scripts\init-db.ps1 -User root -Password 123456
param(
    [string]$User = "root",
    [string]$Password = "123456",
    [string]$Mysql = "mysql"
)

$ErrorActionPreference = "Stop"
$schema = Join-Path $PSScriptRoot "..\sql\schema.sql"
$data = Join-Path $PSScriptRoot "..\sql\data.sql"

if (-not (Test-Path $schema)) { throw "找不到 $schema" }
if (-not (Test-Path $data)) { throw "找不到 $data" }

Write-Host "1/2 执行 schema.sql（建库建表）..."
Get-Content $schema -Raw -Encoding UTF8 | & $Mysql -u$User "-p$Password" --default-character-set=utf8mb4
Write-Host "2/2 执行 data.sql（导入测试数据）..."
Get-Content $data -Raw -Encoding UTF8 | & $Mysql -u$User "-p$Password" --default-character-set=utf8mb4

Write-Host "验证："
& $Mysql -u$User "-p$Password" campus_trade --default-character-set=utf8mb4 -e "SELECT COUNT(*) AS users FROM t_user; SELECT COUNT(*) AS products FROM t_product;"
Write-Host "初始化完成。默认管理员：admin / 123456" -ForegroundColor Green
