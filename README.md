# 📊 SmartApply - Bewerbungsmanagement-System

Ein modernes Full-Stack Bewerbungstracker-System mit Spring Boot, PostgreSQL und modernem UI-Design.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)
![Gradle](https://img.shields.io/badge/Gradle-8.x-blue)

## 🎯 Projektübersicht

SmartApply ist eine professionelle Webanwendung zur Verwaltung und Nachverfolgung von Bewerbungsprozessen. Das System bietet eine intuitive Benutzeroberfläche mit modernem Design (inspiriert von Personio) und umfassende Funktionen zur Bewerbungsverwaltung.

## ✨ Features

### Bewerbungsverwaltung
- ✅ Vollständige CRUD-Operationen für Bewerbungen
- ✅ Status-Tracking (Gesendet, Interview, Zusage, Absage, etc.)
- ✅ Suchfunktion nach Unternehmen und Position
- ✅ Deadline-Überwachung
- ✅ Persistente Datenspeicherung mit PostgreSQL

### Dashboard & Visualisierung
- 📊 Übersichtliches Dashboard mit Statistiken
- 📈 Status-Verteilung auf einen Blick
- 🎨 Modernes, responsives UI-Design
- 🔍 Filter- und Suchfunktionen
- 💳 Card-basiertes Layout für bessere Übersicht

### Technische Features
- 🔄 RESTful API-Design
- 🗄️ JPA/Hibernate ORM
- 📅 Automatische Zeitstempel (created_at, updated_at)
- 🎨 Thymeleaf Template Engine
- 🔐 Vorbereitet für Spring Security Integration

## 🛠️ Technologie-Stack

### Backend
- **Java 17** - Programmiersprache
- **Spring Boot 3.x** - Application Framework
- **Spring Data JPA** - Datenpersistenz
- **Hibernate** - ORM Framework
- **PostgreSQL** - Relationale Datenbank
- **Lombok** - Boilerplate-Reduktion

### Frontend
- **Thymeleaf** - Server-side Template Engine
- **HTML5/CSS3** - Strukturierung und Styling
- **Modernes UI Design** - Personio-inspiriertes Interface
- **Responsive Design** - Mobile-First Ansatz

### Build & Tools
- **Gradle** - Build-Tool
- **IntelliJ IDEA** - IDE
- **Git** - Versionskontrolle

## 📦 Installation & Setup

### Voraussetzungen
- Java 17 oder höher
- PostgreSQL 15+
- Gradle (oder nutze gradle wrapper)
- IntelliJ IDEA (empfohlen)

### Datenbank Setup

1. PostgreSQL installieren und starten

2. Datenbank erstellen:
```sql
CREATE DATABASE smartapply;
CREATE USER smartapply_user WITH PASSWORD 'dein_passwort';
GRANT ALL PRIVILEGES ON DATABASE smartapply TO smartapply_user;
```

3. `application.properties` anpassen:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/smartapply
spring.datasource.username=smartapply_user
spring.datasource.password=dein_passwort
```

### Application Setup

1. Repository klonen:
```bash
git clone https://github.com/DEIN-USERNAME/SmartApply.git
cd SmartApply
```

2. Dependencies installieren:
```bash
./gradlew build
```

3. Anwendung starten:
```bash
./gradlew bootRun
```

4. Browser öffnen: `http://localhost:8080`

## 📁 Projektstruktur

```
SmartApply/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/smartapply/
│   │   │       ├── SmartApplyApplication.java
│   │   │       ├── controller/
│   │   │       │   └── ApplicationController.java
│   │   │       ├── model/
│   │   │       │   ├── Application.java
│   │   │       │   └── ApplicationStatus.java
│   │   │       ├── repository/
│   │   │       │   └── ApplicationRepository.java
│   │   │       └── service/
│   │   │           └── ApplicationService.java
│   │   │
│   │   └── resources/
│   │       ├── templates/
│   │       │   ├── applications/
│   │       │   │   ├── list.html
│   │       │   │   ├── form.html
│   │       │   │   └── detail.html
│   │       │   └── layout/
│   │       │       └── base.html
│   │       ├── static/
│   │       │   └── css/
│   │       │       └── style.css
│   │       └── application.properties
│   │
│   └── test/
│       └── java/
│
├── build.gradle
├── settings.gradle
└── README.md
```

## 🚀 Verwendung

### Neue Bewerbung anlegen
1. Klicke auf "Neue Bewerbung"
2. Fülle das Formular aus:
   - Unternehmensname
   - Position
   - Status
   - Bewerbungsdatum
   - Optional: Deadline
3. Speichern

### Bewerbungen verwalten
- **Anzeigen**: Klick auf eine Bewerbung für Details
- **Bearbeiten**: Edit-Button in der Detailansicht
- **Status ändern**: Dropdown-Menü in der Detailansicht
- **Löschen**: Delete-Button in der Detailansicht
- **Suchen**: Suchfeld in der Übersicht

## 💾 Datenmodell

### Application Entity
- `id` (Long) - Primary Key
- `companyName` (String) - Unternehmensname
- `position` (String) - Stellenbezeichnung
- `status` (ApplicationStatus) - Bewerbungsstatus
- `applicationDate` (LocalDate) - Bewerbungsdatum
- `deadline` (LocalDate) - Optional: Bewerbungsdeadline
- `createdAt` (LocalDateTime) - Erstellungszeitpunkt
- `updatedAt` (LocalDateTime) - Letzte Änderung

### ApplicationStatus Enum
- `SENT` - Gesendet
- `INTERVIEW` - Interview
- `OFFER` - Zusage
- `REJECTED` - Absage
- `IN_PROGRESS` - In Bearbeitung
- `WITHDRAWN` - Zurückgezogen

## 🎨 UI Features

- **Modernes Card-Design**: Übersichtliche Darstellung
- **Status-Badges**: Farbcodierte Status-Anzeige
- **Responsive Layout**: Optimiert für Desktop und Mobile
- **Intuitive Navigation**: Klare Benutzerführung
- **Personio-inspiriert**: Professionelles Corporate Design

## 🔮 Geplante Erweiterungen

- [ ] Dashboard mit Statistiken und Charts
- [ ] Export-Funktionen (PDF, Excel)
- [ ] E-Mail-Benachrichtigungen
- [ ] Kalender-Integration
- [ ] Dokumenten-Upload (Lebenslauf, Anschreiben)
- [ ] Notizen und Kontakte zu Bewerbungen
- [ ] Multi-User Support mit Authentication

## 🧪 Testing

Tests ausführen:
```bash
./gradlew test
```

## 📝 API Endpoints

- `GET /applications` - Liste aller Bewerbungen
- `GET /applications/new` - Formular für neue Bewerbung
- `POST /applications` - Bewerbung speichern
- `GET /applications/{id}` - Bewerbungsdetails
- `GET /applications/{id}/edit` - Bearbeitungsformular
- `POST /applications/{id}` - Bewerbung aktualisieren
- `POST /applications/{id}/delete` - Bewerbung löschen
- `POST /applications/{id}/status` - Status ändern

## 👤 Autor

**David**

Entwickelt als Portfolio-Projekt zur Demonstration von:
- Full-Stack Java Development
- Spring Boot Framework
- RESTful API Design
- JPA/Hibernate ORM
- PostgreSQL Datenbankdesign
- Moderne Frontend-Entwicklung
- Clean Code & Best Practices

## 📄 Lizenz

Dieses Projekt wurde für Bewerbungszwecke erstellt.

---

**Kontakt**: Für Fragen oder Feedback zu diesem Projekt stehe ich gerne zur Verfügung.

**Status**: ✅ Aktiv in Entwicklung
