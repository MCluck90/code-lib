
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.streams.toList

class Project(val name: String) {
}

fun findProjects(): List<Project> {
    fun getProjects(path: Path): List<Project> {
        val allFiles = Files.list(path).toList()
        val directories = allFiles.filter { it.isDirectory() }
        val files = allFiles.filter { it.isRegularFile() }
        if (files.isNotEmpty()) {
            return listOf(Project(path.fileName.toString()))
        }

        return directories.flatMap { getProjects(it) }
    }

    return Config.roots.flatMap { getProjects(it) }
}