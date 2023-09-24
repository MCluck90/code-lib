package ui

import Project
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProjectGrid(projects: List<Project>) {
  Box {
    val gridState = rememberLazyGridState()

    LazyVerticalGrid(
      state = gridState,
      columns = GridCells.Adaptive(128.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      items(projects) { ProjectCard(it) }
    }
    VerticalScrollbar(
      modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
      adapter = rememberScrollbarAdapter(scrollState = gridState)
    )
  }
}

@Composable
private fun ProjectCard(project: Project) {
  Card {
    Text(text = project.name)
  }
}