# NyayaSetu REST API Reference

Complete API documentation for the NyayaSetu backend.

**Base URL**: `http://localhost:8080/api` (development)  
**Production URL**: Your deployed backend URL

---

## Authentication

All protected endpoints require a Firebase ID token in the Authorization header:

```
Authorization: Bearer <firebase_id_token>
```

### Obtaining a Firebase ID Token

In the Flutter app, after user signs in:

```dart
final user = FirebaseAuth.instance.currentUser;
final token = await user.getIdToken();
```

### Public Endpoints (No Auth Required)

- `GET /api/advocates` — List all active lawyers
- `GET /api/advocates/{id}` — Get lawyer details
- `POST /api/anonymous/*` — Anonymous chat endpoints
- `POST /api/lawyer-applications` — Submit lawyer application

### Admin Endpoints

Endpoints under `/api/admin/**` require the Firebase custom claim `admin: true`.

---

## Response Format

All responses follow a standard envelope:

### Success Response

```json
{
  "success": true,
  "data": { ... },
  "message": "Operation completed successfully"
}
```

### Error Response

```json
{
  "success": false,
  "message": "Description of the error",
  "errors": [ ... ]
}
```

### Common HTTP Status Codes

| Code | Meaning |
|------|---------|
| 200 | Success |
| 201 | Created |
| 400 | Bad Request (validation error) |
| 401 | Unauthorized (missing or invalid token) |
| 403 | Forbidden (insufficient permissions) |
| 404 | Not Found |
| 409 | Conflict (duplicate resource) |
| 500 | Internal Server Error |

---

## Endpoints

---

### Advocates (Lawyers)

#### List All Advocates

```
GET /api/advocates
```

**Auth**: Not required

**Query Parameters**:

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| practiceArea | string | No | Filter by practice area |
| language | string | No | Filter by language spoken |
| state | string | No | Filter by jurisdiction state |
| minRating | double | No | Minimum rating filter |
| page | int | No | Page number (default: 0) |
| size | int | No | Page size (default: 20) |

