#!/bin/bash

# Development Database Reset Script
# Dieses Script löscht automatisch die MySQL-Datenbank und startet die Container neu
# Verwende es, wenn du Schema-Änderungen an den Models gemacht hast

echo "🗑️  Stoppe alle Container..."
docker-compose down

echo "🗃️  Lösche MySQL Volume (Datenbank wird neu erstellt)..."
docker volume ls | grep mysql_data
if [ $? -eq 0 ]; then
    docker volume rm semester-project-2025-bc-aka-the-canadians_mysql_data
    echo "✅ MySQL Volume gelöscht"
else
    echo "ℹ️  Kein MySQL Volume gefunden"
fi

echo "🏗️  Baue Container neu und starte mit frischer Datenbank..."
docker-compose up --build -d

echo "⏳ Warte auf Backend-Start..."
echo "📝 Backend-Logs werden angezeigt..."
docker logs -f backend 