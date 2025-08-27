import com.goblenstudios.thrainer.dtos.ReturnUserDto

data class AuthResponseDto(
    val token: String,
    val userId: ReturnUserDto
)