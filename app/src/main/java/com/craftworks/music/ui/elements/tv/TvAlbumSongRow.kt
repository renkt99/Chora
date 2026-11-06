package com.craftworks.music.ui.elements.tv

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media.utils.MediaConstants.METADATA_KEY_IS_EXPLICIT
import androidx.media3.common.MediaItem
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import coil3.compose.SubcomposeAsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.craftworks.music.R
import com.craftworks.music.data.model.id
import com.craftworks.music.utils.StringUtils

/**
 * Full-width song row for album pages: track number (or artwork), title, artist and length in
 * aligned columns, so every row lines up and the width is used.
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvAlbumSongRow(
    song: MediaItem,
    showTrackNumber: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val durationText = song.mediaMetadata.durationMs
        ?.takeIf { it > 0 }
        ?.let { StringUtils.formatSeconds((it / 1000).toInt()) }
        ?: ""

    Surface(
        onClick = onClick,
        onLongClick = onLongClick,
        modifier = modifier.fillMaxWidth(),
        shape = ClickableSurfaceDefaults.shape(RoundedCornerShape(12.dp)),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
            contentColor = MaterialTheme.colorScheme.onBackground,
            focusedContainerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.18f),
            focusedContentColor = MaterialTheme.colorScheme.onBackground,
            pressedContainerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.26f),
            pressedContentColor = MaterialTheme.colorScheme.onBackground
        ),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1.02f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (showTrackNumber) {
                Box(modifier = Modifier.width(40.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = song.mediaMetadata.trackNumber?.toString() ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(song.mediaMetadata.artworkUri)
                        .diskCacheKey(song.mediaMetadata.id ?: song.mediaId)
                        .placeholderMemoryCacheKey(song.mediaMetadata.id ?: song.mediaId)
                        .memoryCacheKey(song.mediaMetadata.id ?: song.mediaId)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
            }

            // Title (+ explicit marker) with the artist underneath, both left aligned
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = song.mediaMetadata.title?.toString() ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (song.mediaMetadata.extras?.getBoolean(METADATA_KEY_IS_EXPLICIT) == true) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.rounded_explicit_24),
                            contentDescription = "Explicit"
                        )
                    }
                }
                Text(
                    text = song.mediaMetadata.artist?.toString() ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Length
            Text(
                text = durationText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.End,
                maxLines = 1,
                modifier = Modifier.width(72.dp)
            )
        }
    }
}
