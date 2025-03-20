import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.dedany.secretgift.domain.usecases.auth.AuthUseCase
import com.dedany.secretgift.domain.usecases.users.UsersUseCase
import com.dedany.secretgift.presentation.login.LoginViewModel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class LoginViewModelTest {

    @get:Rule
    val rule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var authUseCase: AuthUseCase

    @Mock
    private lateinit var usersUseCase: UsersUseCase

    private lateinit var loginViewModel: LoginViewModel

    private lateinit var closeable: AutoCloseable

    @Before
    fun setUp() {
        closeable = MockitoAnnotations.openMocks(this)
        loginViewModel = LoginViewModel(authUseCase, usersUseCase)
    }

    @Test
    fun `when setEmail is called, should update email and call isLoginFormValid with valid data`() {
        // Given
        val email = "test@example.com"
        val password = "Test1234."
        val isValid = true

        //When
        //Defino como se compartará la función cuando se le pase información y deba retornar
        `when`(authUseCase.isLoginFormValid(email.lowercase().trim(), password)).thenReturn(isValid)

        val canDoLoginObserver = mockObserver<Boolean>()
        loginViewModel.canDoLogin.observeForever(canDoLoginObserver)

        loginViewModel.setPassword(password)
        loginViewModel.setEmail(email)

        //Then
        assertEquals(email.lowercase().trim(), loginViewModel.email.value)
        assertEquals(password.trim(), loginViewModel.password.value)
        verify(authUseCase).isLoginFormValid(email.lowercase().trim(), password)
        verify(canDoLoginObserver).onChanged(true)
    }

    @Test
    fun `when setEmail is called, should update email and call isLoginFormValid with invalid data`() {
        // Given
        val email = "test"
        val password = "test"
        val isValid = false

        //When
        `when`(authUseCase.isLoginFormValid(email, password)).thenReturn(isValid)

        val canDoLoginObserver = mockObserver<Boolean>()
        loginViewModel.canDoLogin.observeForever(canDoLoginObserver)

        loginViewModel.setPassword(password)
        loginViewModel.setEmail(email)

        //Then
        assertEquals(email.lowercase().trim(), loginViewModel.email.value)
        assertEquals(password.trim(), loginViewModel.password.value)
        verify(authUseCase).isLoginFormValid(email.lowercase().trim(), password)
        verify(canDoLoginObserver, times(3)).onChanged(false)
    }

    private fun <T> mockObserver(): Observer<T> {
        val observer: Observer<T> = mock(Observer::class.java) as Observer<T>
        return observer
    }

    @After
    fun tearDown(){
        reset(authUseCase, usersUseCase)
        closeable.close()
    }
}

