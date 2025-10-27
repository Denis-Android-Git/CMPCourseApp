package com.example.cmpcourseapp.preview

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.example.designsystem.components.brand.Logo
import com.example.designsystem.components.layouts.MyAdaptiveFormLayout
import com.example.designsystem.theme.ChirpTheme

@Composable
@PreviewLightDark
@PreviewScreenSizes
@Preview(
    device = Devices.NEXUS_10
)
fun MyAdaptiveFormLayoutPreview() {
    ChirpTheme {
        MyAdaptiveFormLayout(
            headerText = "Header Text",
            errorText = "Error Text",
            logo = {
                Logo()
            },
            content = {
                Text(text = "Content")
                Text(text = "More Content")
            }
        )
    }
}