# Entwicklungsanleitung

## Voraussetzungen
- Node.js 18+
- Java 17+
- Maven 3.8+
- Docker und Docker Compose (optional)
- MySQL 8.0+

## Lokale Entwicklung

### Frontend
```bash
cd frontend
npm install
npm run dev
```

### Backend
```bash
cd backend
mvn spring-boot:run
```

### Datenbank
```bash
# MySQL muss laufen und die Datenbank muss erstellt sein
mysql -u root -p
CREATE DATABASE campus_lost_found;
```

## Docker Entwicklung
```bash
# Alle Services starten
docker-compose up

# Nur bestimmte Services starten
docker-compose up frontend backend

# Im Hintergrund starten
docker-compose up -d
```

## Tests
```bash
# Frontend Tests
cd frontend
npm run test

# Backend Tests
cd backend
mvn test
```

## Deployment
```bash
# Produktionsbuild erstellen
docker-compose -f docker-compose.prod.yml up --build
```

## Nützliche Befehle
```bash
# Logs anzeigen
docker-compose logs -f

# Services neustarten
docker-compose restart

# Datenbank zurücksetzen
docker-compose down -v
docker-compose up -d
``` 