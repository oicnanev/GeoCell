package sdato.geocell.config

object ApiRoutes {
    // Base paths
    private const val API_BASE = "/api"
    const val AUTH_BASE = "$API_BASE/auth"
    const val USER_BASE = "$API_BASE/users"

    // Authentication endpoints
    const val LOGIN = "$AUTH_BASE/login"
    const val REGISTER = "$AUTH_BASE/register"
    const val LOGOUT = "$AUTH_BASE/logout"

    // User management endpoints
    const val CREATE_USER = USER_BASE
    const val GET_USER = "$USER_BASE/{id}"
    const val USER_DELETE = "$USER_BASE/{id}/deactivate"
}
