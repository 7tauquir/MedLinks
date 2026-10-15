# MedLinks Frontend

React/Vite frontend built against the supplied MedLinks 1.0 Spring Boot backend.

## Backend contract used

- `POST /api/auth/register`
- `POST /api/auth/login`
- Medicine CRUD/search/category/seller endpoints
- Cart add/get/update/remove/clear
- Orders: place, my orders, seller orders, status, cancel
- JWT sent as `Authorization: Bearer <token>`

The backend source uses the role `WHOLESALER` (the documentation calls this Stock Seller), so the frontend uses `WHOLESALER` to match the actual Java enum.

## Run

1. Start the Spring Boot backend on `http://localhost:8080`.
2. Install packages:
   `npm install`
3. Start:
   `npm run dev`
4. Open `http://localhost:5173`.

Vite proxies `/api` to the backend in development.

## Note about Admin

The supplied project contains ADMIN security rules but no AdminController/Admin service/DTO implementation. The Admin page is therefore a deliberate scaffold rather than fake API data.
