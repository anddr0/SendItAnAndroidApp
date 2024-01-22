import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime


//-------------------------USERS-------------------------

@Serializable
data class User(
    val idU: Int,
    val username: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val dataUr: LocalDateTime,
    val email: String,
    val password: String,
    val description: String
)

@Serializable
data class NewUser(
    val username: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val dataUr: LocalDateTime,
    val email: String,
    val password: String,
    val description: String
)

//-------------------------GOALS-------------------------

@Serializable
data class Goal(
    val idC: Int,
    val idU: Int,
    val opisC: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val dataUt: LocalDateTime,
    val cyklicznosc: Boolean,
    @Serializable(with = LocalDateTimeSerializer::class)
    val termin: LocalDateTime?
)

@Serializable
data class NewGoal(
    val idU: Int,
    val opisC: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val dataUt: LocalDateTime,
    val cyklicznosc: Boolean,
    @Serializable(with = LocalDateTimeSerializer::class)
    val termin: LocalDateTime?
)

@Serializable
data class SubGoal(
    val idP: Int?,
    val idC: Int?,
    var opisPC: String?,
    var zrealizowany: Boolean
)

@Serializable
data class NewSubGoal(
    val idC: Int,
    var opisPC: String?,
    val zrealizowany: Boolean
)

@Serializable
data class finishedSubGoal(
    val idZP: Int?,
    val idP: Int,
    @Serializable(with = LocalDateTimeSerializer::class)
    val dataR: LocalDateTime
)

@Serializable
data class newFinishedSubGoal(
    val idP: Int,
    @Serializable(with = LocalDateTimeSerializer::class)
    val dataR: LocalDateTime
)
//-------------------------------------------------------

object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.toString())
    }
    override fun deserialize(decoder: Decoder): LocalDateTime {
        return LocalDateTime.parse(decoder.decodeString())
    }
}