**Response** `200 OK`:

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": "lawyer_uid_123",
        "name": "Adv. Priya Sharma",
        "email": "priya@example.com",
        "practiceArea": "Family Law",
        "rating": 4.8,
        "yearsOfExperience": 12,
        "pricePerMinute": 50.0,
        "isAvailable": true,
        "languages": ["Hindi", "English"],
        "barCouncilId": "MH/1234/2015",
        "jurisdictionState": "Maharashtra",
        "city": "Mumbai",
        "isVerified": true
      }
    ],
    "totalElements": 45,
    "totalPages": 3,
    "currentPage": 0
  }
}
```

---

#### Get Advocate by ID

```
GET /api/advocates/{id}
```

**Auth**: Not required

**Path Parameters**:

| Parameter | Type | Description |
|-----------|------|-------------|
| id | string | Firebase UID of the lawyer |

**Response** `200 OK`:

```json
{
  "success": true,
  "data": {
    "id": "lawyer_uid_123",
    "name": "Adv. Priya Sharma",
    "email": "priya@example.com",
    "practiceArea": "Family Law",
    "rating": 4.8,
    "yearsOfExperience": 12,
    "pricePerMinute": 50.0,
    "isAvailable": true,
    "languages": ["Hindi", "English"],
    "barCouncilId": "MH/1234/2015",
    "jurisdictionState": "Maharashtra",
    "city": "Mumbai",
    "isVerified": true
  }
}
```

**Error** `404 Not Found`:

```json
{
  "success": false,
  "message": "Lawyer not found with id: lawyer_uid_123"
}
```

---

### Advocate Bookings

#### Create Booking

```
POST /api/bookings
```

**Auth**: Required (client)

**Request Body**:

```json
{
  "advocateId": "lawyer_uid_123",
  "mode": "chat",
  "scheduledStartsAt": "2026-08-20T14:30:00Z",
  "clientNote": "Need help with property dispute"
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| advocateId | string | Yes | Firebase UID of the lawyer |
| mode | string | Yes | `"chat"` or `"call"` |
| scheduledStartsAt | string (ISO 8601) | Yes | Scheduled start time (UTC) |
| clientNote | string | No | Optional note for the lawyer |

**Response** `201 Created`:

```json
{
  "success": true,
  "data": {
    "id": "booking_abc123",
    "advocateId": "lawyer_uid_123",
    "advocateName": "Adv. Priya Sharma",
    "clientId": "client_uid_456",
    "clientEmail": "client@example.com",
    "mode": "chat",
    "scheduledStartsAt": "2026-08-20T14:30:00Z",
    "status": "requested",
    "clientNote": "Need help with property dispute",
    "createdAt": "2026-08-16T10:00:00Z"
  }
}
```

**Errors**:

| Code | Condition |
|------|-----------|
| 400 | Missing required fields or invalid date format |
| 404 | Lawyer not found or not active |
| 401 | Missing or invalid auth token |

---

#### Get My Bookings (Client)

```
GET /api/bookings/my-bookings
```

**Auth**: Required (client)

**Query Parameters**:

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| status | string | No | Filter: `requested`, `accepted`, `completed`, `cancelled` |
| page | int | No | Page number (default: 0) |
| size | int | No | Page size (default: 20) |

**Response** `200 OK`:

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": "booking_abc123",
        "advocateId": "lawyer_uid_123",
        "advocateName": "Adv. Priya Sharma",
        "clientId": "client_uid_456",
        "mode": "chat",
        "scheduledStartsAt": "2026-08-20T14:30:00Z",
        "status": "requested",
        "clientNote": "Need help with property dispute",
        "createdAt": "2026-08-16T10:00:00Z"
      }
    ],
    "totalElements": 5,
    "totalPages": 1,
    "currentPage": 0
  }
}
```

---

#### Get Booking by ID

```
GET /api/bookings/{id}
```

**Auth**: Required (client who owns the booking or the assigned lawyer)

**Response** `200 OK`:

```json
{
  "success": true,
  "data": {
    "id": "booking_abc123",
    "advocateId": "lawyer_uid_123",
    "advocateName": "Adv. Priya Sharma",
    "clientId": "client_uid_456",
    "clientEmail": "client@example.com",
    "mode": "chat",
    "scheduledStartsAt": "2026-08-20T14:30:00Z",
    "status": "accepted",
    "clientNote": "Need help with property dispute",
    "createdAt": "2026-08-16T10:00:00Z"
  }
}
```

**Error** `403 Forbidden`:

```json
{
  "success": false,
  "message": "You do not have access to this booking"
}
```

---

#### Update Booking Status (Lawyer)

```
PUT /api/bookings/{id}/status
```

**Auth**: Required (lawyer)

**Request Body**:

```json
{
  "status": "accepted"
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| status | string | Yes | New status: `accepted`, `completed`, `cancelled` |

**Response** `200 OK`:

```json
{
  "success": true,
  "data": {
    "id": "booking_abc123",
    "status": "accepted",
    "updatedAt": "2026-08-16T11:00:00Z"
  }
}
```

**Errors**:

| Code | Condition |
|------|-----------|
| 400 | Invalid status transition |
| 403 | Not the assigned lawyer |
| 404 | Booking not found |

---

#### Get Lawyer's Bookings

```
GET /api/bookings/lawyer/my-bookings
```

**Auth**: Required (lawyer)

**Query Parameters**:

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| status | string | No | Filter: `requested`, `accepted`, `completed`, `cancelled` |
| page | int | No | Page number (default: 0) |
| size | int | No | Page size (default: 20) |

**Response** `200 OK`:

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": "booking_abc123",
        "clientId": "client_uid_456",
        "clientEmail": "client@example.com",
        "mode": "chat",
        "scheduledStartsAt": "2026-08-20T14:30:00Z",
        "status": "requested",
        "clientNote": "Need help with property dispute",
        "createdAt": "2026-08-16T10:00:00Z"
      }
    ],
    "totalElements": 12,
    "totalPages": 1,
    "currentPage": 0
  }
}
```

---

### Legal Matters

#### Create Legal Matter

```
POST /api/matters
```

**Auth**: Required (client)

**Request Body**:

```json
{
  "title": "Property Dispute - Sector 15",
  "description": "Dispute with neighbor regarding boundary wall construction",
  "category": "Property Law",
  "lawyerId": "lawyer_uid_123",
  "priority": "high"
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| title | string | Yes | Matter title |
| description | string | Yes | Detailed description |
| category | string | Yes | Legal category (e.g., "Property Law", "Family Law") |
| lawyerId | string | No | Assigned lawyer's Firebase UID |
| priority | string | No | `"low"`, `"medium"`, `"high"` (default: `"medium"`) |

**Response** `201 Created`:

```json
{
  "success": true,
  "data": {
    "id": "matter_xyz789",
    "title": "Property Dispute - Sector 15",
    "description": "Dispute with neighbor regarding boundary wall construction",
    "category": "Property Law",
    "ownerUserId": "client_uid_456",
    "ownerEmail": "client@example.com",
    "lawyerId": "lawyer_uid_123",
    "priority": "high",
    "status": "open",
    "createdAt": "2026-08-16T10:00:00Z",
    "updatedAt": "2026-08-16T10:00:00Z"
  }
}
```

---

#### Get My Matters (Client)

```
GET /api/matters/my-matters
```

**Auth**: Required (client)

**Query Parameters**:

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| status | string | No | Filter: `open`, `in_progress`, `resolved`, `closed` |
| category | string | No | Filter by category |
| page | int | No | Page number (default: 0) |
| size | int | No | Page size (default: 20) |

**Response** `200 OK`:

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": "matter_xyz789",
        "title": "Property Dispute - Sector 15",
        "category": "Property Law",
        "lawyerId": "lawyer_uid_123",
        "priority": "high",
        "status": "open",
        "createdAt": "2026-08-16T10:00:00Z",
        "updatedAt": "2026-08-16T10:00:00Z"
      }
    ],
    "totalElements": 3,
    "totalPages": 1,
    "currentPage": 0
  }
}
```

---

#### Get Matter by ID

```
GET /api/matters/{id}
```

**Auth**: Required (owner or assigned lawyer)

**Response** `200 OK`:

```json
{
  "success": true,
  "data": {
    "id": "matter_xyz789",
    "title": "Property Dispute - Sector 15",
    "description": "Dispute with neighbor regarding boundary wall construction",
    "category": "Property Law",
    "ownerUserId": "client_uid_456",
    "ownerEmail": "client@example.com",
    "lawyerId": "lawyer_uid_123",
    "priority": "high",
    "status": "open",
    "createdAt": "2026-08-16T10:00:00Z",
    "updatedAt": "2026-08-16T10:00:00Z"
  }
}
```

**Error** `403 Forbidden`:

```json
{
  "success": false,
  "message": "You do not have access to this matter"
}
```

---

#### Update Matter

```
PUT /api/matters/{id}
```

**Auth**: Required (owner or assigned lawyer)

**Request Body** (all fields optional):

```json
{
  "title": "Updated Title",
  "status": "in_progress",
  "priority": "high"
}
```

| Field | Type | Description |
|-------|------|-------------|
| title | string | Updated title |
| description | string | Updated description |
| status | string | `"open"`, `"in_progress"`, `"resolved"`, `"closed"` |
| priority | string | `"low"`, `"medium"`, `"high"` |
| lawyerId | string | Reassign to different lawyer |

**Response** `200 OK`:

```json
{
  "success": true,
  "data": {
    "id": "matter_xyz789",
    "title": "Updated Title",
    "status": "in_progress",
    "updatedAt": "2026-08-16T12:00:00Z"
  }
}
```

---

#### Delete Matter

```
DELETE /api/matters/{id}
```

**Auth**: Required (owner)

**Response** `200 OK`:

```json
{
  "success": true,
  "message": "Matter deleted successfully"
}
```

---

### Matter Records

#### Add Record to Matter

```
POST /api/matters/{matterId}/records
```

**Auth**: Required (matter owner or assigned lawyer)

**Request Body**:

```json
{
  "title": "Court Notice Received",
  "content": "Received notice from district court for hearing on 2026-09-01",
  "recordType": "document",
  "fileUrl": "https://storage.example.com/notices/notice_001.pdf"
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| title | string | Yes | Record title |
| content | string | No | Detailed content |
| recordType | string | Yes | `"document"`, `"note"`, `"hearing"`, `"evidence"` |
| fileUrl | string | No | URL to attached file |

**Response** `201 Created`:

```json
{
  "success": true,
  "data": {
    "id": "record_rec456",
    "matterId": "matter_xyz789",
    "title": "Court Notice Received",
    "content": "Received notice from district court for hearing on 2026-09-01",
    "recordType": "document",
    "fileUrl": "https://storage.example.com/notices/notice_001.pdf",
    "createdBy": "lawyer_uid_123",
    "createdAt": "2026-08-16T10:30:00Z"
  }
}
```

---

#### Get Records for Matter

```
GET /api/matters/{matterId}/records
```

**Auth**: Required (matter owner or assigned lawyer)

**Query Parameters**:

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| recordType | string | No | Filter by type |
| page | int | No | Page number (default: 0) |
| size | int | No | Page size (default: 20) |

**Response** `200 OK`:

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": "record_rec456",
        "matterId": "matter_xyz789",
        "title": "Court Notice Received",
        "recordType": "document",
        "createdBy": "lawyer_uid_123",
        "createdAt": "2026-08-16T10:30:00Z"
      }
    ],
    "totalElements": 8,
    "totalPages": 1,
    "currentPage": 0
  }
}
```

---

#### Get Record by ID

```
GET /api/matters/{matterId}/records/{recordId}
```

**Auth**: Required (matter owner or assigned lawyer)

**Response** `200 OK`:

```json
{
  "success": true,
  "data": {
    "id": "record_rec456",
    "matterId": "matter_xyz789",
    "title": "Court Notice Received",
    "content": "Received notice from district court for hearing on 2026-09-01",
    "recordType": "document",
    "fileUrl": "https://storage.example.com/notices/notice_001.pdf",
    "createdBy": "lawyer_uid_123",
    "createdAt": "2026-08-16T10:30:00Z"
  }
}
```

---

#### Delete Record

```
DELETE /api/matters/{matterId}/records/{recordId}
```

**Auth**: Required (matter owner or assigned lawyer)

**Response** `200 OK`:

```json
{
  "success": true,
  "message": "Record deleted successfully"
}
```

---

### Anonymous Chat

These endpoints allow users to start a chat without authentication.

#### Start Anonymous Chat

```
POST /api/anonymous/chats
```

**Auth**: Not required

**Request Body**:

```json
{
  "subject": "Property dispute question",
  "category": "Property Law"
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| subject | string | Yes | Chat subject/topic |
| category | string | No | Legal category |

**Response** `201 Created`:

```json
{
  "success": true,
  "data": {
    "id": "chat_anon_001",
    "subject": "Property dispute question",
    "category": "Property Law",
    "status": "open",
    "createdAt": "2026-08-16T10:00:00Z"
  }
}
```

---

#### Send Anonymous Message

```
POST /api/anonymous/chats/{chatId}/messages
```

**Auth**: Not required

**Request Body**:

```json
{
  "content": "My neighbor has built a wall extending into my property by 2 feet.",
  "senderType": "user"
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| content | string | Yes | Message content |
| senderType | string | Yes | `"user"` or `"support"` |

**Response** `201 Created`:

```json
{
  "success": true,
  "data": {
    "id": "msg_001",
    "chatId": "chat_anon_001",
    "content": "My neighbor has built a wall extending into my property by 2 feet.",
    "senderType": "user",
    "createdAt": "2026-08-16T10:01:00Z"
  }
}
```

---

#### Get Anonymous Chat Messages

```
GET /api/anonymous/chats/{chatId}/messages
```

**Auth**: Not required

**Response** `200 OK`:

```json
{
  "success": true,
  "data": [
    {
      "id": "msg_001",
      "chatId": "chat_anon_001",
      "content": "My neighbor has built a wall extending into my property by 2 feet.",
      "senderType": "user",
      "createdAt": "2026-08-16T10:01:00Z"
    },
    {
      "id": "msg_002",
      "chatId": "chat_anon_001",
      "content": "I understand. Have you checked your property deed for the exact boundary measurements?",
      "senderType": "support",
      "createdAt": "2026-08-16T10:05:00Z"
    }
  ]
}
```

---

#### Close Anonymous Chat

```
PUT /api/anonymous/chats/{chatId}/close
```

**Auth**: Not required

**Response** `200 OK`:

```json
{
  "success": true,
  "data": {
    "id": "chat_anon_001",
    "status": "closed",
    "closedAt": "2026-08-16T10:30:00Z"
  }
}
```

---

### Grievances

#### Submit Grievance

```
POST /api/grievances
```

**Auth**: Required

**Request Body**:

```json
{
  "title": "Poor consultation experience",
  "description": "The lawyer did not show up for the scheduled consultation and did not inform me in advance.",
  "category": "service_quality",
  "relatedBookingId": "booking_abc123"
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| title | string | Yes | Grievance title |
| description | string | Yes | Detailed description |
| category | string | Yes | `"service_quality"`, `"billing"`, `"technical"`, `"other"` |
| relatedBookingId | string | No | Related booking ID if applicable |

**Response** `201 Created`:

```json
{
  "success": true,
  "data": {
    "id": "grievance_griev001",
    "title": "Poor consultation experience",
    "description": "The lawyer did not show up for the scheduled consultation...",
    "category": "service_quality",
    "ownerUserId": "client_uid_456",
    "relatedBookingId": "booking_abc123",
    "status": "submitted",
    "createdAt": "2026-08-16T11:00:00Z"
  }
}
```

---

#### Get My Grievances

```
GET /api/grievances/my-grievances
```

**Auth**: Required

**Response** `200 OK`:

```json
{
  "success": true,
  "data": [
    {
      "id": "grievance_griev001",
      "title": "Poor consultation experience",
      "category": "service_quality",
      "status": "submitted",
      "createdAt": "2026-08-16T11:00:00Z"
    }
  ]
}
```

---

#### Get Grievance by ID

```
GET /api/grievances/{id}
```

**Auth**: Required (owner or admin)

**Response** `200 OK`:

```json
{
  "success": true,
  "data": {
    "id": "grievance_griev001",
    "title": "Poor consultation experience",
    "description": "The lawyer did not show up for the scheduled consultation...",
    "category": "service_quality",
    "ownerUserId": "client_uid_456",
    "relatedBookingId": "booking_abc123",
    "status": "submitted",
    "createdAt": "2026-08-16T11:00:00Z",
    "updatedAt": "2026-08-16T11:00:00Z"
  }
}
```

---

### Lawyer Applications

#### Submit Lawyer Application

```
POST /api/lawyer-applications
```

**Auth**: Not required

**Request Body**:

```json
{
  "name": "Adv. Rajesh Kumar",
  "email": "rajesh@example.com",
  "phone": "+919876543210",
  "barCouncilId": "DL/5678/2018",
  "jurisdictionState": "Delhi",
  "city": "New Delhi",
  "practiceArea": "Criminal Law",
  "yearsOfExperience": 8,
  "languages": ["Hindi", "English"],
  "pricePerMinute": 40.0,
  "education": "LLB from Delhi University",
  "bio": "Experienced criminal lawyer with 8 years of practice in Delhi courts."
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| name | string | Yes | Full name |
| email | string | Yes | Email address |
| phone | string | Yes | Phone number |
| barCouncilId | string | Yes | Bar Council enrollment ID |
| jurisdictionState | string | Yes | State of practice |
| city | string | Yes | City of practice |
| practiceArea | string | Yes | Primary practice area |
| yearsOfExperience | int | Yes | Years of experience |
| languages | list[string] | Yes | Languages spoken |
| pricePerMinute | double | Yes | Price per minute of consultation |
| education | string | No | Educational qualifications |
| bio | string | No | Professional biography |

**Response** `201 Created`:

```json
{
  "success": true,
  "data": {
    "id": "application_app001",
    "name": "Adv. Rajesh Kumar",
    "email": "rajesh@example.com",
    "status": "pending",
    "createdAt": "2026-08-16T10:00:00Z"
  },
  "message": "Your application has been submitted. We will review it within 3-5 business days."
}
```

**Error** `409 Conflict`:

```json
{
  "success": false,
  "message": "An application with this email already exists"
}
```

---

### Admin Endpoints

All admin endpoints require the `admin` custom claim on the Firebase token.

#### List All Grievances (Admin)

```
GET /api/admin/grievances
```

**Auth**: Required (admin)

**Response** `200 OK`:

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": "grievance_griev001",
        "title": "Poor consultation experience",
        "ownerUserId": "client_uid_456",
        "status": "submitted",
        "createdAt": "2026-08-16T11:00:00Z"
      }
    ],
    "totalElements": 15,
    "totalPages": 1,
    "currentPage": 0
  }
}
```

---

#### Update Grievance Status (Admin)

```
PUT /api/admin/grievances/{id}/status
```

**Auth**: Required (admin)

**Request Body**:

```json
{
  "status": "resolved",
  "resolutionNote": "Lawyer has been warned and consultation fee refunded."
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| status | string | Yes | `"submitted"`, `"under_review"`, `"resolved"`, `"rejected"` |
| resolutionNote | string | No | Admin's resolution note |

**Response** `200 OK`:

```json
{
  "success": true,
  "data": {
    "id": "grievance_griev001",
    "status": "resolved",
    "resolutionNote": "Lawyer has been warned and consultation fee refunded.",
    "updatedAt": "2026-08-16T15:00:00Z"
  }
}
```

---

#### Manage Lawyer Status (Admin)

```
PUT /api/admin/lawyers/{id}/status
```

**Auth**: Required (admin)

**Request Body**:

```json
{
  "status": "active",
  "isVerified": true
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| status | string | Yes | `"active"`, `"inactive"`, `"suspended"` |
| isVerified | boolean | No | Verification status |

**Response** `200 OK`:

```json
{
  "success": true,
  "data": {
    "id": "lawyer_uid_123",
    "status": "active",
    "isVerified": true,
    "updatedAt": "2026-08-16T15:00:00Z"
  }
}
```

---

#### List Pending Applications (Admin)

```
GET /api/admin/lawyer-applications
```

**Auth**: Required (admin)

**Query Parameters**:

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| status | string | No | Filter: `pending`, `approved`, `rejected` |

**Response** `200 OK`:

```json
{
  "success": true,
  "data": [
    {
      "id": "application_app001",
      "name": "Adv. Rajesh Kumar",
      "email": "rajesh@example.com",
      "practiceArea": "Criminal Law",
      "yearsOfExperience": 8,
      "status": "pending",
      "createdAt": "2026-08-16T10:00:00Z"
    }
  ]
}
```

---

#### Process Lawyer Application (Admin)

```
PUT /api/admin/lawyer-applications/{id}
```

**Auth**: Required (admin)

**Request Body**:

```json
{
  "status": "approved"
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| status | string | Yes | `"approved"` or `"rejected"` |

**Response** `200 OK`:

```json
{
  "success": true,
  "data": {
    "id": "application_app001",
    "status": "approved",
    "lawyerId": "new_lawyer_uid",
    "updatedAt": "2026-08-16T15:00:00Z"
  },
  "message": "Lawyer application approved. Lawyer profile created."
}
```

---

## Rate Limiting

The API implements rate limiting on a per-user basis:

| Endpoint Category | Rate Limit |
|-------------------|------------|
| Anonymous chat | 10 messages per minute |
| Booking creation | 5 per hour |
| General API | 100 requests per minute |

When rate limited, the API returns:

```
HTTP 429 Too Many Requests
```

```json
{
  "success": false,
  "message": "Rate limit exceeded. Please try again later."
}
```

---

## WebSocket / Real-time

For real-time updates on bookings and chat, the Flutter app uses Firestore real-time listeners directly:

```dart
// Listen to booking status changes
Firestore.instance
    .collection('advocateBookings')
    .where('clientId', isEqualTo: currentUserId)
    .snapshots()
    .listen((snapshot) {
  // Handle real-time updates
});
```

The Spring Boot backend does not expose WebSocket endpoints. All real-time functionality is handled through Firestore's built-in real-time sync.

---

**Last Updated**: 2026-08-16
