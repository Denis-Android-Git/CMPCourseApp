package com.example.presentation.profile.mediapicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerConfigurationSelectionOrdered
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIApplication
import platform.UniformTypeIdentifiers.UTType
import platform.darwin.NSObject
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_group_create
import platform.darwin.dispatch_group_enter
import platform.darwin.dispatch_group_leave
import platform.darwin.dispatch_group_notify
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberImagePickerLauncher(onResult: (PickedImageData) -> Unit): ImagePickerLauncher {
    val scope = rememberCoroutineScope()
    val delegate = remember {
        object : NSObject(), PHPickerViewControllerDelegateProtocol {
            override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
                picker.dismissViewControllerAnimated(true, null)
                val results = didFinishPicking.filterIsInstance<PHPickerResult>()
                val dispatchGroup = dispatch_group_create()
                val imageList = mutableListOf<PickedImageData>()
                for (result in results) {
                    dispatch_group_enter(dispatchGroup)
                    val itemProvider = result.itemProvider
                    val typeIdentifiers = itemProvider.registeredTypeIdentifiers
                    val primaryType = typeIdentifiers.firstOrNull() as? String

                    if (primaryType == null) {
                        dispatch_group_leave(dispatchGroup)
                        continue
                    }
                    val mimeType = UTType.typeWithIdentifier(primaryType)
                        ?.preferredMIMEType
                    if (mimeType == null) {
                        dispatch_group_leave(dispatchGroup)
                        continue
                    }
                    itemProvider.loadDataRepresentationForTypeIdentifier(
                        primaryType
                    ) { nsData, _ ->
                        scope.launch {
                            nsData?.let {
                                val bytes = ByteArray(it.length.toInt())
                                withContext(Dispatchers.Default) {
                                    memcpy(bytes.refTo(0), it.bytes, it.length)
                                }
                                imageList.add(
                                    PickedImageData(
                                        bytes,
                                        mimeType
                                    )
                                )
                            }
                            dispatch_group_leave(dispatchGroup)
                        }
                    }
                    dispatch_group_notify(dispatchGroup, dispatch_get_main_queue()) {
                        scope.launch {
                            imageList.firstOrNull()?.let {
                                onResult(it)
                            }
                        }
                    }
                }
            }
        }
    }
    return remember {
        val controller = PHPickerViewController(
            configuration = PHPickerConfiguration().apply {
                setSelectionLimit(1)
                setFilter(PHPickerFilter.imagesFilter)
                setSelection(PHPickerConfigurationSelectionOrdered)
            }
        )
        controller.delegate = delegate
        ImagePickerLauncher(
            onLaunch = {
                UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(
                    controller,
                    true,
                    null
                )
            }
        )
    }
}