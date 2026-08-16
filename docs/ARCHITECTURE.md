# NyayaSetu — Architecture Overview

## System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     Flutter Mobile App                      │
│              (iOS & Android — Flutter 3.x / Dart)           │
└────────────┬────────────────────────────┬───────────────────┘
             │                            │
             │ REST API calls             │ Direct SDK
             │ Bearer <firebase_id_token> │ (real-time listeners)
             v                            v
┌────────────────────────┐   ┌────────────────────────────────┐
│  Spring Boot Backend   │   │        Firebase Auth           │
│  (Java 17, port 8080)  │   │  (Email/Password + Google)     │
│                        │   └────────────────────────────────┘
│  FirebaseTokenFilter   │
│  ↓ verifies token      │   ┌────────────────────────────────┐
│  Controllers           │──▶│      Cloud Firestore           │
│  Services              │   │   (NoSQL document database)    │
│  Repositories          │   └────────────────────────────────┘
└────────────────────────┘
```

---

## Layer Descriptions

### 1. Flutter App (Client Layer)

**Technology**: Flutter 3.x with Dart
**Dependencies**: firebase_core, firebase_auth, cloud_firestore, provider, go_router, http

**Responsibilities**:
- User interface and navigation
- Firebase Authentication (sign up, sign in, sign out)
- Direct Firestore reads/writes with real-time listeners
- HTTP API calls to Spring Boot backend for business logic
- Local state management (Provider)
- Declarative routing (go_router)

### 2. Spring Boot Backend (API Layer)

**Technology**: Spring Boot 3.2.5, Java 17
**Dependencies**: spring-boot-starter-web, spring-boot-starter-security, firebase-admin 9.3.0

**Responsibilities**:
- REST API endpoints for all business logic
- Firebase ID token verification on every protected request (`FirebaseTokenFilter`)
- Firestore operations via Firebase Admin SDK (bypasses security rules)
- Input validation, slot conflict detection, audit logging
- CORS configuration

**Key components**:
- `FirebaseConfig.java` — initializes Firebase Admin SDK from service account JSON
- `FirebaseTokenFilter.java` — extracts + verifies Bearer token, sets SecurityContext
- `SecurityConfig.java` — stateless Spring Security, public vs. protected endpoints
- `FirebaseUserPrincipal.java` — holds UID, email, custom claims for the request

### 3. Cloud Firestore (Data Layer)

**Technology**: Google Cloud Firestore (NoSQL)

**Collections**:
| Collection | Access |
|---|---|
| `lawyers` | Public read (active only); admin write |
| `advocateBookings` | Owner client or assigned lawyer |
| `legalMatters` | Owner client or assigned lawyer |
| `matterRecords` | Via parent matter access |
| `anonymousChats` | Backend only (Admin SDK) — deny all client direct |
| `anonymousMessages` | Backend only (Admin SDK) — deny all client direct |
| `grievances` | Authenticated create; owner or admin read |

### 4. Firebase Authentication (Identity Layer)

- Email/password and Google sign-in
- ID token (JWT) issued per session, 1-hour expiry, auto-refreshed by SDK
- Custom claims: `admin: true` for admin users
- Token checked by both Spring Boot and Firestore security rules

---

## Authentication Flow

```
User logs in
    │
    ▼
Firebase Auth SDK (Flutter)
    │  returns Firebase ID Token (JWT)
    ▼
Flutter stores token, attaches to every API call
    │  Authorization: Bearer <firebase_id_token>
    ▼
FirebaseTokenFilter (Spring Boot)
    │  1. Extract Bearer token
    │  2. Verify with Firebase Admin SDK
    │  3. Extract UID, email, custom claims
    │  4. Set SecurityContext
    ▼
Controller executes business logic
    │  reads/writes Firestore via Admin SDK
    ▼
Response → Flutter App
```

---

## Key Data Flows

### 1. Booking Creation

```
Client selects lawyer + time slot
    │
    ▼  POST /api/bookings  { lawyerId, mode, scheduledStartsAt, clientNote }
