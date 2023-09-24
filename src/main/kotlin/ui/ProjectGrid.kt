package ui

import Project
import androidx.compose.foundation.Image
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.io.File

@Composable
fun ProjectGrid(projects: List<Project>, onClickProject: (Project) -> Unit) {
    var iconPathToImageBitmap = mutableMapOf<String, ImageBitmap>()

    Box {
        val gridState = rememberLazyGridState()

        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Adaptive(128.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(projects) {
                val iconPath = it.iconPath.toString()
                val icon = if (iconPathToImageBitmap.containsKey(iconPath)) {
                    iconPathToImageBitmap[iconPath]!!
                } else {
                    val imageFile = File(iconPath)
                    val image = loadImageBitmap(imageFile.inputStream())
                    iconPathToImageBitmap[iconPath] = image
                    image
                }
                ProjectCard(project = it, icon = icon, onClick = onClickProject)
            }
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(scrollState = gridState)
        )
    }
}

@Composable
private fun ProjectCard(project: Project, icon: ImageBitmap, onClick: (Project) -> Unit) {
    Card(modifier = Modifier
        .height(160.dp)
        .clickable { onClick(project) }
    ) {
        Column {
            Image(
                painter = BitmapPainter(image = icon),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxHeight(0.65f)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = project.name,
                color = MaterialTheme.colors.onPrimary,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}