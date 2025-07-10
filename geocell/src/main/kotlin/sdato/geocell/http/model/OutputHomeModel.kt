package sdato.geocell.http.model

class OutputHomeModel {
    val welcome = "Welcome to GeoCell API"
    val description =
        "GeoCell is a web application designed to assist search and rescue teams in locating mobile devices connected " +
            "to cellular networks by identifying their Cell Global Identity (CGI) registration."
    val keyFeatures =
        listOf(
            "Real-time sharing of locations from CGI data activated by mobile devices within operational groups",
            "Rapid dissemination of location data to ground personnel by operation coordinators",
            "Access to operation-related points of interest and team member positions",
            "First Fix service capability",
            "Cellular network analysis and diagnostics",
        )
    val version = "1.0.0"
    val usage = LinkModel()
}

class LinkModel {
    val rel = listOf("collection")
    val href = "https://documenter.getpostman.com/view/24046057/2s9YRGyUc9" // TODO: wrong link
}
