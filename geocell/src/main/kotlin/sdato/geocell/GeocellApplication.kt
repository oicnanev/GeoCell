package sdato.geocell

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EntityScan(basePackages = ["sdato.geocell.model"])
@EnableJpaRepositories(basePackages = ["sdato.geocell.repository"])
class GeocellApplication

fun main(args: Array<String>) {
    runApplication<GeocellApplication>(*args)
}
