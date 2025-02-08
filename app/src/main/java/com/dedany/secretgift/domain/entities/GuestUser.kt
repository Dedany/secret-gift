package com.dedany.secretgift.domain.entities

//usuario invitado
data class GuestUser(
   override val id: String,
   override val name: String,
): User
