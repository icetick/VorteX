package alex.orobinsk.annotation

import kotlin.annotation.*

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
annotation class ModelBuilder