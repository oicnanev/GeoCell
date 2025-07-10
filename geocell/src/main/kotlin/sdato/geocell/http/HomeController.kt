package sdato.geocell.http

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import sdato.geocell.http.model.OutputHomeModel

@RestController
class HomeController {
    @GetMapping(Uris.HOME)
    fun getHome() = OutputHomeModel()
}
