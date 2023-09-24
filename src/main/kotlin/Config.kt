
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Paths

@Serializable
data class ConfigFile(val roots: List<String>)

object Config {
    private lateinit var configFile: ConfigFile

    init {
        reload()
    }

    fun reload() {
        val home = System.getenv("HOME")
        val path = Paths.get(home, ".config/codelib.json")

        if (!Files.exists(path)) {
            Files.createFile(path)
            Files.write(path, "{}".toByteArray())
        }

        val json = Files.readString(path)
        configFile = Json.decodeFromString(json)
    }
}