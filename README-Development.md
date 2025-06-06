# Development Guide - Automatisches Datenbank-Management

Dieses Projekt bietet mehrere Optionen für das automatische Management der Datenbank während der Entwicklung, insbesondere bei Schema-Änderungen an den JPA Models.

## 🔄 Automatisches Schema-Reset - Optionen

### **Option 1: Development Profile (Empfohlen)**
Verwendet das `development` Spring-Profile mit automatischem Schema-Reset:

```bash
# Normale docker-compose mit development profile
SPRING_PROFILES_ACTIVE=development docker-compose up --build
```

**Vorteile:**
- ✅ Automatisches `create-drop` - Schema wird bei jedem Start neu erstellt
- ✅ Ausführliche Logging für Debugging
- ✅ Keine manuellen Scripts erforderlich

### **Option 2: Development Docker-Compose**
Spezielle docker-compose.dev.yml ohne persistente MySQL-Volumes:

```bash
# Development-Modus starten
docker-compose -f docker-compose.dev.yml up --build

# Development-Modus stoppen
docker-compose -f docker-compose.dev.yml down
```

**Vorteile:**
- ✅ Komplett isoliert von Production-Setup
- ✅ Keine Volumes - jeder Neustart = frische DB
- ✅ Separate Container-Namen (frontend-dev, backend-dev, etc.)

### **Option 3: Reset-Scripts (Windows/Linux)**

#### Windows (PowerShell):
```powershell
.\dev-reset.ps1
```

#### Linux/Mac (Bash):
```bash
chmod +x dev-reset.sh
./dev-reset.sh
```

**Vorteile:**
- ✅ Vollständige Kontrolle über den Reset-Prozess
- ✅ Automatisches Volume-Löschen
- ✅ Live-Logs vom Backend

## 🎯 Wann welche Option verwenden?

### **Bei Schema-Änderungen an Models:**
```java
// Wenn du Änderungen machst wie:
@Entity
public class User {
    // Neue Felder hinzufügen
    @Column(name = "new_field")
    private String newField;
    
    // Foreign Key Namen ändern
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_new_name"))
    private User user;
}
```

**Empfohlenes Vorgehen:**
1. **Development Profile** verwenden für kontinuierliche Entwicklung
2. **Reset-Scripts** verwenden für manuelle Kontrolle
3. **Development Docker-Compose** für komplett isolierte Tests

### **Für tägliche Entwicklung:**
```bash
# Einmal setup, dann normal arbeiten
export SPRING_PROFILES_ACTIVE=development
docker-compose up -d
```

### **Für Bug-Reproduktion:**
```bash
# Komplett sauberer Zustand
./dev-reset.ps1
# oder
docker-compose -f docker-compose.dev.yml up --build
```

## ⚠️ **Wichtige Hinweise**

### **Development vs Production:**
- **Development**: `hibernate.ddl-auto: create-drop` 
- **Production**: `hibernate.ddl-auto: validate` oder `update`

### **Daten-Verlust:**
- ❌ **Development Profile**: Alle Daten gehen bei jedem Neustart verloren
- ❌ **Reset Scripts**: Löschen alle Daten im MySQL Volume
- ✅ **Normal docker-compose**: Daten bleiben persistent

### **Container-Namen:**
- **Normal**: `backend`, `frontend`, `mysql`
- **Development**: `backend-dev`, `frontend-dev`, `mysql-dev`

## 🚀 **Quick Start Commands**

```bash
# Für neue Schema-Features (empfohlen)
SPRING_PROFILES_ACTIVE=development docker-compose up --build -d

# Für isolierte Tests
docker-compose -f docker-compose.dev.yml up --build -d

# Für manuellen Reset
./dev-reset.ps1  # Windows
./dev-reset.sh   # Linux/Mac

# Production-like mit persistenten Daten
docker-compose up --build -d

# Für schnellen Neustart der Development-Umgebung
docker-compose -f docker-compose.dev.yml down
docker-compose -f docker-compose.dev.yml up --build -d
```

## 🔧 **Konfiguration anpassen**

### Hibernate DDL-Modi:
- `create`: Erstellt Schema bei Start, löscht nicht bei Stop
- `create-drop`: Erstellt bei Start, löscht bei Stop  
- `update`: Updated Schema, fügt neue Tabellen/Spalten hinzu
- `validate`: Validiert nur, macht keine Änderungen

### Environment Variables:
```yaml
environment:
  - HIBERNATE_DDL_AUTO=create-drop    # Für Development
  - SHOW_SQL=true                     # SQL-Queries anzeigen  
  - FORMAT_SQL=true                   # SQL formatiert anzeigen
  - LOG_LEVEL=DEBUG                   # Ausführliche Logs
```

Diese Konfiguration stellt sicher, dass bei jeder Schema-Änderung die Datenbank automatisch mit dem neuen Schema neu erstellt wird! 🎯 