package com.example.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Composable
fun DialogScopedViewmodelScreen(
    isVisible: Boolean,
    scopeId: String = rememberSaveable { Uuid.random().toString() },
    content: @Composable () -> Unit
) {
    val parentOwner = LocalViewModelStoreOwner.current ?: throw IllegalStateException("No ViewModelStoreOwner was provided via LocalViewModelStoreOwner")
    val registry = koinViewModel<ScopedStoreRegistryViewModel>(
        viewModelStoreOwner = parentOwner
    )
    var owner by remember {
        mutableStateOf<ViewModelStoreOwner?>(null)
    }
    LaunchedEffect(isVisible, scopeId) {
        if (isVisible && owner == null) {
            owner = object : ViewModelStoreOwner {
                override val viewModelStore: ViewModelStore
                    get() = registry.getOrCreateStore(scopeId)
            }
        } else if (!isVisible && owner != null) {
            registry.clear(scopeId)
            owner = null
        }
    }
    owner?.let { dialogOwner ->
        CompositionLocalProvider(LocalViewModelStoreOwner provides dialogOwner) {
            content()
        }
    }
}