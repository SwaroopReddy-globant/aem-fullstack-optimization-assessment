# 📄 Project Decisions – Weather Component (AEM)

---

# 🚀 Deployment

Deploy to Author (localhost:4502)

```
mvn clean install -PautoInstallPackage -Daem.host=localhost -Daem.port=4502 -Daem.username=admin -Daem.password=admin
```

---

# 🧠 Key Technical Decisions

### 1. Context-Aware Configuration (CAConfig)

**Decision:** Used CAConfig instead of OSGi config
**Rationale:**

* Supports multi-tenant & multi-language setups
* Enables config per site / region

**Implementation:**

```
/conf/assessment/sling:configs/
/conf/assessment/us/en/sling:configs/
/conf/assessment/us/ar/sling:configs/
```

**Benefit:**

* Language-specific API keys
* Clean separation of configs

---

### 2. Sling Model Design

**Decision:** Use Sling Model as controller layer

**Responsibilities:**

* Fetch CAConfig
* Build API URL
* Call WeatherService
* Provide safe data to HTL

**Benefit:**

* No business logic in HTL
* Clean MVC separation

---

### 3. Service Layer Architecture

**Decision:** Introduced WeatherService + HttpService

**Flow:**

```
HTL → Model → WeatherService → HttpService → API
```

**Benefit:**

* Separation of concerns
* Reusable HTTP layer
* Testable architecture

---

### 4. HTTP Client Implementation

**Decision:** Apache HttpClient with pooling

**Features:**

* Connection pooling
* Timeout handling
* Response validation

**Benefit:**

* Better performance
* Production-ready HTTP handling

---

### 5. Caching Strategy

**Decision:** In-memory cache using ConcurrentHashMap

**Details:**

* Key = API URL
* TTL = 5 minutes

**Benefit:**

* Reduces API calls
* Improves performance

---

### 6. Fallback Mechanism

**Decision:** Return cached data on failure

**Implementation:**

* If API fails → return last cached response

**Benefit:**

* Improves resilience
* Prevents blank UI

---

### 7. Security Improvements

**Decision:** Removed API exposure from frontend

**Done:**

* API calls moved to backend
* No API key in HTL
---

### 8. Multi-Language Support

**Decision:** Language-based config resolution

**Structure:**

```
/content/assessment/us/en
/content/assessment/us/ar
```

**Config Mapping:**

```
/conf/assessment/us/en → English config
/conf/assessment/us/ar → Arabic config
```

**Benefit:**

* Automatic config resolution per page

---

### 9. Configuration Linking

**Decision:** Used sling:configRef

**Location:**

```
/content/assessment/jcr:content
sling:configRef = /conf/assessment
```

**Benefit:**

* Enables CAConfig resolution

---

### 10. HTL Cleanup

**Decision:** Removed logic from HTL

**HTL now only displays:**

* Page title
* City
* Weather JSON

**Benefit:**

* Secure rendering
* Clean frontend

---

# 🌍 Internationalization & Localization

### Multi-Region Setup

**Decision:** Implemented US region with EN & AR

**Structure:**

```
/content/assessment/us/en/
/content/assessment/us/ar/
