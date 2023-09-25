import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.exists
import kotlin.io.path.extension
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.streams.toList

class Project(val path: Path) {
    val name: String
        get() = path.fileName.toString()

    val iconPath: Path
        get() {
            val tagIconsDir = Config.tagIconsDirectory.toString()

            // Check the priority tags before running through the list in order
            for (tag in Config.tagIconPriority) {
                if (!tags.contains(tag)) {
                    continue
                }

                val path = Paths.get(tagIconsDir, "$tag.png")
                if (path.exists()) {
                    return path
                }
            }

            for (tag in tags) {
                val path = Paths.get(tagIconsDir, "$tag.png")
                if (path.exists()) {
                    return path
                }
            }

            // Couldn't find an image based on tags, show the default
            return Paths.get(tagIconsDir, "Default.png")
        }

    private var _tags: List<String> = Config.getProject(path.toString())?.tags ?: listOf()
    var tags: List<String>
        get() = _tags
        set(tags) {
            _tags = tags
            Config.setProject(this)
        }

    val readme: String
        get() {
            val readmePath = Paths.get(path.toString(), "README.md")
            if (readmePath.exists()) {
                return Files.readString(readmePath)
            }
            return ""
        }

    val openCommand: String
        get() {
            for ((tag, command) in Config.editors.byTag) {
                if (tags.contains(tag)) {
                    return "$command $path"
                }
            }

            if (Config.editors.default.isNotEmpty()) {
                return "${Config.editors.default} $path"
            }
            return "code $path"
        }

    sealed class TagInferCriteria private constructor() {
        class SpecificFiles(val fileNames: List<String>) : TagInferCriteria()
        class FileExtensions(val extensions: List<String>) : TagInferCriteria()
    }

    fun inferTags(): List<String> {
        fun hasFile(fileName: String): Boolean {
            return Paths.get(path.toString(), fileName).exists()
        }

        fun hasFileWithExtension(extension: String): Boolean {
            return Files.list(path).anyMatch { it.extension == extension }
        }

        val tagToCriteria = mapOf(
            Pair("C", TagInferCriteria.FileExtensions(listOf("c"))),
            Pair("C#", TagInferCriteria.FileExtensions(listOf("cs", "csproj"))),
            Pair("Deno", TagInferCriteria.SpecificFiles(listOf("deno.jsonc"))),
            Pair("F#", TagInferCriteria.FileExtensions(listOf("fs", "fsproj"))),
            Pair("Gleam", TagInferCriteria.SpecificFiles(listOf("gleam.toml"))),
            Pair("Godot", TagInferCriteria.FileExtensions(listOf("tscn"))),
            Pair("Haskell", TagInferCriteria.FileExtensions(listOf("hs"))),
            Pair("HTML", TagInferCriteria.FileExtensions(listOf("html"))),
            Pair("IntelliJ", TagInferCriteria.SpecificFiles(listOf(".idea"))),
            Pair("Kotlin", TagInferCriteria.SpecificFiles(listOf("build.gradle.kts"))),
            Pair("Mint", TagInferCriteria.SpecificFiles(listOf("mint.json"))),
            Pair("Node.js", TagInferCriteria.SpecificFiles(listOf("package.json"))),
            Pair("Perl", TagInferCriteria.FileExtensions(listOf("pl"))),
            Pair("Python", TagInferCriteria.SpecificFiles(listOf("requirements.txt"))),
            Pair("Python", TagInferCriteria.FileExtensions(listOf("py"))),
            Pair("Rust", TagInferCriteria.SpecificFiles(listOf("Cargo.toml"))),
            Pair("Shell", TagInferCriteria.FileExtensions(listOf("sh"))),
            Pair("TypeScript", TagInferCriteria.SpecificFiles(listOf("tsconfig.json", "tsconfig.base.json"))),
            Pair("Unity", TagInferCriteria.SpecificFiles(listOf("ProjectSettings/URPProjectSettings.asset"))),
        )

        val result = mutableListOf<String>()
        for ((tag, criteria) in tagToCriteria) {
            if (result.contains(tag)) {
                continue
            }

            val shouldAddTag = when (criteria) {
                is TagInferCriteria.SpecificFiles -> criteria.fileNames.any { hasFile(it) }
                is TagInferCriteria.FileExtensions -> criteria.extensions.any { hasFileWithExtension(it) }
            }
            if (shouldAddTag) {
                result.add(tag)
            }
        }
        return result
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
            val project = Project(path)
            if (project.tags.isEmpty()) {
                project.tags = project.inferTags()
            }
            return listOf(project)
        }

        return directories.flatMap { getProjects(it) }
    }

    return Config.roots.flatMap { getProjects(it) }
}