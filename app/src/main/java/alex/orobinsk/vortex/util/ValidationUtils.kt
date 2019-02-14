package alex.orobinsk.vortex.util

import androidx.lifecycle.MutableLiveData

private val minimumLength = 2

infix fun MutableLiveData<String>.isValidAs(type: ValidationType): Boolean {
    var validationResult = false
    this.value?.let { value ->
        validationResult = when (type) {
            ValidationType.PASSWORD -> !value.isEmpty() && value.length > minimumLength
            ValidationType.USERNAME -> !value.isEmpty() && value.length > minimumLength
            ValidationType.EMAIL -> android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches()
            ValidationType.ANDROID_ID -> !value.isEmpty()
        }
    } ?: run {
        validationResult = false
    }
    return validationResult
}

enum class ValidationType {
    PASSWORD, EMAIL, USERNAME, ANDROID_ID
}