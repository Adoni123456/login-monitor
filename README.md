# Testing Workflow

Login requests can be tested using Postman.

1. Send a login attempt using `POST /api/login`.
2. The Spring Boot backend validates and stores the login attempt.
3. The system checks for suspicious behaviour.
4. If suspicious behaviour is detected, it is stored in
   `suspicious_activity`.
5. Use `GET /api/login` to retrieve login attempts.
6. Use `GET /api/suspicious` to retrieve detected suspicious activities.
7. The React dashboard displays this information visually.

    Postman
                |
                | POST /api/login
                v
        Spring Boot Backend
                |
                v
          LoginService
                |
        +-------+-------+
        |               |
        v               v
  login_attempts   Suspicious Detection
        |               |
        |               v
        |       suspicious_activity
        |               |
        +-------+-------+
                |
                v
              MySQL
                |
                | GET /api/login
                | GET /api/suspicious
                v
        React Dashboard
# React Dashboard

The React frontend provides a visual dashboard for monitoring login
activity and suspicious behaviour.

The dashboard retrieves data from the Spring Boot REST APIs and displays:

- Total login attempts
- Successful login attempts
- Failed login attempts
- Suspicious activities
- Login activity records
- Suspicious activity details and reasons

The current dashboard is read-only. Login attempts are submitted and
tested through the REST API using Postman.

The data flow is:

React Dashboard
      |
      | GET /api/login
      | GET /api/suspicious
      v
Spring Boot REST API
      |
      v
MySQL Database





# React + Vite

This template provides a minimal setup to get React working in Vite with HMR and some Oxlint rules.

Currently, two official plugins are available:

- [@vitejs/plugin-react](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react) uses [Oxc](https://oxc.rs)
- [@vitejs/plugin-react-swc](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react-swc) uses [SWC](https://swc.rs/)

## React Compiler

The React Compiler is not enabled on this template because of its impact on dev & build performances. To add it, see [this documentation](https://react.dev/learn/react-compiler/installation).

## Expanding the Oxlint configuration

If you are developing a production application, we recommend using TypeScript with type-aware lint rules enabled. Check out the [TS template](https://github.com/vitejs/vite/tree/main/packages/create-vite/template-react-ts) for information on how to integrate TypeScript and Oxlint's TypeScript related rules in your project.
