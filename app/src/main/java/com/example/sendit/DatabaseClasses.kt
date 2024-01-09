import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime

@Serializable
data class User(
    val idU: Int,
    val username: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val dataUr: LocalDateTime,
    val email: String,
    val haslo: String
)

@Serializable
data class NewUser(
    val username: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val dataUr: LocalDateTime,
    val email: String,
    val haslo: String
)

@Serializable
data class EmailWrapper(val email: String)

@Serializable
data class LoggedUserWrapper(val email: String, val password: String)


object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.toString())
    }
    override fun deserialize(decoder: Decoder): LocalDateTime {
        return LocalDateTime.parse(decoder.decodeString())
    }
}
