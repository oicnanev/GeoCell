package sdato.geocell.http

import org.springframework.web.util.UriTemplate
import java.net.URI

object Uris {
    // Base paths
    const val API_BASE = "/api"
    const val HOME = API_BASE

    fun home(): URI = URI(HOME)

    object Users {
        const val CREATE = "$API_BASE/users"
        const val TOKEN = "$API_BASE/users/token"
        const val LOGOUT = "$API_BASE/logout"
        const val GET_BY_ID = "$API_BASE/users/{id}"
        const val HOME = "$API_BASE/me"

        fun byId(id: Int) = UriTemplate(GET_BY_ID).expand(id)

        fun home(): URI = URI(HOME)

        fun login(): URI = URI(TOKEN)

        fun register(): URI = URI(CREATE)
    }

    object Search {
        // TODO: Implement search uris
        // const val LOBBY = "$API_BASE/lobby"
        // const val JOINGAME = "$API_BASE/joingame"
        // const val CHECKMATCH = "$API_BASE/checkmatch"
        // const val LOBBYID = "$API_BASE/lobby/{id}"
        // const val JOINGAMEID = "$API_BASE/joingame/{lobbyGameId}"

        // fun byId(id: Int) = UriTemplate(LOBBYID).expand(id)
    }

    object Operation {
        // TODO: Implement operation uris
        // const val GAME = "$API_BASE/game"
        // const val GIVEUP = "$API_BASE/game/giveup"
        // const val GAMEID = "$API_BASE/game/{id}"

        // fun byId(id: Int) = UriTemplate(GAMEID).expand(id)
    }

    object MobileNetworkOperators {
        // TODO: Implement mobile network operators uris
        // const val STATS = "$API_BASE/stats"
        // fun stats(): URI = URI(STATS)
    }

    object About {
        // TODO: Implement about uris
        const val ABOUT = "$API_BASE/about"

        fun about(): URI = URI(ABOUT)
    }

    object SystemInfo {
        // TODO: Implement system info uris
        const val INFO = "$API_BASE/info"

        fun info(): URI = URI(INFO)
    }
}
