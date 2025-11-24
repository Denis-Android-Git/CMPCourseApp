package com.example.presentation.util

import cmpcourseapp.core.presentation.generated.resources.Res
import cmpcourseapp.core.presentation.generated.resources.error_bad_request
import cmpcourseapp.core.presentation.generated.resources.error_conflict
import cmpcourseapp.core.presentation.generated.resources.error_disk_full
import cmpcourseapp.core.presentation.generated.resources.error_forbidden
import cmpcourseapp.core.presentation.generated.resources.error_no_internet
import cmpcourseapp.core.presentation.generated.resources.error_not_found
import cmpcourseapp.core.presentation.generated.resources.error_payload_too_large
import cmpcourseapp.core.presentation.generated.resources.error_request_timeout
import cmpcourseapp.core.presentation.generated.resources.error_serialization
import cmpcourseapp.core.presentation.generated.resources.error_server
import cmpcourseapp.core.presentation.generated.resources.error_service_unavailable
import cmpcourseapp.core.presentation.generated.resources.error_too_many_requests
import cmpcourseapp.core.presentation.generated.resources.error_unauthorized
import cmpcourseapp.core.presentation.generated.resources.error_unknown
import com.example.domain.util.DataError

fun DataError.toUiText(): UiText {
    val resource = when (this) {
        DataError.Local.DISC_FULL -> Res.string.error_disk_full
        DataError.Local.FILE_NOT_FOUND -> Res.string.error_not_found
        DataError.Local.UNKNOWN -> Res.string.error_unknown
        DataError.Remote.BAD_REQUEST -> Res.string.error_bad_request
        DataError.Remote.REQUEST_TIMEOUT -> Res.string.error_request_timeout
        DataError.Remote.INTERNAL_SERVER_ERROR -> Res.string.error_server
        DataError.Remote.NOT_FOUND -> Res.string.error_not_found
        DataError.Remote.UNAUTHORIZED -> Res.string.error_unauthorized
        DataError.Remote.FORBIDDEN -> Res.string.error_forbidden
        DataError.Remote.UNKNOWN -> Res.string.error_unknown
        DataError.Remote.CONFLICT -> Res.string.error_conflict
        DataError.Remote.TOO_MANY_REQUESTS -> Res.string.error_too_many_requests
        DataError.Remote.NO_INTERNET_CONNECTION -> Res.string.error_no_internet
        DataError.Remote.PAYLOAD_TOO_LARGE -> Res.string.error_payload_too_large
        DataError.Remote.SERVER_ERROR -> Res.string.error_server
        DataError.Remote.SERVICE_UNAVAILABLE -> Res.string.error_service_unavailable
        DataError.Remote.SERIALIZATION_ERROR -> Res.string.error_serialization
    }
    return UiText.MyStringResource(resource)
}