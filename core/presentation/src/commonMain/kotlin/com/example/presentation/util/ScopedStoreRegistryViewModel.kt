package com.example.presentation.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore

class ScopedStoreRegistryViewModel : ViewModel() {
    private val stores = mutableMapOf<String, ViewModelStore>()

    fun getOrCreateStore(id: String): ViewModelStore {
        return stores.getOrPut(id) { ViewModelStore() }
    }

    fun clear(id: String) {
        stores.remove(id)?.clear()
    }

    override fun onCleared() {
        stores.values.forEach { it.clear() }
        stores.clear()
    }
}