Spring Boot BookingService
    │  1. Verify lawyer is active
    │  2. Validate time: 15min–90days from now
    │  3. Check slot conflict in Firestore (409 if clash)
    │  4. Write advocateBookings document, status=requested
    │  5. Write audit event
    ▼
201 Created → Flutter client
    │
    ▼  Firestore real-time listener (advocate's app)
Advocate sees new booking request instantly
```

### 2. Anonymous Chat

```
Visitor (no auth) provides alias + phoneMask + phoneHash + consent
    │
    ▼  POST /api/anonymous/chats
AnonymousChatService
    │  1. Generate 32-byte random token
    │  2. Store SHA-256(token) as tokenHash — never raw token
    │  3. Store alias, phoneMask, phoneHash, expiresAt = now+24h
    │  4. Return chatId + raw token to client (one time only)
    ▼
Client stores token locally
    │
    ▼  GET /api/anonymous/chats/{id}/messages?token={token}
Service validates SHA-256(token) == tokenHash, checks not expired
    │
    ▼  POST /api/anonymous/chats/{id}/messages?token={token}
Message stored with expiresAt = chat.expiresAt (auto-expires with room)
    │
After 24h: room status=expired, messages return 410 Gone
```

### 3. Matter Lifecycle

```
INTAKE: Client creates matter (title, area, state, language, urgency, opposingParty)
    │  POST /api/matters  →  status=intake, conflictStatus=pending
    ▼
CONFLICT SCREEN: Lawyer reviews opposingParty
    │  PUT /api/matters/{id}/conflict  →  conflictStatus=clear|conflict
    │  If conflict → matter stays intake, no advice records allowed
    ▼
ACTIVE: Lawyer assigned, conflictStatus=clear
    │  Matter records added: note / task / advice / document_request / timeline
    │  Client adds CNR number if court case exists
    ▼
CLOSED: Lawyer or client closes matter
    │  status=closed, all records preserved read-only
```

---

## Security — Defense in Depth

```
Layer 1 — Firebase Auth
    Email/password verification, Google OAuth
    JWT with 1-hour expiry, auto-refreshed

Layer 2 — Spring Boot (FirebaseTokenFilter)
    Verifies token signature via Firebase Admin SDK
    Extracts UID, email, admin custom claim
    Returns 401 for invalid/expired tokens
    Returns 403 for insufficient role

Layer 3 — Firestore Security Rules
    Validates request.auth.uid per document
    Checks ownerUserId, lawyerId, clientUserId fields
    Denies all direct access to anonymousChats/Messages
    Admin checks via request.auth.token.admin == true
```

### Admin Privileges

```javascript
// Set admin claim via Firebase Admin SDK (run once per admin user)
admin.auth().setCustomUserClaims(uid, { admin: true });
```

---

## Deployment

### Development
```
Flutter app (localhost)
    └─▶ http://localhost:8080/api  (Spring Boot)
              └─▶ Firebase Firestore (cloud)
```

### Production
```
Flutter app (iOS / Android)
    └─▶ https://api.nyayasetu.com/api  (Spring Boot on Cloud Run / GKE)
              └─▶ Firebase Firestore (cloud)
Flutter app also talks directly to Firebase Auth SDK
```

### Technology Stack Summary

| Layer | Technology | Purpose |
|---|---|---|
| Mobile Client | Flutter 3.x, Dart | Cross-platform iOS + Android |
| State Management | Provider | Reactive state |
| Routing | go_router | Declarative navigation + auth guards |
| Backend | Spring Boot 3.2.5, Java 17 | REST API + business logic |
| Auth Filter | Spring Security + Firebase Admin SDK | Token verification |
| Database | Cloud Firestore | NoSQL, real-time sync |
| Identity | Firebase Authentication | User auth + custom claims |
| Build | Maven | Backend dependency management |

---

*Last updated: 2026-08-16*
