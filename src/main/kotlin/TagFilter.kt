
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

@Composable
fun TagFilter() {
  var isFiltersOpen by remember { mutableStateOf(false) }
  fun onFiltersClick() {
    isFiltersOpen = true
  }

  Button(onClick = ::onFiltersClick) {
    Text("Tags")
    DropdownMenu(
      expanded = isFiltersOpen,
      offset = DpOffset((-16).dp, 8.dp),
      onDismissRequest = { isFiltersOpen = false }
    ) {
      TagFilterItem("Assembly")
      TagFilterItem("Kotlin")
      TagFilterItem("TypeScript")
    }
  }
}

@Composable
fun TagFilterItem(tag: String) {
  DropdownMenuItem(onClick = {}) {
    Row(Modifier.fillMaxSize(), Arrangement.spacedBy(8.dp)) {
      Checkbox(checked = false, onCheckedChange = null)
      Text(text = tag)
    }
  }
}
