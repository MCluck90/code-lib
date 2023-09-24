
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import ui.ProjectGrid
import ui.TagFilter
import java.util.*

@Composable
@Preview
fun App() {
    val colors = Colors(
        primary = Color(0.1686f, 0.1882f, 0.2f),
        primaryVariant = Color(0.2686f, 0.2882f, 0.3f),
        secondary = Color(0.8f, 0.8f, 0.8f),
        secondaryVariant = Color(0f, 0.2f, 0.8f),
        surface = Color(0.1059f, 0.1255f, 0.1373f),
        background = Color(0.1059f, 0.1255f, 0.1373f),
        error = Color.Red,
        isLight = false,
        onBackground = Color(0.8f, 0.8f, 0.8f),
        onError = Color.White,
        onPrimary = Color(0.98f, 0.85f, 0.32f),
        onSecondary = Color.Black,
        onSurface = Color(0.8f, 0.8f, 0.8f),
    )

    var selectedTags by remember { mutableStateOf(listOf<String>()) }
    val allProjects = findProjects()
    val projects = allProjects
        .filter {
            if (selectedTags.isEmpty()) {
                true
            } else {
                selectedTags.all { tag -> it.tags.contains(tag) }
            }
        }
        .sortedBy { it.name.lowercase(Locale.getDefault()) }
    val allTags = allProjects.flatMap { it.tags }.toSet().sortedBy { it.lowercase(Locale.getDefault()) }
    var selectedProject by remember { mutableStateOf<Project?>(null) }
    val readmeScrollState = rememberScrollState(0)
    fun onClickProject(project: Project) {
        selectedProject = project
    }
    fun onOpenProject() {
        val openCommand = selectedProject?.openCommand
        if (openCommand != null) {
            Runtime.getRuntime().exec(openCommand)
        }
    }

    MaterialTheme(colors = colors) {
        Surface {
            Row(Modifier.fillMaxSize().padding(16.dp), Arrangement.spacedBy(5.dp)) {
                Column(Modifier.weight(3f)) {
                    TagFilter(tags = allTags, onTagsSelected = { selectedTags = it })
                    ProjectGrid(
                        projects = projects,
                        onClickProject = ::onClickProject
                    )
                }
                Column(Modifier.weight(1f)) {
                    Text(text = selectedProject?.name ?: "")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = selectedProject?.tags?.joinToString(", ") ?: "")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = ::onOpenProject, enabled = selectedProject != null) {
                        Text("Open")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(color = Color(0.1f, 0.1f, 0.1f), modifier = Modifier.fillMaxSize()) {
                        Box {
                            Text(
                                text = selectedProject?.readme ?: "",
                                modifier = Modifier.verticalScroll(readmeScrollState)
                            )
                            VerticalScrollbar(
                                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                                adapter = rememberScrollbarAdapter(scrollState = readmeScrollState)
                            )
                        }
                    }
                }
            }
        }
    }
}

fun main() = application {
    Window(
        title = "CodeLib",
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(width = 1280.dp, height = 768.dp)
    ) {
        App()
    }
}
