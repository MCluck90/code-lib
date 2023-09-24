
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Serializable
data class ProjectConfig(val tags: List<String>)

@Serializable
data class ConfigFile(val roots: List<String>, val tagIconsDirectory: String, val projects: MutableMap<String, ProjectConfig>)

object Config {
    private lateinit var configFile: ConfigFile

    private val configFilePath: Path
        get() = Paths.get(System.getenv("HOME"), ".config/codelib.json")

    val roots: List<Path>
        get() = configFile.roots.map { Paths.get(it.replace("~", System.getenv("HOME"))) }

    val tagIconsDirectory: Path
        get() = Paths.get(configFile.tagIconsDirectory.replace("~", System.getenv("HOME")))

    init {
        reload()
    }

    fun reload() {
        if (!Files.exists(configFilePath)) {
            Files.createFile(configFilePath)
            val baseConfigFile = ConfigFile(
                roots = listOf("~/code"),
                tagIconsDirectory = "~/Pictures/CodeLib Icons",
                projects = mutableMapOf()
            )
            Files.write(configFilePath, Json.encodeToString(baseConfigFile).toByteArray())
        }

        val json = Files.readString(configFilePath)
        configFile = Json.decodeFromString(json)
    }

    fun getProject(path: String): ProjectConfig? {
        return configFile.projects[path]
    }

    fun setProject(project: Project) {
        val home = System.getenv("HOME")
        val path = Paths.get(home, ".config/codelib.json")
        configFile.projects[project.path.toString()] = ProjectConfig(tags = project.tags)
        Files.write(path, Json.encodeToString(configFile).toByteArray())
    }
}