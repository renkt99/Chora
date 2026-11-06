package com.craftworks.music.ui.elements.tv

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import com.craftworks.music.R
import com.craftworks.music.ui.playing.tv.MinimalProgressBar
import com.craftworks.music.ui.playing.tv.NextSongButton
import com.craftworks.music.ui.playing.tv.PlayPauseButton
import com.craftworks.music.ui.playing.tv.PreviousSongButton

/**
 * Always-visible "now playing" bar for the TV UI. Shows the current song with
 * previous / play-pause / next controls. Selecting the song opens the full Now Playing screen.
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvMiniPlayer(
    mediaController: MediaController?,
    onOpenNowPlaying: () -> Unit,
    modifier: Modifier = Modifier
) {
    var metadata by remember { mutableStateOf(mediaController?.mediaMetadata) }
    var hasItem by remember { mutableStateOf(mediaController?.currentMediaItem != null) }

    DisposableEffect(mediaController) {
        val listener = object : Player.Listener {
            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                metadata = mediaMetadata
                hasItem = mediaController?.currentMediaItem != null
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                metadata = mediaController?.mediaMetadata
                hasItem = mediaItem != null
            }
        }
        mediaController?.addListener(listener)
        metadata = mediaController?.mediaMetadata
        hasItem = mediaController?.currentMediaItem != null
        onDispose { mediaController?.removeListener(listener) }
    }

    if (mediaController == null || !hasItem) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp, vertical = 8.dp)
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            onClick = onOpenNowPlaying,
            modifier = Modifier.weight(1f),
            shape = ClickableSurfaceDefaults.shape(RoundedCornerShape(12.dp)),
            // Keep the text readable when focused (subtle tint, no white fill) and don't grow
            // the focused area over the neighbouring buttons.
            colors = ClickableSurfaceDefaults.colors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onBackground,
                focusedContainerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f),
                focusedContentColor = MaterialTheme.colorScheme.onBackground,
                pressedContainerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                pressedContentColor = MaterialTheme.colorScheme.onBackground
            ),
            scale = ClickableSurfaceDefaults.scale(focusedScale = 1f)
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AsyncImage(
                    model = metadata?.artworkUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    fallback = painterResource(R.drawable.placeholder),
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Column {
                    Text(
                        text = metadata?.title?.toString() ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = metadata?.artist?.toString() ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        PreviousSongButton(mediaController)
        PlayPauseButton(mediaController)
        NextSongButton(mediaController)
    }

    MinimalProgressBar(
        color = MaterialTheme.colorScheme.onBackground,
        mediaController = mediaController,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
    )
    }
}
