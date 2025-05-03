# API Specification Campus Lost & Found
## CREATE
- **POST** `/users`
  Create a new user.
- **POST** `/reports`
  Create a new lost/found report.
- **POST** `/items`
  Create a new item record.
- **POST** `/locations`
  Create a new location entry.
  
---

## READ
- **GET** `/users`
  Get all users.
- **GET** `/users/:id`
  Get a specific user.
- **GET** `/users/:id/reports`
  Get all reports from a user.
- **GET** `/reports`
  Get all reports.
- **GET** `/reports/:id`
  Get a specific report.
- **GET** `/items`
  Get all items.
- **GET** `/items/:id`
  Get a specific item.
- **GET** `/locations`
  Get all locations.
- **GET** `/locations/:id`
  Get a specific location.
- **GET** `/locations/:id/items`
  Get all items in a location.

---

## UPDATE
- **PUT** `/users/:id`
  Update user details.
- **PUT** `/reports/:id`
  Update an existing report.
- **PUT** `/items/:id`
  Update item information.
- **PUT** `/locations/:id`
  Update location information.

---

## DELETE
- **DELETE** `/users/:id`
  Delete a user.
- **DELETE** `/reports/:id`
  Delete a report.
- **DELETE** `/items/:id`
  Delete an item.
- **DELETE** `/locations/:id`
  Delete a location.
