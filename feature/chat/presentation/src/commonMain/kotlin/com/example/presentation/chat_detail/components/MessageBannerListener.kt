package com.example.presentation.chat_detail.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import com.example.presentation.model.MessageUi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun MessageBannerListener(
    lazyListState: LazyListState,
    messages: List<MessageUi>,
    isBannerVisible: Boolean,
    onShowBanner: (topVisibleItemIndex: Int) -> Unit,
    onHide: () -> Unit
) {
    val updatedIsBannerVisible by rememberUpdatedState(isBannerVisible)

    LaunchedEffect(lazyListState, messages) {
        snapshotFlow {
            val info = lazyListState.layoutInfo
            val total = info.totalItemsCount
            val visibleItems = info.visibleItemsInfo

            val oldestVisibleMessageIndex = visibleItems.maxOfOrNull {
                it.index
            } ?: -1

            val isAtOldestMessages = oldestVisibleMessageIndex >= total - 1
            val isAtNewestMessages = visibleItems.any { it.index == 0 }
            MessageBannerScrollState(
                oldestVisibleMessageIndex = oldestVisibleMessageIndex,
                isScrollInProgress = lazyListState.isScrollInProgress,
                isEdgeList = isAtOldestMessages || isAtNewestMessages
            )

        }
            .distinctUntilChanged()
            .collect { (oldestVisibleMessageIndex, isScrollInProgress, isEdgeList) ->
                val shouldShowBanner = isScrollInProgress && !isEdgeList && oldestVisibleMessageIndex >= 0
                when {
                    shouldShowBanner -> onShowBanner(oldestVisibleMessageIndex)
                    !shouldShowBanner && updatedIsBannerVisible -> {
                        delay(1000)
                        onHide()
                    }
                }
            }
    }
}

data class MessageBannerScrollState(
    val oldestVisibleMessageIndex: Int,
    val isScrollInProgress: Boolean,
    val isEdgeList: Boolean
)