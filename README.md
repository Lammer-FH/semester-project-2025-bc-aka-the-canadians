# The Campus Lost & Found Service

This repository contains a full-stack web application implementing a Lost & Found service.

Our users can use the service to report found items and view existing reports.

# Tech Stack
- Spring Boot Backend
- Ionic Vue.js Frontend

# API Specification
## CREATE
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
## UPDATE
## DELETE

# Contribution Guidelines
1. Consistent class / variable naming
   - No mixing of snake_case and camelCase
