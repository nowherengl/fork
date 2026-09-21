package de.mm20.launcher2.ui.launcher.search.apps

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import de.mm20.launcher2.profiles.Profile
import de.mm20.launcher2.search.Application
import de.mm20.launcher2.ui.ktx.onTwoFingerTap
import de.mm20.launcher2.ui.launcher.search.common.grid.GridItem
import de.mm20.launcher2.ui.launcher.search.common.grid.GridResults
import de.mm20.launcher2.ui.launcher.search.common.list.ListItem
import de.mm20.launcher2.ui.launcher.search.common.list.ListResults
import de.mm20.launcher2.ui.locals.LocalGridSettings

fun LazyListScope.AppResults(
    onProfileSelected: (Profile) -> Unit = {},
    profiles: List<Profile> = emptyList(),
    selectedProfile: Profile? = null,
    profileStates: Map<Profile.Type, Profile.State> = emptyMap(),
    showProfileLockControls: Boolean = false,
    onProfileLockChange: ((Profile, Boolean) -> Unit)? = null,
    apps: List<Application>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    highlightedItem: Application? = null,
    columns: Int,
    reverse: Boolean,
    showList: Boolean,
) {
    val selectedProfileType by derivedStateOf {
        selectedProfile?.type ?: Profile.Type.Personal
    }

    val isProfileLocked by derivedStateOf {
        profileStates[selectedProfileType]?.locked == true
    }

    // Tabs permanently hidden
    val before: (@Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit)? = null

    val triggerProfileToggle: (androidx.compose.ui.hapticfeedback.HapticFeedback) -> Unit = { haptic ->
        if (profiles.size > 1) {
            val currentIdx = profiles.indexOf(selectedProfile).coerceAtLeast(0)
            val nextProfile = profiles[(currentIdx + 1) % profiles.size]
            onProfileSelected(nextProfile)
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    if (showList) {
        ListResults(
            key = "apps",
            items = if (isProfileLocked) emptyList() else apps,
            before = before,
            selectedIndex = selectedIndex,
            itemContent = { app, showDetails, index ->
                val haptic = LocalHapticFeedback.current
                ListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onTwoFingerTap { triggerProfileToggle(haptic) },
                    item = app,
                    showDetails = showDetails,
                    onShowDetails = { onSelect(if (it) index else -1) },
                    highlight = highlightedItem?.key == app.key
                )
            },
            reverse = reverse,
        )
    } else {
        GridResults(
            key = "apps",
            items = if (isProfileLocked) emptyList() else apps,
            before = before,
            itemContent = {
                val haptic = LocalHapticFeedback.current
                GridItem(
                    modifier = Modifier.onTwoFingerTap { triggerProfileToggle(haptic) },
                    item = it,
                    showLabels = LocalGridSettings.current.showLabels,
                    highlight = it.key == highlightedItem?.key
                )
            },
            reverse = reverse,
            columns = columns,
        )
    }
}
