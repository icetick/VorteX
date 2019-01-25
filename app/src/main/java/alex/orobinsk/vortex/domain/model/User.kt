package alex.orobinsk.vortex.domain.model

import alex.orobinsk.annotation.ModelBuilder

@ModelBuilder
data class User(var userName: String? = "", var password: String? = "", var passwordAuth: String? = "")