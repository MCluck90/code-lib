
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

    fun inferTags(): List<String> {
        fun hasFile(fileName: String): Boolean {
            return Paths.get(path.toString(), fileName).exists()
        }

        fun hasFileWithExtension(extension: String): Boolean {
            return Files.list(path).anyMatch { it.extension == extension }
        }

        val isC = hasFileWithExtension("c")
        val isCSharp = hasFileWithExtension("cs") || hasFileWithExtension("csproj")
        val isDeno = hasFile("deno.jsonc")
        val isFSharp = hasFileWithExtension("fs") || hasFileWithExtension("fsproj")
        val isGleam = hasFile("gleam.toml")
        val isGodot = hasFileWithExtension("tscn")
        val isHaskell = hasFileWithExtension("hs")
        val isHtml = hasFileWithExtension("html")
        val isIntelliJ = hasFile(".idea")
        val isKotlin = hasFile("build.gradle.kts")
        val isMint = hasFile("mint.json")
        val isNodeJs = hasFile("package.json")
        val isPerl = hasFileWithExtension("pl")
        val isPython = hasFile("requirements.txt") || hasFileWithExtension("py")
        val isRust = hasFile("Cargo.toml")
        val isShell = hasFileWithExtension("sh")
        val isTypeScript = hasFile("tsconfig.json") || hasFile("tsconfig.base.json")
        val isUnity = hasFile("ProjectSettings/URPProjectSettings.asset")
        val isUxn = hasFileWithExtension("tal") || hasFile("uxnasm")
        val isVisualStudio = hasFileWithExtension("sln")
        val isWasm = hasFileWithExtension("wat") || hasFileWithExtension("wasm")

        return listOfNotNull(
            if (isC) "C" else null,
            if (isCSharp) "C#" else null,
            if (isDeno) "Deno" else null,
            if (isFSharp) "F#" else null,
            if (isGleam) "Gleam" else null,
            if (isGodot) "Godot" else null,
            if (isHaskell) "Haskell" else null,
            if (isHtml) "HTML" else null,
            if (isIntelliJ) "IntelliJ" else null,
            if (isKotlin) "Kotlin" else null,
            if (isMint) "Mint" else null,
            if (isNodeJs) "Node.js" else null,
            if (isPerl) "Perl" else null,
            if (isPython) "Python" else null,
            if (isRust) "Rust" else null,
            if (isShell) "Shell" else null,
            if (isTypeScript) "TypeScript" else null,
            if (isUnity) "Unity" else null,
            if (isUxn) "uxn" else null,
            if (isVisualStudio) "Visual Studio" else null,
            if (isWasm) "Wasm" else null,
        )
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