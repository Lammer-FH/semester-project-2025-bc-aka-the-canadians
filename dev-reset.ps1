# Development Database Reset Script (PowerShell)
# Dieses Script löscht automatisch die MySQL-Datenbank und startet die Container neu
# Verwende es, wenn du Schema-Änderungen an den Models gemacht hast

Write-Host "🗑️  Stoppe alle Container..." -ForegroundColor Yellow
docker-compose down

Write-Host "🗃️  Lösche MySQL Volume (Datenbank wird neu erstellt)..." -ForegroundColor Yellow
$volumeExists = docker volume ls | Select-String "mysql_data"
if ($volumeExists) {
    docker volume rm semester-project-2025-bc-aka-the-canadians_mysql_data
    Write-Host "✅ MySQL Volume gelöscht" -ForegroundColor Green
} else {
    Write-Host "ℹ️  Kein MySQL Volume gefunden" -ForegroundColor Blue
}

Write-Host "🏗️  Baue Container neu und starte mit frischer Datenbank..." -ForegroundColor Yellow
docker-compose up --build -d

Write-Host "⏳ Warte auf Backend-Start..." -ForegroundColor Yellow
Start-Sleep -Seconds 5

Write-Host "📝 Backend-Logs werden angezeigt..." -ForegroundColor Yellow
docker logs -f backend 