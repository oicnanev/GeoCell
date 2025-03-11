package sdato.geocell.model

import jakarta.persistence.*

@Entity
@Table(name = "content_type")
data class ContentType(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "app_label", nullable = false, length = 100)
    val appLabel: String,

    @Column(name = "model", nullable = false, length = 100)
    val model: String
)
