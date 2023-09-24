
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.streams.toList

class Project(private val path: Path) {
    val name: String
        get() = path.fileName.toString()

    val iconPath: Path
        get() {
            val tagIconsDir = Config.tagIconsDirectory.toString()

            for (tag in tags) {
                val path = Paths.get(tagIconsDir, "$tag.png")
                if (path.exists()) {
                    return path
                }
            }

            // Couldn't find an image based on tags, show the default
            return Paths.get(tagIconsDir, "Default.png")
        }

    val tags: List<String>
        get() = Config.getProject(path.toString())?.tags ?: listOf()

    val readme: String
        get() {
            val readmePath = Paths.get(path.toString(), "README.md")
            if (readmePath.exists()) {
                return Files.readString(readmePath)
            }
            return ""
        }
}

fun findProjects(): List<Project> {
    fun getProjects(path: Path): List<Project> {
        if (!path.exists()) {
            return listOf()
        }

        val allFiles = Files.list(path).toList()
        val directories = allFiles.filter { it.isDirectory() }
        val files = allFiles.filter { it.isRegularFile() }
        if (files.isNotEmpty()) {
            return listOf(Project(path))
        }

        return directories.flatMap { getProjects(it) }
    }

    return Config.roots.flatMap { getProjects(it) }
}