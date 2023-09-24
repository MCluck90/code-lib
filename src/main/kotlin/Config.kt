
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Serializable
data class ProjectConfig(val tags: List<String>)

@Serializable
data class ConfigFile(val roots: List<String>, val tagIconsDirectory: String, val projects: Map<String, ProjectConfig>)

object Config {
    private lateinit var configFile: ConfigFile

    val roots: List<Path>
        get() = configFile.roots.map { Paths.get(it.replace("~", System.getenv("HOME"))) }

    val tagIconsDirectory: Path
        get() = Paths.get(configFile.tagIconsDirectory.replace("~", System.getenv("HOME")))

    init {
        reload()
    }

    fun reload() {
        val home = System.getenv("HOME")
        val path = Paths.get(home, ".config/codelib.json")

        if (!Files.exists(path)) {
            Files.createFile(path)
            val baseConfigFile = ConfigFile(
                roots = listOf("~/code"),
                tagIconsDirectory = "~/Pictures/CodeLib Icons",
                projects = mapOf()
            )
            Files.write(path, Json.encodeToString(baseConfigFile).toByteArray())
        }

        val json = Files.readString(path)
        configFile = Json.decodeFromString(json)
    }

    fun getProject(path: String): ProjectConfig? {
        return configFile.projects.get(path)
    }
}