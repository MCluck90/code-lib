package ui
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

@Composable
fun TagFilter(tags: List<String>, onTagsSelected: (List<String>) -> Unit) {
    val selectedTags = remember { mutableStateListOf<String>() }
    var isFiltersOpen by remember { mutableStateOf(false) }
    fun onFiltersClick() {
        isFiltersOpen = true
    }
    fun onClickTag(tag: String) {
        if (selectedTags.contains(tag)) {
            selectedTags.remove(tag)
        } else {
            selectedTags.add(tag)
        }
        onTagsSelected(selectedTags.toList())
    }

    Button(onClick = ::onFiltersClick) {
        Text("Tags")
        DropdownMenu(
            expanded = isFiltersOpen,
            offset = DpOffset((-16).dp, 8.dp),
            onDismissRequest = { isFiltersOpen = false }
        ) {
            tags.map {
                TagFilterItem(
                    tag = it,
                    selected = selectedTags.contains(it),
                    onClick = ::onClickTag
                )
            }
        }
    }
}

@Composable
private fun TagFilterItem(tag: String, selected: Boolean, onClick: (String) -> Unit) {
    DropdownMenuItem(onClick = { onClick(tag) }) {
        Row(Modifier.fillMaxSize(), Arrangement.spacedBy(8.dp)) {
            Checkbox(checked = selected, onCheckedChange = null)
            Text(text = tag)
        }
    }
}
