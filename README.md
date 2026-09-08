# Auth Notify API

Project built for authentication/authorization and messaging(events design) learning purposes.

### ⚙️Technologies Used

- Spring Security
- JWT (JJWT)
- OAuth2 Client
- OpenID Connect (OIDC)
- Google OAuth2
- BCrypt
- Spring Data JPA

### 🐇RabbitMQ Section

The messaging service using RabbitMQ can be found on [`Email Sender`](https://github.com/icaro-silva2108/auth-notify-email-sender) repository.

# 🔒Authentication and Authorization

The `API` supports two independent authentication mechanisms:

Local authentication (email and password)
Google Authentication (OAuth2 + OpenID Connect)

Although the authentication flows are different, both converge into a single internal user model and use JWT as the mechanism for authenticating API requests.

## 🏗️Architecture

The application authentication is divided into two stages:

- `Identity authentication`
- `API authorization`

The identity provider (Google) is responsible only for authenticating the user and confirming their identity.

After authentication, the application synchronizes (or creates) a corresponding internal user and generates its own JWT.

From that point, all requests use exclusively the JWT issued by the application.

This ensures the API remains stateless and independent of the authentication provider used during login.

## 📍Local Login

Local login uses:

- `Spring Security`  
- `AuthenticationProvider`  
- `BCryptPasswordEncoder`  
- `JWT`

Flow:

```text
The user submits email and password
                |
                ▼
The AuthenticationProvider validates the credentials
                |
                ▼
The application generates a JWT
                |
                ▼
The client uses the JWT in protected requests
                |
                ▼
The JwtAuthFilter validates the token and authenticates the user in the SecurityContext
```

## 🌐Google Login (OAuth2 + OpenID Connect)

Google login uses the Authorization Code Flow.

Simplified flow:

```text
The client requests authentication with Google
                |
                ▼
The application redirects the user to Google's Authorization Server
                |
                ▼
The user logs in and grants consent
                |
                ▼
Google returns an Authorization Code
                |
                ▼
Spring Security automatically exchanges the Authorization Code for protocol tokens
                |
                ▼
The ID Token is used by OIDC to identify the authenticated user
                |
                ▼
The CustomOidcUserService synchronizes the user with the database
                |
                ▼
The AuthenticationSuccessHandler generates a JWT issued by the application
                |
                ▼
The client uses this JWT for subsequent requests
```

After this process, the application no longer depends on Google-issued tokens to authenticate requests.

## 🔁User Synchronization

The `CustomOidcUserService` is responsible for integrating the user authenticated via Google into the internal application model.

During login:

- it searches for a user by the email provided by Google;
- if the user exists, it reuses the existing record;
- if not, it automatically creates a new user.

This approach allows a single user to use different authentication methods while maintaining a single internal identity.

## 🏢Authentication Providers

Each user maintains a set of associated authentication providers.

Currently supported providers:

- `LOCAL`
- `GOOGLE`

The association is stored using a Set<AuthProvider>, persisted via `@ElementCollection`.

Examples:

| Authentication Type Used | Provider(s)    |
|--------------------------|----------------|
| User created locally     | `LOCAL`          | 
| User created via Google  | `GOOGLE`         | 
| User using both methods  | `LOCAL` & `GOOGLE` |

This model makes it easy to add new providers in the future, such as `GitHub` or `Microsoft`, without structural changes to the User entity.

## 🔑JWT

After any successful authentication, the application generates its own `JWT` containing the necessary information for API authorization.

The token is validated by the `JwtAuthFilter` on every protected request.

The JWT includes information such as:

- user identifier;
- email (subject);
- role;
- account status;
- token id(random `UUID`);
- issued at timestamp;
- expiration timestamp.

## ©️Access Control

Authorization is role-based.

Currently available roles:

- `USER`
- `ADMIN`

Permissions are enforced using Spring Security and authorities derived from the user's role.

## 🚧Session Policy

The API uses SessionCreationPolicy `STATELESS`.

Although the Authorization Code Flow uses internal Spring Security mechanisms during the interaction with Google, no authenticated session is maintained after the JWT is issued.

All subsequent authentication is performed exclusively through the token provided in the Authorization header.
